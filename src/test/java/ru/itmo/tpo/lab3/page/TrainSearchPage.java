package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Страница поиска ЖД-билетов (/travel/trains/).
 *
 * Реальные локаторы T-Travel:
 *  - input «Откуда»: data-qa-type='originField.field'
 *  - input «Куда»:  data-qa-type='destinationField.field'
 *  - дата «Когда»:  data-qa-type='dateFieldDesktop.field'
 *  - пассажиры:     data-qa-type='passengerInputDesktop.field' (default '1 взрослый')
 *  - кнопка поиска: button[data-qa-type='searchButton'] (текст 'Найти на OneTwoTrip')
 *  - подсказки:     div[role='option' and @data-qa-type='tui/dropdown-menu-item']
 */
public class TrainSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/trains/";

    private static final String XPATH_ORIGIN_INPUT =
            "//input[@data-qa-type='originField.field']";
    private static final String XPATH_DESTINATION_INPUT =
            "//input[@data-qa-type='destinationField.field']";
    private static final String XPATH_DATE_INPUT =
            "//input[@data-qa-type='dateFieldDesktop.field']";
    private static final String XPATH_PASSENGERS_INPUT =
            "//input[@data-qa-type='passengerInputDesktop.field']";
    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='searchButton']";
    private static final String XPATH_SUGGESTIONS =
            "//div[@role='option' and @data-qa-type='tui/dropdown-menu-item']";
    private static final String XPATH_SUGGESTIONS_LISTBOX =
            "//div[@role='listbox' and @data-qa-type='tui/dropdown-menu']";

    public TrainSearchPage(WebDriver driver) {
        super(driver);
    }

    public TrainSearchPage open() {
        driver.get(URL);
        waitVisible(By.xpath(XPATH_SEARCH_BUTTON));
        return this;
    }

    public boolean isOriginInputVisible() {
        return isPresent(By.xpath(XPATH_ORIGIN_INPUT));
    }

    public boolean isDestinationInputVisible() {
        return isPresent(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isDateInputVisible() {
        return isPresent(By.xpath(XPATH_DATE_INPUT));
    }

    public boolean isPassengersInputVisible() {
        return isPresent(By.xpath(XPATH_PASSENGERS_INPUT));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public TrainSearchPage typeOrigin(String city) {
        setFieldValue("originField", XPATH_ORIGIN_INPUT, city);
        return this;
    }

    public TrainSearchPage typeDestination(String city) {
        setFieldValue("destinationField", XPATH_DESTINATION_INPUT, city);
        return this;
    }

    public TrainSearchPage clearOrigin() {
        setFieldValue("originField", XPATH_ORIGIN_INPUT, "");
        return this;
    }

    public TrainSearchPage clearDestination() {
        setFieldValue("destinationField", XPATH_DESTINATION_INPUT, "");
        return this;
    }

    private void setFieldValue(String fieldName, String inputXpath, String text) {
        WebElement container = waitVisible(By.xpath(
                "//div[@data-qa-type='" + fieldName + "']"));
        scrollTo(container);
        try { container.click(); } catch (Exception e) { jsClick(container); }
        WebElement input = driver.findElement(By.xpath(inputXpath));
        jsFocus(input);
        setReactInputValue(input, "");
        if (!text.isEmpty()) {
            driver.switchTo().activeElement().sendKeys(text);
        }
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
    }

    public boolean isSuggestionsListboxVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS_LISTBOX));
    }

    public int getSuggestionsCount() {
        try {
            List<WebElement> items = driver.findElements(By.xpath(XPATH_SUGGESTIONS));
            int count = 0;
            for (WebElement item : items) {
                if (item.isDisplayed() && !item.getText().isBlank()) count++;
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean anySuggestionContains(String fragment) {
        try {
            for (WebElement el : driver.findElements(By.xpath(XPATH_SUGGESTIONS))) {
                if (el.isDisplayed()) {
                    String text = el.getText();
                    if (text != null && text.toLowerCase().contains(fragment.toLowerCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public TrainSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
        active.sendKeys(org.openqa.selenium.Keys.ENTER);
        return this;
    }

    public String getOriginValue() {
        return driver.findElement(By.xpath(XPATH_ORIGIN_INPUT)).getAttribute("value");
    }

    public String getDestinationValue() {
        return driver.findElement(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    public String getPassengersValue() {
        return driver.findElement(By.xpath(XPATH_PASSENGERS_INPUT)).getAttribute("value");
    }

    public TrainSearchPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    /** «Найти на OneTwoTrip» открывает новую вкладку. Проверяем рост числа окон. */
    public boolean isResultsOpened() {
        try {
            int initial = driver.getWindowHandles().size();
            return wait.until(d -> d.getWindowHandles().size() > initial
                    || d.getCurrentUrl().contains("trains")
                    && !d.getCurrentUrl().endsWith("/travel/trains/"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(previousUrl)));
            int handles = driver.getWindowHandles().size();
            return handles == 1;
        } catch (Exception e) {
            return driver.getWindowHandles().size() == 1;
        }
    }
}
