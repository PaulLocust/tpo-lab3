package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Страница раздела «Туры» (/travel/tours/).
 *
 * Важно: на странице НЕТ инлайн-формы поиска. Есть только карточки
 * партнёров-турагентов (Travelata, Onlinetours, Level.Travel),
 * у каждого — кнопка-ссылка «Найти тур», ведущая на сайт партнёра.
 *
 * Поэтому тестируется наличие карточек партнёров, а не форма поиска.
 */
public class TourPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/tours/";

    private static final String XPATH_PARTNER_BUTTONS =
            "//a[@data-qa-type='tui/button' and normalize-space()='Найти тур']";

    private static final String XPATH_PARTNER_CARDS =
            "//a[@data-qa-type='uikit/clickable']";

    public TourPage(WebDriver driver) {
        super(driver);
    }

    public TourPage open() {
        driver.get(URL);
        // ждём, пока страница хотя бы загрузится по document.readyState
        wait.until(d -> "complete".equals(
                ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState")));
        return this;
    }

    public boolean isFindTourButtonVisible() {
        return isPresent(By.xpath(XPATH_PARTNER_BUTTONS));
    }

    public int getFindTourButtonsCount() {
        List<WebElement> items = driver.findElements(By.xpath(XPATH_PARTNER_BUTTONS));
        int n = 0;
        for (WebElement el : items) {
            if (el.isDisplayed()) n++;
        }
        return n;
    }

    public int getPartnerCardsCount() {
        List<WebElement> items = driver.findElements(By.xpath(XPATH_PARTNER_CARDS));
        int n = 0;
        for (WebElement el : items) {
            if (el.isDisplayed()) n++;
        }
        return n;
    }
}
