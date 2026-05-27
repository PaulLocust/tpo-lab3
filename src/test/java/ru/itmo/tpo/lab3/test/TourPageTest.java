package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TourPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-06: Просмотр раздела «Туры».
 * Актор: Гость.
 */
public class TourPageTest extends BaseTest {

    private TourPage tourPage;

    @BeforeEach
    void openToursPage() {
        tourPage = new TourPage(driver).open();
    }

    @Test
    @DisplayName("Страница /travel/tours/ загружается")
    void tourPageLoads() {
        assertTrue(driver.getCurrentUrl().contains("/travel/tours"),
                "URL должен содержать /travel/tours");
    }

    @Test
    @DisplayName("На странице туров есть поле выбора направления")
    void tourPageHasDestinationField() {
        assertTrue(tourPage.isDestinationInputVisible(),
                "На странице туров должно быть поле направления");
    }
}
