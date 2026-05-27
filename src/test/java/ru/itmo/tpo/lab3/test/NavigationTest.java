package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightSearchPage;
import ru.itmo.tpo.lab3.page.HotelSearchPage;
import ru.itmo.tpo.lab3.page.MainPage;
import ru.itmo.tpo.lab3.page.TourPage;
import ru.itmo.tpo.lab3.page.TrainSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-02: Переход между разделами сайта (Авиа / Отели / Поезда / Туры).
 * Актор: Гость.
 */
public class NavigationTest extends BaseTest {

    private MainPage mainPage;

    @BeforeEach
    void openMainPage() {
        mainPage = new MainPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Клик 'Авиа' открывает форму поиска авиабилетов")
    void aviaTabOpensFlightForm() {
        FlightSearchPage flights = mainPage.clickAvia();
        assertTrue(driver.getCurrentUrl().contains("/travel/flights"),
                "URL должен содержать /travel/flights");
        assertTrue(flights.isOriginInputVisible(),      "Не отображается поле 'Откуда'");
        assertTrue(flights.isDestinationInputVisible(), "Не отображается поле 'Куда'");
        assertTrue(flights.isSearchButtonVisible(),     "Не отображается кнопка поиска");
    }

    @Test
    @DisplayName("Клик 'Отели' открывает форму поиска отелей")
    void hotelsTabOpensHotelForm() {
        HotelSearchPage hotels = mainPage.clickHotels();
        assertTrue(driver.getCurrentUrl().contains("/travel/hotels"),
                "URL должен содержать /travel/hotels");
        assertTrue(hotels.isDestinationInputVisible(), "Не отображается поле города");
        assertTrue(hotels.isSearchButtonVisible(),     "Не отображается кнопка 'Искать'");
    }

    @Test
    @DisplayName("Клик 'Поезда' открывает форму поиска ЖД-билетов")
    void trainsTabOpensTrainForm() {
        TrainSearchPage trains = mainPage.clickTrains();
        assertTrue(driver.getCurrentUrl().contains("/travel/trains"),
                "URL должен содержать /travel/trains");
        assertTrue(trains.isOriginInputVisible(),      "Не отображается поле 'Откуда'");
        assertTrue(trains.isDestinationInputVisible(), "Не отображается поле 'Куда'");
    }

    @Test
    @DisplayName("Клик 'Туры' открывает раздел туров")
    void toursTabOpensToursPage() {
        TourPage tours = mainPage.clickTours();
        assertTrue(driver.getCurrentUrl().contains("/travel/tours"),
                "URL должен содержать /travel/tours");
        assertTrue(tours.isFindTourButtonVisible(),
                "На странице туров должна быть кнопка 'Найти тур' от партнёра");
    }

    // ============== Краевые случаи ==============


    @Test
    @DisplayName("Краевой: прямой переход на /travel/flights/ открывает форму авиабилетов")
    void directNavigationToFlightsPage() {
        FlightSearchPage flights = new FlightSearchPage(driver).open();
        assertTrue(flights.isOriginInputVisible(),
                "Прямой переход по URL должен открывать рабочую форму");
        assertTrue(flights.isSearchButtonVisible(),
                "Прямой переход по URL должен показывать кнопку поиска");
    }

}
