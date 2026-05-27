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
    @DisplayName("Поле даты вылета 'Когда' видно в форме")
    void departureDateFieldIsVisible() {
        assertTrue(flightPage.isDepartureDateVisible(),
                "Поле даты вылета 'Когда' должно отображаться");
    }

    @Test
    @DisplayName("Клик по полю даты открывает виджет календаря")
    void calendarOpensOnClick() {
        flightPage.openDepartureDatePicker();
        assertTrue(flightPage.isCalendarVisible(),
                "По клику на 'Когда' должен открываться виджет календаря (gridcell)");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи календаря дат")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: повторный клик по дате не ломает форму")
        void reopeningCalendar_doesNotBreakForm() {
            flightPage.openDepartureDatePicker();
            assertTrue(flightPage.isCalendarVisible(), "Сначала календарь должен открыться");
            // Кликаем по полю ещё раз — форма должна остаться рабочей
            flightPage.openDepartureDatePicker();
            assertTrue(flightPage.isOriginInputVisible(),
                    "После повторного открытия календаря форма должна оставаться рабочей");
            assertTrue(flightPage.isSearchButtonVisible(),
                    "Кнопка поиска должна оставаться видимой");
        }

        @Test
        @DisplayName("Краевой: открытие календаря не уводит со страницы формы")
        void openingCalendar_doesNotNavigateAway() {
            String urlBefore = driver.getCurrentUrl();
            flightPage.openDepartureDatePicker();
            assertTrue(driver.getCurrentUrl().equals(urlBefore),
                    "Открытие календаря не должно менять URL");
        }
    }
}
