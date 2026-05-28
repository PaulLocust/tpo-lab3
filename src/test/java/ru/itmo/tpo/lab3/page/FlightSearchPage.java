package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * UC-1 — стартовая страница поиска авиабилетов (/travel/flights/).
 * Здесь происходит ввод маршрута, выбор даты и запуск поиска.
 *
 * После нажатия «Найти» возвращается {@link FlightResultsPage}.
 */
public class FlightSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/flights/";

    private static final String XPATH_ORIGIN_CONTAINER =
            "//span[contains(@data-qa-type,'inputBox.label') and normalize-space()='Откуда']"
                    + "/ancestor::div[contains(@data-qa-type,'Suggest_')][1]";
    private static final String XPATH_DESTINATION_CONTAINER =
            "//span[contains(@data-qa-type,'inputBox.label') and normalize-space()='Куда']"
                    + "/ancestor::div[contains(@data-qa-type,'Suggest_')][1]";

    private static final String XPATH_ORIGIN_INPUT =
            XPATH_ORIGIN_CONTAINER + "//input[contains(@data-qa-type,'.value.input')]";
    private static final String XPATH_DESTINATION_INPUT =
            XPATH_DESTINATION_CONTAINER + "//input[contains(@data-qa-type,'.value.input')]";

    private static final String XPATH_DATE_FIELD =
            "//*[normalize-space()='Когда']/ancestor::div[@data-qa-type][1]";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='uikit/button' and normalize-space()='Найти']";

    private static final String XPATH_SUGGESTIONS =
            "//li[@data-qa-type='itemColumn']";

    private static final String XPATH_CALENDAR_CELL =
            "//*[@role='gridcell' and contains(@data-qa-type,'CalendarItem')"
                    + "      and not(contains(@data-qa-type,'disabled'))]";

    public FlightSearchPage(WebDriver driver) {
        super(driver);
    }

    public FlightSearchPage open() {
        driver.get(URL);
        waitVisible(By.xpath(XPATH_SEARCH_BUTTON));
        return this;
    }

    public FlightSearchPage typeOrigin(String city) {
        fillSuggestField(XPATH_ORIGIN_CONTAINER, XPATH_ORIGIN_INPUT, city);
        return this;
    }

    public FlightSearchPage typeDestination(String city) {
        fillSuggestField(XPATH_DESTINATION_CONTAINER, XPATH_DESTINATION_INPUT, city);
        return this;
    }

    public FlightSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(Keys.ARROW_DOWN);
        active.sendKeys(Keys.ENTER);
        return this;
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
    }

    /** Открывает виджет календаря и убеждается, что появились ячейки дней. */
    public FlightSearchPage openDatePicker() {
        WebElement el = waitClickable(By.xpath(XPATH_DATE_FIELD));
        scrollTo(el);
        try { el.click(); } catch (Exception e) { jsClick(el); }
        waitVisible(By.xpath(XPATH_CALENDAR_CELL));
        return this;
    }

    public boolean isCalendarOpened() {
        return isDisplayedFast(By.xpath(XPATH_CALENDAR_CELL));
    }

    /** Выбирает первую кликабельную ячейку в открытом календаре. */
    public FlightSearchPage pickFirstAvailableDate() {
        WebElement cell = waitClickable(By.xpath(XPATH_CALENDAR_CELL + "[1]"));
        scrollTo(cell);
        try { cell.click(); } catch (Exception e) { jsClick(cell); }
        return this;
    }

    public FlightResultsPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        try { btn.click(); } catch (Exception e) { jsClick(btn); }
        return new FlightResultsPage(driver).awaitResults();
    }

    public String getOriginValue() {
        return driver.findElement(By.xpath(XPATH_ORIGIN_INPUT)).getAttribute("value");
    }

    public String getDestinationValue() {
        return driver.findElement(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    private void fillSuggestField(String containerXpath, String inputXpath, String text) {
        WebElement container = waitVisible(By.xpath(containerXpath));
        scrollTo(container);
        try { container.click(); } catch (Exception e) { jsClick(container); }

        WebElement input = driver.findElement(By.xpath(inputXpath));
        setReactInputValue(input, "");
        jsFocus(input);
        if (!text.isEmpty()) {
            driver.switchTo().activeElement().sendKeys(text);
        }
    }
}
