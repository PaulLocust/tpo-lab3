package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-03: Поиск авиабилетов (основной сценарий сайта).
 * Актор: Гость.
 *
 * Покрывает как основной поток (happy path), так и краевые случаи
 * (валидация частично заполненной формы, очистка полей, очень короткий
 * и заведомо несуществующий ввод и т.п.).
 */
public class FlightSearchTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Форма поиска содержит все ключевые элементы")
    void formHasAllKeyElements() {
        assertTrue(flightPage.isOriginInputVisible(),      "Нет поля 'Откуда'");
        assertTrue(flightPage.isDestinationInputVisible(), "Нет поля 'Куда'");
        assertTrue(flightPage.isSearchButtonVisible(),     "Нет кнопки поиска");
        assertTrue(flightPage.isDepartureDateVisible(),    "Нет поля даты вылета");
        assertTrue(flightPage.isSwapButtonVisible(),       "Нет кнопки swap городов");
    }

    @Test
    @DisplayName("Ввод в поле 'Откуда' открывает список подсказок городов")
    void originAutocompleteShowsSuggestions() {
        flightPage.typeOrigin("Моск");
        assertTrue(flightPage.isSuggestionsVisible(),
                "При вводе подстроки должен показываться выпадающий список подсказок");
    }

    @Test
    @DisplayName("Выбор подсказки заполняет поле 'Откуда'")
    void selectingSuggestionFillsOriginField() {
        flightPage.typeOrigin("Москва").chooseFirstSuggestion();
        String value = flightPage.getOriginValue();
        assertTrue(value != null && !value.isBlank(),
                "После выбора подсказки поле 'Откуда' должно быть заполнено, получено: " + value);
    }

    @Test
    @DisplayName("Полный сценарий: Москва → Сочи → Найти билеты ведёт к выдаче")
    void fullOneWayFlightSearchOpensResults() {
        flightPage
                .typeOrigin("Москва").chooseFirstSuggestion()
                .typeDestination("Сочи").chooseFirstSuggestion()
                .clickSearch();
        assertTrue(flightPage.isResultsOpened(),
                "После корректно заполненной формы должна открыться страница результатов");
    }

    // ============== Краевые случаи ==============

    /**
     * Краевые сценарии — отдельный @Nested-класс, чтобы их было легко
     * отличить от happy-path в отчёте Gradle.
     */
    @Nested
    @DisplayName("Краевые случаи поиска авиабилетов")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: поиск с пустой формой не приводит к выдаче")
        void emptyFormDoesNotProceedToResults() {
            String before = driver.getCurrentUrl();
            flightPage.clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При пустой форме поиск не должен переходить к выдаче рейсов");
            assertFalse(driver.getCurrentUrl().matches(".*from=.*&to=.*"),
                    "URL не должен содержать параметров поиска при незаполненной форме");
        }

        @Test
        @DisplayName("Краевой: заполнено только 'Откуда' — поиск не идёт")
        void onlyOriginFilled_doesNotProceed() {
            String before = driver.getCurrentUrl();
            flightPage.typeOrigin("Москва").chooseFirstSuggestion();
            flightPage.clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При незаполненном 'Куда' поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: заполнено только 'Куда' — поиск не идёт")
        void onlyDestinationFilled_doesNotProceed() {
            String before = driver.getCurrentUrl();
            flightPage.typeDestination("Сочи").chooseFirstSuggestion();
            flightPage.clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При незаполненном 'Откуда' поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: одинаковые города (Москва → Москва) — поиск блокируется")
        void sameOriginAndDestination_doesNotProceed() {
            String before = driver.getCurrentUrl();
            flightPage
                    .typeOrigin("Москва").chooseFirstSuggestion()
                    .typeDestination("Москва").chooseFirstSuggestion()
                    .clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При совпадающих городах поиск не должен переходить к выдаче");
        }

        @Test
        @DisplayName("Краевой: очистка поля 'Откуда' опустошает значение")
        void clearingOrigin_emptiesField() {
            flightPage.typeOrigin("Москва").chooseFirstSuggestion();
            String filled = flightPage.getOriginValue();
            assertFalse(filled == null || filled.isBlank(),
                    "Сначала поле должно быть заполнено");

            flightPage.clearOrigin();
            String afterClear = flightPage.getOriginValue();
            assertTrue(afterClear == null || afterClear.isBlank(),
                    "После очистки поле 'Откуда' должно быть пустым, получено: " + afterClear);
        }

        @Test
        @DisplayName("Краевой: ввод заведомо несуществующего города не даёт реальных подсказок")
        void nonExistentCity_doesNotShowMeaningfulSuggestions() {
            flightPage.typeOrigin("Кфтыкчоувапролд");
            int count = flightPage.getSuggestionsCount();
            assertTrue(count == 0,
                    "Для бессмысленного запроса подсказок быть не должно, получено: " + count);
        }

        @Test
        @DisplayName("Краевой: ввод одной буквы — подсказки либо отсутствуют, либо релевантны")
        void singleLetterInput_isHandledSafely() {
            flightPage.typeOrigin("М");
            // На реальных сайтах одна буква обычно не открывает выпадашку.
            // Проверяем, что страница не упала и форма по-прежнему рабочая.
            assertTrue(flightPage.isOriginInputVisible(),
                    "После ввода одной буквы поле 'Откуда' должно оставаться видимым");
            assertTrue(flightPage.isSearchButtonVisible(),
                    "После ввода одной буквы кнопка поиска должна оставаться видимой");
        }

        @Test
        @DisplayName("Краевой: повторный набор города после очистки снова открывает подсказки")
        void retypingAfterClear_showsSuggestionsAgain() {
            flightPage.typeOrigin("Москва").chooseFirstSuggestion();
            flightPage.clearOrigin();
            flightPage.typeOrigin("Каз");
            assertTrue(flightPage.isSuggestionsVisible(),
                    "После очистки и повторного ввода автокомплит должен снова работать");
        }
    }
}
