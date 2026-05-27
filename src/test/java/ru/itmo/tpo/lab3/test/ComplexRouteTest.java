package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-07: Дополнительные опции формы авиапоиска — «Сложный маршрут»
 * и тоггл «Лечу по работе».
 * Актор: Гость. Extend для UC-03.
 *
 * Изначально планировался тест swap-кнопки, но на актуальной форме
 * T-Travel её нет — вместо этого тестируем реально доступные расширения.
 */
public class ComplexRouteTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Кнопка 'Сложный маршрут' видна на форме поиска")
    void complexRouteButtonIsVisible() {
        assertTrue(flightPage.isComplexRouteButtonVisible(),
                "На форме должна быть кнопка 'Сложный маршрут'");
    }

    @Test
    @DisplayName("Тоггл 'Лечу по работе' присутствует в форме")
    void workTripToggleIsPresent() {
        assertTrue(flightPage.isWorkTripTogglePresent(),
                "Тоггл 'Лечу по работе' должен быть в форме");
    }

    @Test
    @DisplayName("Тоггл 'Лечу по работе' переключается")
    void workTripToggleCanBeSwitched() {
        boolean before = flightPage.isWorkTripChecked();
        flightPage.toggleWorkTrip();
        boolean after = flightPage.isWorkTripChecked();
        assertTrue(before != after,
                "После клика состояние тоггла должно измениться. До: " + before + ", после: " + after);
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи 'Сложный маршрут' / тогглы")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: клик 'Сложный маршрут' не ломает форму поиска")
        void clickComplexRoute_doesNotBreakSearchForm() {
            flightPage.clickComplexRoute();
            // После открытия мультисегментного режима кнопка 'Найти' должна остаться доступной
            assertTrue(flightPage.isSearchButtonVisible(),
                    "После клика 'Сложный маршрут' кнопка 'Найти' должна оставаться видимой");
        }

        @Test
        @DisplayName("Краевой: двойное переключение тоггла возвращает в исходное состояние")
        void doubleToggle_restoresInitialState() {
            boolean initial = flightPage.isWorkTripChecked();
            flightPage.toggleWorkTrip();
            flightPage.toggleWorkTrip();
            assertTrue(flightPage.isWorkTripChecked() == initial,
                    "После двух кликов тоггл должен вернуться к исходному состоянию");
        }
    }
}
