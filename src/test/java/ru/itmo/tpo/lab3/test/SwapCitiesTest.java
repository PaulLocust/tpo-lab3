package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-07: Замена городов местами (swap) в форме поиска авиабилетов.
 * Актор: Гость. Extend для UC-03.
 */
public class SwapCitiesTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Кнопка swap видна в форме поиска")
    void swapButtonIsVisible() {
        assertTrue(flightPage.isSwapButtonVisible(),
                "Кнопка swap должна быть в форме поиска авиабилетов");
    }

    @Test
    @DisplayName("Swap обменивает значения 'Откуда' и 'Куда'")
    void swapExchangesOriginAndDestination() {
        flightPage
                .typeOrigin("Москва").chooseFirstSuggestion()
                .typeDestination("Сочи").chooseFirstSuggestion();

        String originBefore = flightPage.getOriginValue();
        String destBefore   = flightPage.getDestinationValue();

        flightPage.clickSwap();

        String originAfter = flightPage.getOriginValue();
        String destAfter   = flightPage.getDestinationValue();

        assertNotEquals(originBefore, originAfter,
                "После swap значение 'Откуда' должно измениться");
        assertTrue(
                originAfter.equalsIgnoreCase(destBefore) && destAfter.equalsIgnoreCase(originBefore),
                "После swap 'Откуда' и 'Куда' должны поменяться местами. "
                        + "До: [" + originBefore + " -> " + destBefore + "], "
                        + "после: [" + originAfter + " -> " + destAfter + "]"
        );
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи swap городов")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: swap при пустых полях не вызывает ошибки")
        void swapWithEmptyFields_doesNotCrash() {
            String originBefore = flightPage.getOriginValue();
            String destBefore   = flightPage.getDestinationValue();

            // Клик не должен бросать исключение
            flightPage.clickSwap();

            String originAfter = flightPage.getOriginValue();
            String destAfter   = flightPage.getDestinationValue();

            assertEquals(blankOrNull(originBefore), blankOrNull(originAfter),
                    "Пустое 'Откуда' должно остаться пустым после swap");
            assertEquals(blankOrNull(destBefore), blankOrNull(destAfter),
                    "Пустое 'Куда' должно остаться пустым после swap");
            assertTrue(flightPage.isOriginInputVisible(),
                    "После swap при пустой форме поле 'Откуда' должно оставаться доступным");
        }

        @Test
        @DisplayName("Краевой: двойной swap возвращает исходное состояние")
        void doubleSwap_restoresOriginalState() {
            flightPage
                    .typeOrigin("Москва").chooseFirstSuggestion()
                    .typeDestination("Сочи").chooseFirstSuggestion();

            String originBefore = flightPage.getOriginValue();
            String destBefore   = flightPage.getDestinationValue();

            flightPage.clickSwap();
            flightPage.clickSwap();

            assertEquals(originBefore, flightPage.getOriginValue(),
                    "После двух swap 'Откуда' должно вернуться к исходному значению");
            assertEquals(destBefore, flightPage.getDestinationValue(),
                    "После двух swap 'Куда' должно вернуться к исходному значению");
        }

        private String blankOrNull(String s) {
            return (s == null || s.isBlank()) ? "" : s;
        }
    }
}
