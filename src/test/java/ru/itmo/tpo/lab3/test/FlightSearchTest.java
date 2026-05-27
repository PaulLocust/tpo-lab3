package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-03: Поиск авиабилетов (основной сценарий сайта).
 * Актор: Гость.
 *
 * Учтено, что форма приходит с уже заполненным «Откуда» (по умолчанию
 * Санкт-Петербург, LED) и пустым «Куда». «Краевые» тесты сначала
 * подготавливают форму к нужному стартовому состоянию.
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
        assertTrue(flightPage.isSearchButtonVisible(),     "Нет кнопки 'Найти'");
        assertTrue(flightPage.isDepartureDateVisible(),    "Нет поля даты вылета 'Когда'");
        assertTrue(flightPage.isComplexRouteButtonVisible(),
                "Должна быть кнопка 'Сложный маршрут'");
    }

    @Test
    @DisplayName("Ввод в поле 'Куда' открывает список подсказок городов")
    void destinationAutocompleteShowsSuggestions() {
        flightPage.typeDestination("Моск");
        assertTrue(flightPage.isSuggestionsVisible(),
                "При вводе подстроки должен показываться выпадающий список подсказок");
    }

    @Test
    @DisplayName("Выбор подсказки заполняет поле 'Куда'")
    void selectingSuggestionFillsDestinationField() {
        flightPage.typeDestination("Москва").chooseFirstSuggestion();
        String value = flightPage.getDestinationValue();
        assertTrue(value != null && !value.isBlank(),
                "После выбора подсказки поле 'Куда' должно быть заполнено, получено: " + value);
    }

    @Test
    @DisplayName("Полный сценарий: заполняем 'Куда' → Найти ведёт к выдаче")
    void fullSearchOpensResults() {
        // По умолчанию Откуда='Санкт-Петербург' уже заполнено, добавим только Куда.
        flightPage
                .typeDestination("Сочи").chooseFirstSuggestion()
                .clickSearch();
        assertTrue(flightPage.isResultsOpened(),
                "После корректно заполненной формы должна открыться страница результатов");
    }

    @Test
    @DisplayName("Поле 'Откуда' имеет дефолтное значение (Санкт-Петербург)")
    void originIsPrefilledByDefault() {
        String origin = flightPage.getOriginValue();
        assertTrue(origin != null && !origin.isBlank(),
                "Поле 'Откуда' должно быть заполнено по умолчанию, получено: " + origin);
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи поиска авиабилетов")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: оба поля пустые → поиск не идёт")
        void bothEmpty_doesNotProceed() {
            flightPage.clearOrigin();
            String before = driver.getCurrentUrl();
            flightPage.clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При пустых обоих полях поиск не должен переходить к выдаче");
        }

        @Test
        @DisplayName("Краевой: заполнено только 'Откуда' (default) → поиск не идёт")
        void onlyOriginPrefilled_doesNotProceed() {
            // 'Куда' пусто по дефолту, 'Откуда' = LED
            String before = driver.getCurrentUrl();
            flightPage.clickSearch();
            assertTrue(flightPage.stayedOnSearchForm(before),
                    "При незаполненном 'Куда' поиск не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: очистка поля 'Откуда' опустошает значение")
        void clearingOrigin_emptiesField() {
            String filled = flightPage.getOriginValue();
            assertTrue(filled != null && !filled.isBlank(),
                    "До очистки поле 'Откуда' должно быть заполнено (default)");

            flightPage.clearOrigin();
            String afterClear = flightPage.getOriginValue();
            assertTrue(afterClear == null || afterClear.isBlank(),
                    "После очистки поле 'Откуда' должно быть пустым, получено: " + afterClear);
        }

        @Test
        @DisplayName("Краевой: ввод бессмыслицы — ни одна подсказка не содержит введённой строки")
        void nonExistentCity_noSuggestionContainsTheInput() {
            String junk = "Кфтыкчоувапролд";
            flightPage.typeDestination(junk);
            assertFalse(flightPage.anySuggestionContains(junk),
                    "Подсказок, содержащих '" + junk + "', быть не должно");
        }

        @Test
        @DisplayName("Краевой: повторный набор города после очистки снова открывает подсказки")
        void retypingAfterClear_showsSuggestionsAgain() {
            flightPage.typeDestination("Москва").chooseFirstSuggestion();
            flightPage.clearDestination();
            flightPage.typeDestination("Каз");
            assertTrue(flightPage.isSuggestionsVisible(),
                    "После очистки и повторного ввода автокомплит должен снова работать");
        }

        @Test
        @DisplayName("Краевой: выпадашка автокомплита содержит >= 1 пункта для известного города")
        void knownCity_returnsAtLeastOneSuggestion() {
            flightPage.typeDestination("Москва");
            assertTrue(flightPage.getSuggestionsCount() >= 1,
                    "Для 'Москва' выпадашка должна содержать хотя бы один пункт");
        }
    }
}
