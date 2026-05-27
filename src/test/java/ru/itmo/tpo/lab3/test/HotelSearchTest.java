package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.HotelSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-04: Поиск отелей.
 * Актор: Гость.
 */
public class HotelSearchTest extends BaseTest {

    private HotelSearchPage hotelPage;

    @BeforeEach
    void openHotelForm() {
        hotelPage = new HotelSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Форма поиска отелей содержит поле города и кнопку поиска")
    void formHasMandatoryFields() {
        assertTrue(hotelPage.isDestinationInputVisible(),
                "Нет поля выбора города/направления");
        assertTrue(hotelPage.isSearchButtonVisible(),
                "Нет кнопки поиска отелей");
    }

    @Test
    @DisplayName("Ввод в поле города показывает выпадающие подсказки")
    void destinationAutocompleteShowsSuggestions() {
        hotelPage.typeDestination("Сочи");
        assertTrue(hotelPage.isSuggestionsVisible(),
                "Должны отображаться подсказки автокомплита при вводе названия города");
    }

    @Test
    @DisplayName("Полный сценарий: выбор города и нажатие 'Найти' открывает результаты")
    void hotelSearchByCityOpensResults() {
        hotelPage
                .typeDestination("Сочи")
                .chooseFirstSuggestion()
                .clickSearch();
        assertTrue(hotelPage.isResultsOpened(),
                "После выбора города и нажатия 'Найти' должна открыться страница результатов");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи поиска отелей")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: поиск с пустым полем города не запускает выдачу")
        void emptyDestination_doesNotProceed() {
            String before = driver.getCurrentUrl();
            hotelPage.clickSearch();
            assertTrue(hotelPage.stayedOnSearchForm(before),
                    "С пустым полем города поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: ввод заведомо несуществующего города не даёт реальных подсказок")
        void nonExistentCity_doesNotShowMeaningfulSuggestions() {
            hotelPage.typeDestination("Кфтыкчоувапролд");
            int count = hotelPage.getSuggestionsCount();
            assertTrue(count == 0,
                    "Для бессмысленного запроса подсказок быть не должно, получено: " + count);
        }

        @Test
        @DisplayName("Краевой: очистка поля города опустошает выпадашку")
        void clearingDestination_removesSuggestions() {
            hotelPage.typeDestination("Сочи");
            assertTrue(hotelPage.isSuggestionsVisible(), "Сначала подсказки должны быть видны");

            hotelPage.clearDestination();
            int count = hotelPage.getSuggestionsCount();
            assertFalse(count > 0 && hotelPage.isSuggestionsVisible() && count == hotelPage.getSuggestionsCount(),
                    "После очистки поля подсказки автокомплита по запросу не должны оставаться");
        }

        @Test
        @DisplayName("Краевой: повторное открытие страницы не ломает поиск")
        void reopenPage_preservesFunctionality() {
            hotelPage.typeDestination("Сочи");
            hotelPage = new HotelSearchPage(driver).open();
            assertTrue(hotelPage.isDestinationInputVisible(),
                    "После перезагрузки страницы поле города должно быть доступно");
            assertTrue(hotelPage.isSearchButtonVisible(),
                    "После перезагрузки кнопка поиска должна быть доступна");
        }
    }
}
