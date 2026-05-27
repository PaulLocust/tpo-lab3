package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Страница поиска ЖД-билетов (/travel/trains/).
 */
public class TrainSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/trains/";

    private static final String XPATH_ORIGIN_INPUT =
            "//input[@placeholder='Откуда' or @aria-label='Откуда' or contains(@placeholder,'Откуда')]";
    private static final String XPATH_DESTINATION_INPUT =
            "//input[@placeholder='Куда' or @aria-label='Куда' or contains(@placeholder,'Куда')]";
    private static final String XPATH_DATE_INPUT =
            "//input[contains(@placeholder,'Туда') or contains(@placeholder,'Дата')"
                    + " or contains(@aria-label,'Дата')]";
    private static final String XPATH_SEARCH_BUTTON =
            "//button[normalize-space()='Найти билеты' or normalize-space()='Найти'"
                    + " or contains(.,'Найти')]";
    private static final String XPATH_SUGGESTIONS =
            "//ul[@role='listbox']//li | //div[@role='listbox']//div[@role='option']";

    public TrainSearchPage(WebDriver driver) {
        super(driver);
    }

    public TrainSearchPage open() {
        driver.get(URL);
        return this;
    }

    public boolean isOriginInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_ORIGIN_INPUT));
    }

    public boolean isDestinationInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isDateInputVisible() {
        return isPresent(By.xpath(XPATH_DATE_INPUT));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public TrainSearchPage typeOrigin(String city) {
        clearAndType(waitClickable(By.xpath(XPATH_ORIGIN_INPUT)), city);
        return this;
    }

    public TrainSearchPage typeDestination(String city) {
        clearAndType(waitClickable(By.xpath(XPATH_DESTINATION_INPUT)), city);
        return this;
    }

    public TrainSearchPage clearOrigin() {
        clearField(waitClickable(By.xpath(XPATH_ORIGIN_INPUT)));
        return this;
    }

    public TrainSearchPage clearDestination() {
        clearField(waitClickable(By.xpath(XPATH_DESTINATION_INPUT)));
        return this;
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
    }

    public int getSuggestionsCount() {
        try {
            List<WebElement> items = driver.findElements(By.xpath(XPATH_SUGGESTIONS));
            int count = 0;
            for (WebElement item : items) {
                if (item.isDisplayed() && !item.getText().isBlank()) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getOriginValue() {
        return waitVisible(By.xpath(XPATH_ORIGIN_INPUT)).getAttribute("value");
    }

    public String getDestinationValue() {
        return waitVisible(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .not(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(previousUrl)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public TrainSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        List<WebElement> items = waitVisibleElements(By.xpath(XPATH_SUGGESTIONS));
        for (WebElement el : items) {
            if (el.isDisplayed()) {
                scrollTo(el);
                el.click();
                return this;
            }
        }
        return this;
    }

    public TrainSearchPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public boolean isResultsOpened() {
        try {
            waitForUrl("trains");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
