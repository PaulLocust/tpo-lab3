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
 * Проверяет, что подсказки городов работают одинаково на Авиа, Отелях и ЖД.
 * Актор: Гость. Include для UC-03, UC-04, UC-05.
 */
public class CrossSectionAutocompleteTest extends BaseTest {

    // ============== Основной поток ==============

    @Test
    @DisplayName("Автокомплит работает на форме поиска авиабилетов")
    void autocompleteWorksOnFlightForm() {
        FlightSearchPage page = new FlightSearchPage(driver).open();
        page.typeOrigin("Каз");
        assertTrue(page.isSuggestionsVisible(),
                "На форме авиабилетов должны показываться подсказки городов");
    }

    @Test
    @DisplayName("Автокомплит работает на форме поиска отелей")
    void autocompleteWorksOnHotelForm() {
        HotelSearchPage page = new HotelSearchPage(driver).open();
        page.typeDestination("Кал");
        assertTrue(page.isSuggestionsVisible(),
                "На форме отелей должны показываться подсказки городов");
    }

    @Test
    @DisplayName("Автокомплит работает на форме поиска ЖД-билетов")
    void autocompleteWorksOnTrainForm() {
        TrainSearchPage page = new TrainSearchPage(driver).open();
        page.typeOrigin("Каз");
        assertTrue(page.isSuggestionsVisible(),
                "На форме ЖД должны показываться подсказки городов");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи автокомплита")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: бессмысленный ввод во всех формах не возвращает реальных подсказок")
        void nonExistentCity_returnsNoSuggestionsAcrossAllForms() {
            FlightSearchPage flight = new FlightSearchPage(driver).open();
            flight.typeOrigin("Кфтыкчоувапролд");
            assertTrue(flight.getSuggestionsCount() == 0,
                    "Авиа: на бессмысленный ввод подсказок быть не должно");

            HotelSearchPage hotel = new HotelSearchPage(driver).open();
            hotel.typeDestination("Кфтыкчоувапролд");
            assertTrue(hotel.getSuggestionsCount() == 0,
                    "Отели: на бессмысленный ввод подсказок быть не должно");

            TrainSearchPage train = new TrainSearchPage(driver).open();
            train.typeOrigin("Кфтыкчоувапролд");
            assertTrue(train.getSuggestionsCount() == 0,
                    "ЖД: на бессмысленный ввод подсказок быть не должно");
        }

        @Test
        @DisplayName("Краевой: подсказки авиа содержат хотя бы один реальный пункт для известного города")
        void knownCity_returnsAtLeastOneSuggestion() {
            FlightSearchPage page = new FlightSearchPage(driver).open();
            page.typeOrigin("Москва");
            assertTrue(page.getSuggestionsCount() >= 1,
                    "Для 'Москва' выпадашка должна содержать хотя бы один пункт");
        }

        @Test
        @DisplayName("Краевой: пустой ввод не даёт подсказок на форме отелей")
        void emptyInput_returnsNoSuggestionsOnHotels() {
            HotelSearchPage page = new HotelSearchPage(driver).open();
            // Не вводим ничего — выпадашка не должна сама собой появиться
            assertFalse(page.isSuggestionsVisible(),
                    "Без ввода подсказки автокомплита показываться не должны");
        }
    }
}
