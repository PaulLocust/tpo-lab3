package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Базовый Page Object: общие операции ожидания, поиска и взаимодействия
 * с React-инпутами T-Travel.
 */
public class BasePage {

    private static final long WAIT_SEC = 20L;
    private static final long SHORT_WAIT_SEC = 5L;

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final WebDriverWait shortWait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
        this.shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_WAIT_SEC));
    }

    protected WebElement waitVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected WebElement waitClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    protected List<WebElement> waitVisibleElements(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    protected boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    protected boolean isDisplayedFast(By by) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void jsFocus(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);
    }

    /**
     * Корректно меняет значение в React-controlled input через нативный setter,
     * чтобы React-обработчик увидел изменение.
     */
    protected void setReactInputValue(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];"
                        + "var v = arguments[1];"
                        + "var setter = Object.getOwnPropertyDescriptor("
                        + "    window.HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(el, v);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));",
                input, value);
    }

    protected void pressEscape() {
        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
    }

    protected String parsePrice(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^0-9]", "");
    }

    protected long parsePriceAsLong(String raw) {
        String digits = parsePrice(raw);
        if (digits.isEmpty()) return -1L;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }
}
