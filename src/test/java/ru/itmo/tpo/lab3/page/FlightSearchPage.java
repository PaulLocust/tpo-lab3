package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Страница поиска авиабилетов (/travel/flights/).
 */
public class FlightSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/flights/";

    private static final String XPATH_ORIGIN_INPUT =
            "//input[@placeholder='Откуда' or @aria-label='Откуда' or contains(@placeholder,'Откуда')]";
    private static final String XPATH_DESTINATION_INPUT =
            "//input[@placeholder='Куда' or @aria-label='Куда' or contains(@placeholder,'Куда')]";

    private static final String XPATH_SUGGESTIONS =
            "//ul[@role='listbox']//li"
                    + " | //div[@role='listbox']//div[@role='option']"
                    + " | //*[contains(@data-qa-type,'suggest') or contains(@data-test,'suggest')]"
                    + "   //*[self::li or self::div][string-length(normalize-space(.))>0]";

    private static final String XPATH_DATE_DEPARTURE =
            "//input[@placeholder='Туда' or @aria-label='Туда' or contains(@placeholder,'Туда')]";
    private static final String XPATH_DATE_RETURN =
            "//input[@placeholder='Обратно' or @aria-label='Обратно' or contains(@placeholder,'Обратно')]";

    private static final String XPATH_SWAP_BUTTON =
            "//button[@aria-label='Поменять местами' or @aria-label='Поменять города'"
                    + " or contains(@aria-label,'омен') or contains(@data-qa-type,'swap')]";

    private static final String XPATH_PASSENGERS_BUTTON =
            "//button[contains(.,'пассажир') or contains(.,'Эконом') or contains(.,'Бизнес')]"
                    + " | //*[@aria-label='Пассажиры' or contains(@aria-label,'ассажир')]";

    private static final String XPATH_PASSENGERS_PANEL =
            "//*[contains(@role,'dialog') or contains(@class,'opover') or contains(@class,'opup')"
                    + " or contains(@class,'enu')]"
                    + "[.//text()[contains(., 'Эконом') or contains(., 'Бизнес') or contains(., 'пассажир')]]";

    private static final String XPATH_BUSINESS_OPTION =
            "//*[contains(@role,'menuitem') or self::button or self::label]"
                    + "[normalize-space()='Бизнес' or contains(.,'Бизнес')]";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[normalize-space()='Найти билеты' or normalize-space()='Найти'"
                    + " or contains(.,'Найти билеты')]";

    private static final String XPATH_CALENDAR =
            "//*[@role='dialog' or contains(@class,'alendar') or contains(@class,'atepicker')]"
                    + "[.//*[self::button or self::div][string-length(normalize-space(.))=1"
                    + "  or string-length(normalize-space(.))=2]]";

    public FlightSearchPage(WebDriver driver) {
        super(driver);
    }

    public FlightSearchPage open() {
        driver.get(URL);
        return this;
    }

    public boolean isOriginInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_ORIGIN_INPUT));
    }

    public boolean isDestinationInputVisible() {
        return isDisplayedFast(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public boolean isSwapButtonVisible() {
        return isPresent(By.xpath(XPATH_SWAP_BUTTON));
    }

    public boolean isDepartureDateVisible() {
        return isPresent(By.xpath(XPATH_DATE_DEPARTURE));
    }

    public boolean isReturnDateFieldVisible() {
        return isPresent(By.xpath(XPATH_DATE_RETURN));
    }

    public FlightSearchPage typeOrigin(String city) {
        clearAndType(waitClickable(By.xpath(XPATH_ORIGIN_INPUT)), city);
        return this;
    }

    public FlightSearchPage typeDestination(String city) {
        clearAndType(waitClickable(By.xpath(XPATH_DESTINATION_INPUT)), city);
        return this;
    }

    public FlightSearchPage clearOrigin() {
        clearField(waitClickable(By.xpath(XPATH_ORIGIN_INPUT)));
        return this;
    }

    public FlightSearchPage clearDestination() {
        clearField(waitClickable(By.xpath(XPATH_DESTINATION_INPUT)));
        return this;
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
    }

    /** Количество реально видимых пунктов автокомплита. 0 — если выпадашки нет. */
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

    public FlightSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        List<WebElement> items = waitVisibleElements(By.xpath(XPATH_SUGGESTIONS));
        for (WebElement item : items) {
            if (item.isDisplayed()) {
                scrollTo(item);
                item.click();
                return this;
            }
        }
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
        return this;
    }

    public String getOriginValue() {
        return waitVisible(By.xpath(XPATH_ORIGIN_INPUT)).getAttribute("value");
    }

    public String getDestinationValue() {
        return waitVisible(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    public FlightSearchPage clickSwap() {
        WebElement btn = waitClickable(By.xpath(XPATH_SWAP_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public FlightSearchPage openDepartureDatePicker() {
        WebElement field = waitClickable(By.xpath(XPATH_DATE_DEPARTURE));
        scrollTo(field);
        field.click();
        return this;
    }

    public boolean isCalendarVisible() {
        return isDisplayedFast(By.xpath(XPATH_CALENDAR));
    }

    public boolean isCalendarHidden() {
        try {
            return shortWait.until(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath(XPATH_CALENDAR)));
        } catch (Exception e) {
            return !isPresent(By.xpath(XPATH_CALENDAR));
        }
    }

    public FlightSearchPage closeCalendarWithEscape() {
        pressEscape();
        return this;
    }

    public FlightSearchPage openPassengersPanel() {
        WebElement btn = waitClickable(By.xpath(XPATH_PASSENGERS_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public boolean isPassengersPanelOpen() {
        return isDisplayedFast(By.xpath(XPATH_PASSENGERS_PANEL));
    }

    public FlightSearchPage selectBusinessClass() {
        if (!isPassengersPanelOpen()) {
            openPassengersPanel();
        }
        WebElement option = waitClickable(By.xpath(XPATH_BUSINESS_OPTION));
        scrollTo(option);
        option.click();
        return this;
    }

    public boolean isBusinessClassSelected() {
        By by = By.xpath(
                "//*[contains(.,'Бизнес')][@aria-selected='true'"
                        + " or contains(@class,'elected') or contains(@class,'ctive')]"
                        + " | //button[contains(.,'Бизнес')]");
        return isPresent(by);
    }

    public FlightSearchPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public boolean isResultsOpened() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("search"),
                    ExpectedConditions.urlContains("results"),
                    ExpectedConditions.urlMatches(".*from=.*to=.*"),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(.,'найдено') or contains(.,'рейс')]"))));
        } catch (Exception e) {
            return false;
        }
    }

    /** true, если URL не изменился (форма не приняла отправку). */
    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(previousUrl)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
