package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Страница оформления покупки авиабилета (UC-21).
 *
 * URL после успешного перехода с выдачи: {@value #URL_FRAGMENT}.
 * Тесты только проверяют сам переход — никакие персональные данные не вводятся.
 */
public class FlightCheckoutPage extends BasePage {

    public static final String URL_FRAGMENT = "/travel/flights/checkout";

    public FlightCheckoutPage(WebDriver driver) {
        super(driver);
    }

    /** Ждёт, пока URL не будет содержать checkout-фрагмент. */
    public FlightCheckoutPage awaitOpened() {
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
