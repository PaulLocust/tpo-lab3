package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * UC-4 — раздел «Туры» (/travel/tours/).
 *
 * На странице нет инлайн-формы поиска: только карточки партнёров-турагентов
 * с кнопкой «Найти тур». Тест проверяет, что клик по карточке открывает
 * партнёрскую страницу с туром.
 */
public class TourPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/tours/";

    private static final String XPATH_PARTNER_BUTTONS =
            "//a[@data-qa-type='tui/button' and normalize-space()='Найти тур']";

    public TourPage(WebDriver driver) {
        super(driver);
    }

    public TourPage open() {
        driver.get(URL);
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
        waitVisible(By.xpath(XPATH_PARTNER_BUTTONS));
        return this;
    }

    public int partnerButtonsCount() {
        return driver.findElements(By.xpath(XPATH_PARTNER_BUTTONS)).size();
    }

    /** Кликает по первой партнёрской кнопке и возвращает URL, на который попали. */
    public String openFirstPartnerCard() {
        String originalHandle = driver.getWindowHandle();
        int handlesBefore = driver.getWindowHandles().size();
        String originalUrl = driver.getCurrentUrl();

        WebElement btn = waitClickable(By.xpath("(" + XPATH_PARTNER_BUTTONS + ")[1]"));
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
