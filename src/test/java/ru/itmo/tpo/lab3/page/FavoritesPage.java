package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Страница «Избранное» T-Travel — нужна для UC-18.
 */
public class FavoritesPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/favorites/";

    public FavoritesPage(WebDriver driver) {
        super(driver);
    }

    public FavoritesPage open() {
        driver.get(URL);
        return this;
    }

    /** true, если на странице избранного есть упоминание заголовка отеля. */
    public boolean containsHotelTitle(String titleFragment) {
        if (titleFragment == null || titleFragment.isBlank()) return false;
        String fragment = titleFragment.split("\n")[0].trim();
        if (fragment.length() > 30) fragment = fragment.substring(0, 30);
        String escaped = fragment.replace("'", "\\'");
        String xpath = String.format("//*[contains(normalize-space(.), '%s')]", escaped);
        return isDisplayedFast(By.xpath(xpath));
    }
}
