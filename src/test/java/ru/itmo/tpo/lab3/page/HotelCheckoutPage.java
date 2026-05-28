package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Страница оформления бронирования отеля (UC-22).
 *
 * URL после успешного перехода с карточки отеля: {@value #URL_FRAGMENT}.
 * Тесты только проверяют сам переход — никакие персональные данные не вводятся.
 */
public class HotelCheckoutPage extends BasePage {

    public static final String URL_FRAGMENT = "/travel/hotels/new/checkout";

    public HotelCheckoutPage(WebDriver driver) {
        super(driver);
    }

    public HotelCheckoutPage awaitOpened() {
        try {
            wait.until(ExpectedConditions.urlContains(URL_FRAGMENT));
        } catch (Exception ignored) {
        }
        return this;
    }

    public boolean isOpened() {
        return driver.getCurrentUrl().contains(URL_FRAGMENT);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
