package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * UC-2 — стартовая страница поиска отелей (/travel/hotels/).
 * После клика «Искать» возвращается {@link HotelResultsPage}.
 */
public class HotelSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/hotels/";

    private static final String XPATH_DESTINATION_INPUT =
            "//input[@data-qa-type='inputLocation.value.input']";

    private static final String XPATH_LOCATION_CONTAINER =
            "//div[@data-qa-type='location-field' or @data-qa-type='inputLocation']";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='SearchButton']";

    private static final String XPATH_SUGGESTIONS =
            "//div[@role='option' and @data-qa-type='inputLocation.item']";

    public HotelSearchPage(WebDriver driver) {
        super(driver);
    }

    public HotelSearchPage open() {
        driver.get(URL);
        waitVisible(By.xpath(XPATH_SEARCH_BUTTON));
        return this;
    }

    public HotelSearchPage typeDestination(String city) {
        WebElement container = waitVisible(By.xpath(XPATH_LOCATION_CONTAINER));
        scrollTo(container);
        try { container.click(); } catch (Exception e) { jsClick(container); }

        WebElement input = driver.findElement(By.xpath(XPATH_DESTINATION_INPUT));
        setReactInputValue(input, "");
        jsFocus(input);
        if (!city.isEmpty()) {
            driver.switchTo().activeElement().sendKeys(city);
        }
        return this;
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
    }

    public HotelSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(Keys.ARROW_DOWN);
        active.sendKeys(Keys.ENTER);
        return this;
    }

    public HotelResultsPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        try { btn.click(); } catch (Exception e) { jsClick(btn); }
        return new HotelResultsPage(driver).awaitResults();
    }

    public String getDestinationValue() {
        return driver.findElement(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }
}
