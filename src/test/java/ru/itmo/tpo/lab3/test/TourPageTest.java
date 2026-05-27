package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TourPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-06: Просмотр раздела «Туры».
 * Актор: Гость.
 *
 * На этой странице у T-Travel нет собственной формы поиска —
 * вместо неё показаны карточки партнёров (Travelata, Onlinetours, Level.Travel)
 * с кнопкой 'Найти тур'.
 */
public class TourPageTest extends BaseTest {

    private TourPage tourPage;

    @BeforeEach
    void openToursPage() {
        tourPage = new TourPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Страница /travel/tours/ загружается")
    void tourPageLoads() {
        assertTrue(driver.getCurrentUrl().contains("/travel/tours"),
                "URL должен содержать /travel/tours");
    }

    @Test
    @DisplayName("На странице видны кнопки 'Найти тур' от партнёров")
    void findTourButtonsArePresent() {
        assertTrue(tourPage.isFindTourButtonVisible(),
                "Должна быть хотя бы одна кнопка 'Найти тур' от партнёра");
    }


    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи раздела туров")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: перезагрузка страницы туров сохраняет наличие партнёров")
        void reloadingPreservesContent() {
            int before = tourPage.getFindTourButtonsCount();
            tourPage = new TourPage(driver).open();
            int after = tourPage.getFindTourButtonsCount();
            assertTrue(after == before,
                    "После перезагрузки число партнёрских кнопок должно сохраниться");
        }
    }
}
