package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Страница поиска отелей (/travel/hotels/new/).
 *
 * Реальные локаторы T-Travel:
 *  - input направления: data-qa-type='inputLocation.value.input'
 *  - дата (диапазон): data-qa-type='date-field' (по умолчанию заполнено)
 *  - гости: data-qa-type='desktopGuestField' (по умолчанию '2 гостя')
 *  - кнопка «Искать»: button[data-qa-type='SearchButton']
 *  - подсказки: div[role='option' and @data-qa-type='inputLocation.item']
 */
public class HotelSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/hotels/new/";

    private static final String XPATH_DESTINATION_INPUT =
            "//input[@data-qa-type='inputLocation.value.input']";

    private static final String XPATH_DATE_FIELD =
            "//div[@data-qa-type='date-field' or @data-qa-type='dateInputBoxDesktop']";

    private static final String XPATH_GUESTS_FIELD =
            "//div[@data-qa-type='guests-field' or @data-qa-type='desktopGuestField']";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='SearchButton']";

    private static final String XPATH_SUGGESTIONS =
            "//div[@role='option' and @data-qa-type='inputLocation.item']";

    private static final String XPATH_SUGGESTIONS_LISTBOX =
            "//div[@role='listbox' and @data-qa-type='inputLocation.list']";

    public HotelSearchPage(WebDriver driver) {
        super(driver);
    }

    public HotelSearchPage open() {
        driver.get(URL);
        waitVisible(By.xpath(XPATH_SEARCH_BUTTON));
        return this;
    }

    public boolean isDestinationInputVisible() {
        return isPresent(By.xpath(XPATH_DESTINATION_INPUT));
    }

    public boolean isDateFieldVisible() {
        return isPresent(By.xpath(XPATH_DATE_FIELD));
    }

    public boolean isGuestsFieldVisible() {
        return isPresent(By.xpath(XPATH_GUESTS_FIELD));
    }

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public HotelSearchPage typeDestination(String city) {
        setFieldValue(city);
        return this;
    }

    public HotelSearchPage clearDestination() {
        setFieldValue("");
        return this;
    }

    public String getDestinationValue() {
        return driver.findElement(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    private void setFieldValue(String text) {
        WebElement container = waitVisible(By.xpath(
                "//div[@data-qa-type='location-field' or @data-qa-type='inputLocation']"));
        scrollTo(container);
        try { container.click(); } catch (Exception e) { jsClick(container); }
        WebElement input = driver.findElement(By.xpath(XPATH_DESTINATION_INPUT));
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

    public HotelSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
        active.sendKeys(org.openqa.selenium.Keys.ENTER);
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
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/travel/hotels/new/"),
                    ExpectedConditions.urlContains("checkin="),
                    ExpectedConditions.urlMatches(".*hotels.*city.*"),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(.,'отел') or contains(.,'оценок')]"))));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(previousUrl)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
