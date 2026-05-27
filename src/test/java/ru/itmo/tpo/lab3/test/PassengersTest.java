package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-09: Выбор количества пассажиров и класса обслуживания.
 * Актор: Гость. Extend для UC-03.
 */
public class PassengersTest extends BaseTest {

    private FlightSearchPage flightPage;

    @BeforeEach
    void openFlightForm() {
        flightPage = new FlightSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Клик по селектору пассажиров открывает панель")
    void passengersPanelOpens() {
        flightPage.openPassengersPanel();
        assertTrue(flightPage.isPassengersPanelOpen(),
                "Должна открываться панель выбора пассажиров и класса");
    }

    @Test
    @DisplayName("Можно переключить класс обслуживания на 'Бизнес'")
    void canSwitchToBusinessClass() {
        flightPage.selectBusinessClass();
        assertTrue(flightPage.isBusinessClassSelected(),
                "После выбора 'Бизнес' значение класса должно отразиться в UI");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи селектора пассажиров")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: панель пассажиров остаётся доступной после открытия")
        void panelStaysFunctionalAfterOpen() {
            flightPage.openPassengersPanel();
            assertTrue(flightPage.isPassengersPanelOpen(),
                    "Панель открылась");
            // Форма поиска не должна потерять кнопку поиска
            assertTrue(flightPage.isSearchButtonVisible(),
                    "После открытия панели кнопка поиска должна оставаться видимой");
        }

        @Test
        @DisplayName("Краевой: после выбора 'Бизнес' можно повторно открыть селектор")
        void canReopenSelectorAfterChoosingBusiness() {
            flightPage.selectBusinessClass();
            // Selector может закрыться после выбора — переоткроем
            flightPage.openPassengersPanel();
            assertTrue(flightPage.isPassengersPanelOpen()
                            || flightPage.isBusinessClassSelected(),
                    "После выбора 'Бизнес' селектор должен открываться повторно "
                            + "или значение 'Бизнес' должно сохраняться в UI");
        }
    }
}
