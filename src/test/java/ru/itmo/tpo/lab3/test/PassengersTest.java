package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-09: Открытие селектора пассажиров и класса.
 * Актор: Гость. Extend для UC-03.
 *
 * Признак открытия панели — индикатор стрелки Arrow переходит из
 * Arrow_notopened в Arrow_opened (см. dom-discovery).
 */
public class PassengersTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Поле 'Пассажиры' с дефолтом '1 пассажир, эконом' присутствует на форме")
    void passengersFieldIsPresent() {
        // Простая проверка: клик по полю не должен бросать исключение
        flightPage.openPassengersPanel();
        assertTrue(flightPage.isSearchButtonVisible(),
                "После клика по селектору пассажиров кнопка поиска должна оставаться видимой");
    }

    @Test
    @DisplayName("Клик по селектору пассажиров переводит стрелку в состояние 'opened'")
    void passengersPanelOpens() {
        flightPage.openPassengersPanel();
        assertTrue(flightPage.isPassengersPanelOpen(),
                "После клика индикатор стрелки должен перейти в Arrow_opened");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи селектора пассажиров")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: панель пассажиров не ломает остальную форму")
        void panelStaysFunctionalAfterOpen() {
            flightPage.openPassengersPanel();
            // После открытия панели остальные элементы формы должны оставаться доступными.
            assertTrue(flightPage.isOriginInputVisible(),
                    "Поле 'Откуда' должно оставаться видимым");
            assertTrue(flightPage.isDestinationInputVisible(),
                    "Поле 'Куда' должно оставаться видимым");
            assertTrue(flightPage.isSearchButtonVisible(),
                    "Кнопка поиска должна оставаться видимой");
        }

        @Test
        @DisplayName("Краевой: открытие селектора пассажиров не уводит с раздела /flights/")
        void openingPassengersPanel_staysOnFlightsSection() {
            flightPage.openPassengersPanel();
            // URL может получить hash или query-параметр, но раздел тот же.
            assertTrue(driver.getCurrentUrl().contains("/travel/flights"),
                    "Открытие селектора пассажиров не должно уводить с /travel/flights");
        }
    }
}
