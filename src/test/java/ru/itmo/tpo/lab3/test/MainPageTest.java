package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.MainPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-01: Просмотр главной страницы T-Travel.
 * Актор: Гость.
 */
public class MainPageTest extends BaseTest {

    private MainPage mainPage;

    @BeforeEach
    void openMainPage() {
        mainPage = new MainPage(driver).open();
    }

    @Test
    @DisplayName("Главная страница /travel/ загружается на домене tbank.ru")
    void mainPageOpensOnTbankDomain() {
        assertTrue(
                driver.getCurrentUrl().contains("tbank.ru/travel"),
                "URL должен содержать tbank.ru/travel, получено: " + driver.getCurrentUrl()
        );
    }

    @Test
    @DisplayName("На главной отображается логотип T-Банка")
    void mainPageHasLogo() {
        assertTrue(mainPage.isLogoVisible(), "На странице должен быть логотип T-Банка");
    }

    @Test
    @DisplayName("Вкладка 'Авиа' видна в навигации")
    void aviaTabIsVisible() {
        assertTrue(mainPage.isAviaTabVisible(), "Вкладка 'Авиа' должна быть в шапке");
    }

    @Test
    @DisplayName("Вкладка 'Отели' видна в навигации")
    void hotelsTabIsVisible() {
        assertTrue(mainPage.isHotelsTabVisible(), "Вкладка 'Отели' должна быть в шапке");
    }

    @Test
    @DisplayName("Вкладка 'Поезда' видна в навигации")
    void trainsTabIsVisible() {
        assertTrue(mainPage.isTrainsTabVisible(), "Вкладка 'Поезда' должна быть в шапке");
    }

    @Test
    @DisplayName("Вкладка 'Туры' видна в навигации")
    void toursTabIsVisible() {
        assertTrue(mainPage.isToursTabVisible(), "Вкладка 'Туры' должна быть в шапке");
    }
}
