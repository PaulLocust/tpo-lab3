package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Страница раздела «Туры» (/travel/tours/).
 */
public class TourPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/tours/";

    private static final String XPATH_DESTINATION_INPUT =
            "//input[contains(@placeholder,'Куда') or contains(@placeholder,'Страна')"
                    + " or contains(@aria-label,'Куда')]";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[normalize-space()='Найти' or contains(.,'Найти тур')]";

    public TourPage(WebDriver driver) {
        super(driver);
    }

    public TourPage open() {
        driver.get(URL);
        return this;
    }

    public boolean isDestinationInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }
}
