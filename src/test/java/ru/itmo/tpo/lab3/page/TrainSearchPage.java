package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * UC-3 — раздел «Поезда» (/travel/trains/).
 *
 * Поиск ЖД делегирован партнёру OneTwoTrip, поэтому тестируется только сам
 * переход на партнёрский домен.
 */
public class TrainSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/trains/";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='searchButton'] "
                    + "| //a[contains(translate(., 'НАЙТИ', 'найти'),'найти')]";

    public TrainSearchPage(WebDriver driver) {
        super(driver);
    }

    public TrainSearchPage open() {
        driver.get(URL);
        waitVisible(By.xpath(XPATH_SEARCH_BUTTON));
        return this;
    }

    /**
     * Кликает кнопку поиска и возвращает URL, на который произошёл переход
     * (с учётом возможной новой вкладки). Если переход не произошёл — текущий.
     */
    public String clickSearchAndCaptureUrl() {
        String originalHandle = driver.getWindowHandle();
        int handlesBefore = driver.getWindowHandles().size();
        String originalUrl = driver.getCurrentUrl();

        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        try { btn.click(); } catch (Exception e) { jsClick(btn); }

        try {
            wait.until(d -> d.getWindowHandles().size() > handlesBefore
                    || !d.getCurrentUrl().equals(originalUrl));
        } catch (Exception ignored) {
        }

        if (driver.getWindowHandles().size() > handlesBefore) {
            for (String h : driver.getWindowHandles()) {
                if (!h.equals(originalHandle)) {
                    driver.switchTo().window(h);
                    return driver.getCurrentUrl();
                }
            }
        }
        return driver.getCurrentUrl();
    }
}
