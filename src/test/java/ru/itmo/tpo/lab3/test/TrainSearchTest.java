package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TrainSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-05: Поиск ЖД-билетов.
 * Актор: Гость.
 */
public class TrainSearchTest extends BaseTest {

    private TrainSearchPage trainPage;

    @BeforeEach
    void openTrainForm() {
        trainPage = new TrainSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Форма поиска ЖД содержит поля 'Откуда', 'Куда', дату и кнопку поиска")
    void formHasMandatoryElements() {
        assertTrue(trainPage.isOriginInputVisible(),      "Нет поля 'Откуда'");
        assertTrue(trainPage.isDestinationInputVisible(), "Нет поля 'Куда'");
        assertTrue(trainPage.isDateInputVisible(),        "Нет поля выбора даты");
        assertTrue(trainPage.isSearchButtonVisible(),     "Нет кнопки поиска");
    }

    @Test
    @DisplayName("Ввод в поле 'Откуда' показывает подсказки городов")
    void originAutocompleteShowsSuggestions() {
        trainPage.typeOrigin("Моск");
        assertTrue(trainPage.isSuggestionsVisible(),
                "При вводе должен показываться список подсказок");
    }

    @Test
    @DisplayName("Полный сценарий: Москва → Санкт-Петербург открывает выдачу поездов")
    void searchTrainBetweenCities() {
        trainPage
                .typeOrigin("Москва").chooseFirstSuggestion()
                .typeDestination("Санкт-Петербург").chooseFirstSuggestion()
                .clickSearch();
        assertTrue(trainPage.isResultsOpened(),
                "После заполнения формы должна открываться выдача поездов");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи поиска ЖД-билетов")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: поиск с пустой формой не запускает выдачу")
        void emptyForm_doesNotProceed() {
            String before = driver.getCurrentUrl();
            trainPage.clickSearch();
            assertTrue(trainPage.stayedOnSearchForm(before),
                    "С пустой формой поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: заполнено только 'Откуда' — поиск не идёт")
        void onlyOriginFilled_doesNotProceed() {
            String before = driver.getCurrentUrl();
            trainPage.typeOrigin("Москва").chooseFirstSuggestion();
            trainPage.clickSearch();
            assertTrue(trainPage.stayedOnSearchForm(before),
                    "При незаполненном 'Куда' поиск ЖД не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: одинаковые города (Москва → Москва) — поиск блокируется")
        void sameOriginAndDestination_doesNotProceed() {
            String before = driver.getCurrentUrl();
            trainPage
                    .typeOrigin("Москва").chooseFirstSuggestion()
                    .typeDestination("Москва").chooseFirstSuggestion()
                    .clickSearch();
            assertTrue(trainPage.stayedOnSearchForm(before),
                    "При совпадающих городах поиск не должен переходить к выдаче");
        }

        @Test
        @DisplayName("Краевой: ввод заведомо несуществующего города не даёт подсказок")
        void nonExistentCity_showsNoSuggestions() {
            trainPage.typeOrigin("Кфтыкчоувапролд");
            int count = trainPage.getSuggestionsCount();
            assertTrue(count == 0,
                    "Для бессмысленного запроса подсказок быть не должно, получено: " + count);
        }
    }
}
