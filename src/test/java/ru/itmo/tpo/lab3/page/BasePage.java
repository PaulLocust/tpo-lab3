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

    protected List<WebElement> waitVisibleElements(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    protected WebElement waitClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    protected boolean isDisplayed(By by) {
        try {
            return waitVisible(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isDisplayedFast(By by) {
        try {
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isPresent(By by) {
        return !driver.findElements(by).isEmpty();
    }

    protected void waitForUrl(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected boolean urlContainsFast(String fragment) {
        try {
            return shortWait.until(ExpectedConditions.urlContains(fragment));
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void clearAndType(WebElement element, String text) {
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
    }

    protected void clearField(WebElement element) {
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
    }

    protected void pressEscape() {
        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
    }

    protected void clickOnBody() {
        ((JavascriptExecutor) driver).executeScript("document.body.click();");
    }

    /**
     * Закрытие баннеров cookies / маркетинговых попапов T-Bank.
     * Если попапа нет — просто молча выходим, тест продолжается.
     */
    public void closePopups() {
        By[] closeButtons = new By[]{
                By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'принять') or contains(., 'Принять')]"),
                By.xpath("//button[@aria-label='Закрыть' or @aria-label='Close']"),
                By.xpath("//button[normalize-space()='OK' or normalize-space()='Ок' or normalize-space()='Хорошо']")
        };
        for (By by : closeButtons) {
            try {
                List<WebElement> btns = driver.findElements(by);
                for (WebElement b : btns) {
                    if (b.isDisplayed()) {
                        try { b.click(); } catch (Exception ignored) {}
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
