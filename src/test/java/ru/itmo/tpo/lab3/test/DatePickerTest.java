package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-08: Выбор даты вылета через календарь.
 * Актор: Гость. Extend для UC-03.
 */
public class DatePickerTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Поле даты вылета видно в форме")
    void departureDateFieldIsVisible() {
        assertTrue(flightPage.isDepartureDateVisible(),
                "Поле даты вылета 'Туда' должно отображаться");
    }

    @Test
    @DisplayName("Клик по полю даты открывает календарь")
    void calendarOpensOnClick() {
        flightPage.openDepartureDatePicker();
        assertTrue(flightPage.isCalendarVisible(),
                "По клику на 'Туда' должен открываться виджет календаря");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи календаря дат")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: Esc закрывает открытый календарь")
        void calendarClosesOnEscape() {
            flightPage.openDepartureDatePicker();
            assertTrue(flightPage.isCalendarVisible(),
                    "Сначала календарь должен быть открыт");

            flightPage.closeCalendarWithEscape();

            assertTrue(flightPage.isCalendarHidden(),
                    "После нажатия Esc виджет календаря должен закрыться");
        }

        @Test
        @DisplayName("Краевой: повторное открытие календаря не ломает форму")
        void reopeningCalendar_doesNotBreakForm() {
            flightPage.openDepartureDatePicker();
            flightPage.closeCalendarWithEscape();
            flightPage.openDepartureDatePicker();

            assertTrue(flightPage.isCalendarVisible(),
                    "Календарь должен открываться при повторном клике на поле даты");
            assertTrue(flightPage.isOriginInputVisible(),
                    "Форма поиска должна оставаться рабочей после действий с календарём");
        }
    }
}
