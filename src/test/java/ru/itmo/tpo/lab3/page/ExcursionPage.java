package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * UC-6 — раздел «Экскурсии».
 *
 * Раздел делегирован партнёру. Тест проверяет переход на партнёрскую страницу.
 */
public class ExcursionPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/excursions/";

    private static final String XPATH_PARTNER_LINK =
            "//a[contains(translate(., 'НАЙТИЭКСУРИ', 'найтиэксури'),'найти')"
                    + " or contains(translate(., 'ВЫБРАТЬ', 'выбрать'),'выбрать')"
                    + " or contains(translate(., 'ПОДОБРАТЬ', 'подобрать'),'подобрать')]";

    public ExcursionPage(WebDriver driver) {
        super(driver);
    }

    public ExcursionPage open() {
        driver.get(URL);
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
        return this;
    }

    public boolean isAnyPartnerLinkVisible() {
        return isDisplayedFast(By.xpath(XPATH_PARTNER_LINK));
    }

    public String openFirstPartnerLink() {
        String originalHandle = driver.getWindowHandle();
        int handlesBefore = driver.getWindowHandles().size();
        String originalUrl = driver.getCurrentUrl();

        WebElement link = waitClickable(By.xpath("(" + XPATH_PARTNER_LINK + ")[1]"));
        scrollTo(link);
        try { link.click(); } catch (Exception e) { jsClick(link); }

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
