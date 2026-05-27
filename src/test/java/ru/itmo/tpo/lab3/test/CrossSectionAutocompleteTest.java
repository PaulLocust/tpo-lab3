package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;
import ru.itmo.tpo.lab3.page.HotelSearchPage;
import ru.itmo.tpo.lab3.page.TrainSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-10: Поиск с автодополнением во всех трёх формах сайта.
 * Проверяет, что подсказки городов работают и на Авиа, и на Отелях, и на ЖД.
 * Актор: Гость. Include для UC-03, UC-04, UC-05.
 */
public class CrossSectionAutocompleteTest extends BaseTest {

    // ============== Основной поток ==============

    @Test
    @DisplayName("Автокомплит работает на форме поиска авиабилетов")
    void autocompleteWorksOnFlightForm() {
        FlightSearchPage page = new FlightSearchPage(driver).open();
        page.typeDestination("Каз");
        assertTrue(page.isSuggestionsVisible(),
                "На форме авиабилетов должны показываться подсказки городов");
    }

    @Test
    @DisplayName("Автокомплит работает на форме поиска отелей")
    void autocompleteWorksOnHotelForm() {
        HotelSearchPage page = new HotelSearchPage(driver).open();
        page.typeDestination("Соч");
        assertTrue(page.isSuggestionsListboxVisible() || page.isSuggestionsVisible(),
                "На форме отелей должны показываться подсказки городов");
    }

    @Test
    @DisplayName("Автокомплит работает на форме поиска ЖД-билетов")
    void autocompleteWorksOnTrainForm() {
        TrainSearchPage page = new TrainSearchPage(driver).open();
        page.typeOrigin("Моск");
        assertTrue(page.isSuggestionsListboxVisible() || page.isSuggestionsVisible(),
                "На форме ЖД должны показываться подсказки городов");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи автокомплита")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: ни одна подсказка во всех 3 формах не содержит бессмысленного ввода")
        void nonExistentCity_noSuggestionContainsInputAcrossAllForms() {
            String junk = "Кфтыкчоувапролд";

            FlightSearchPage flight = new FlightSearchPage(driver).open();
            flight.typeDestination(junk);
            assertFalse(flight.anySuggestionContains(junk),
                    "Авиа: подсказок с '" + junk + "' быть не должно");

            HotelSearchPage hotel = new HotelSearchPage(driver).open();
            hotel.typeDestination(junk);
            assertFalse(hotel.anySuggestionContains(junk),
                    "Отели: подсказок с '" + junk + "' быть не должно");

            TrainSearchPage train = new TrainSearchPage(driver).open();
            train.typeOrigin(junk);
            assertFalse(train.anySuggestionContains(junk),
                    "ЖД: подсказок с '" + junk + "' быть не должно");
        }

        @Test
        @DisplayName("Краевой: известный город даёт хотя бы один пункт автокомплита (авиа)")
        void knownCity_returnsAtLeastOneSuggestion() {
            FlightSearchPage page = new FlightSearchPage(driver).open();
            page.typeDestination("Москва");
            assertTrue(page.getSuggestionsCount() >= 1,
                    "Для 'Москва' выпадашка должна содержать хотя бы один пункт");
        }

        @Test
        @DisplayName("Краевой: пустой ввод не открывает выпадашку (форма отелей)")
        void emptyInput_returnsNoSuggestionsOnHotels() {
            HotelSearchPage page = new HotelSearchPage(driver).open();
            assertFalse(page.isSuggestionsListboxVisible(),
                    "Без ввода подсказки автокомплита показываться не должны");
        }
    }
}
