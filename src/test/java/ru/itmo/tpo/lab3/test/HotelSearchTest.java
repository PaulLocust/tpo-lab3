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
 *
 * На форме отелей по умолчанию заполнены даты и количество гостей,
 * не заполнено только направление.
 */
public class HotelSearchTest extends BaseTest {

    private HotelSearchPage hotelPage;

    @BeforeEach
    void openHotelForm() {
        hotelPage = new HotelSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Форма содержит поле направления, даты, гостей и кнопку 'Искать'")
    void formHasAllFields() {
        assertTrue(hotelPage.isDestinationInputVisible(), "Нет поля направления");
        assertTrue(hotelPage.isDateFieldVisible(),        "Нет поля диапазона дат");
        assertTrue(hotelPage.isGuestsFieldVisible(),      "Нет поля количества гостей");
        assertTrue(hotelPage.isSearchButtonVisible(),     "Нет кнопки 'Искать'");
    }

    @Test
    @DisplayName("Ввод названия города показывает выпадающий listbox подсказок")
    void destinationAutocompleteShowsSuggestions() {
        hotelPage.typeDestination("Соч");
        assertTrue(hotelPage.isSuggestionsListboxVisible()
                        || hotelPage.isSuggestionsVisible(),
                "Должны отображаться подсказки автокомплита при вводе названия города");
    }

    @Test
    @DisplayName("Полный сценарий: выбор города и нажатие 'Искать' переходит к результатам")
    void hotelSearchByCityOpensResults() {
        hotelPage
                .typeDestination("Сочи")
                .chooseFirstSuggestion()
                .clickSearch();
        assertTrue(hotelPage.isResultsOpened(),
                "После выбора города и нажатия 'Искать' должна открыться страница результатов");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи поиска отелей")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: поиск с пустым полем города не даёт перехода")
        void emptyDestination_doesNotProceed() {
            String before = driver.getCurrentUrl();
            hotelPage.clickSearch();
            assertTrue(hotelPage.stayedOnSearchForm(before),
                    "С пустым полем города поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: ввод бессмыслицы — ни одна подсказка не содержит введённой строки")
        void nonExistentCity_noSuggestionContainsInput() {
            String junk = "Кфтыкчоувапролд";
            hotelPage.typeDestination(junk);
            assertFalse(hotelPage.anySuggestionContains(junk),
                    "Подсказок, содержащих '" + junk + "', быть не должно");
        }

        @Test
        @DisplayName("Краевой: очистка поля города убирает значение")
        void clearingDestination_emptiesValue() {
            hotelPage.typeDestination("Сочи").chooseFirstSuggestion();
            String filled = hotelPage.getDestinationValue();
            assertTrue(filled != null && !filled.isBlank(),
                    "До очистки поле должно быть заполнено");
            hotelPage.clearDestination();
            String afterClear = hotelPage.getDestinationValue();
            assertTrue(afterClear == null || afterClear.isBlank(),
                    "После очистки поле города должно быть пустым");
        }

        @Test
        @DisplayName("Краевой: повторное открытие страницы не ломает форму")
        void reopenPage_preservesFunctionality() {
            hotelPage.typeDestination("Сочи");
            hotelPage = new HotelSearchPage(driver).open();
            assertTrue(hotelPage.isDestinationInputVisible(),
                    "После перезагрузки страницы поле города должно быть доступно");
            assertTrue(hotelPage.isSearchButtonVisible(),
                    "После перезагрузки кнопка 'Искать' должна быть доступна");
        }
    }
}
