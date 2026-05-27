package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Страница поиска отелей (/travel/hotels/new/).
 */
public class HotelSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/hotels/new/";

    private static final String XPATH_DESTINATION_INPUT =
            "//input[contains(@placeholder,'Город') or contains(@placeholder,'отел')"
                    + " or contains(@placeholder,'Куда') or contains(@aria-label,'Город')"
                    + " or contains(@aria-label,'Куда')]";

    private static final String XPATH_CHECK_IN =
            "//input[contains(@placeholder,'Заезд') or contains(@aria-label,'Заезд')"
                    + " or contains(@placeholder,'Дата заезда')]";

    private static final String XPATH_CHECK_OUT =
            "//input[contains(@placeholder,'Выезд') or contains(@aria-label,'Выезд')"
                    + " or contains(@placeholder,'Дата выезда')]";

    private static final String XPATH_GUESTS_BUTTON =
            "//*[self::button or self::div][contains(.,'гост') or contains(.,'Гост')]"
                    + "[.//*[contains(@aria-label,'+') or contains(@aria-label,'-')]"
                    + " or contains(@role,'button')]";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[normalize-space()='Найти' or contains(.,'Найти отел') or contains(.,'Найти')]";

    private static final String XPATH_SUGGESTIONS =
            "//ul[@role='listbox']//li"
                    + " | //div[@role='listbox']//div[@role='option']"
                    + " | //*[contains(@data-qa-type,'suggest')]//li";

    public HotelSearchPage(WebDriver driver) {
        super(driver);
    }

    public HotelSearchPage open() {
        driver.get(URL);
        return this;
    }

    public boolean isDestinationInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isCheckInVisible() {
        return isPresent(By.xpath(XPATH_CHECK_IN));
    }

    public boolean isCheckOutVisible() {
        return isPresent(By.xpath(XPATH_CHECK_OUT));
    }

    public boolean isGuestsButtonVisible() {
        return isPresent(By.xpath(XPATH_GUESTS_BUTTON));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public HotelSearchPage typeDestination(String city) {
        clearAndType(waitClickable(By.xpath(XPATH_DESTINATION_INPUT)), city);
        return this;
    }

    public HotelSearchPage clearDestination() {
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

    /** true, если URL за короткий таймаут не изменился — форма не приняла отправку. */
    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .not(org.openqa.selenium.support.ui.ExpectedConditions.urlToBe(previousUrl)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public HotelSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        List<WebElement> items = waitVisibleElements(By.xpath(XPATH_SUGGESTIONS));
        for (WebElement item : items) {
            if (item.isDisplayed()) {
                scrollTo(item);
                item.click();
                return this;
            }
        }
        return this;
    }

    public HotelSearchPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public boolean isResultsOpened() {
        try {
            waitForUrl("hotels");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
