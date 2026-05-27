package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Страница поиска авиабилетов (/travel/flights/).
 *
 * Реальные локаторы T-Travel получены сканером DomDiscovery (см. dom-discovery*.txt):
 *  - input  «Откуда»/«Куда»: data-qa-type содержит '.value.input' и 'Suggest_'.
 *  - кнопка «Найти»: data-qa-type='uikit/button', текст 'Найти'.
 *  - кнопка «Сложный маршрут»: текст 'Сложный маршрут'.
 *  - тогглы «Лечу по работе» и «Открыть отели в новой вкладке».
 *  - подсказки автокомплита: li[data-qa-type='itemColumn'].
 *  - календарь дат: span[role='gridcell' и data-qa-type содержит 'CalendarItem'].
 *  - селектор пассажиров: div[data-qa-type='InputBox_not-focused'][.//text()='Пассажиры'].
 *
 * По умолчанию форма уже частично заполнена (Откуда='Санкт-Петербург', Куда=''),
 * поэтому тесты «пустой формы» сначала очищают Откуда.
 */
public class FlightSearchPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/flights/";

    // Сам <input> в T-Travel визуально невидим (его перекрывает div-placeholder),
    // поэтому Selenium считает его не interactable. Стратегия:
    //  - для клика/фокуса → кликаем по корневому КОНТЕЙНЕРУ Suggest (см. CONTAINER xpath),
    //  - для чтения value → читаем атрибут самого input напрямую (см. INPUT xpath).
    private static final String XPATH_ORIGIN_CONTAINER =
            "//span[contains(@data-qa-type,'inputBox.label') and normalize-space()='Откуда']"
                    + "/ancestor::div[contains(@data-qa-type,'Suggest_')"
                    + "                and contains(@data-qa-type,'_no-error')"
                    + "                and not(contains(@data-qa-type,'.'))][1]";
    private static final String XPATH_DESTINATION_CONTAINER =
            "//span[contains(@data-qa-type,'inputBox.label') and normalize-space()='Куда']"
                    + "/ancestor::div[contains(@data-qa-type,'Suggest_')"
                    + "                and contains(@data-qa-type,'_no-error')"
                    + "                and not(contains(@data-qa-type,'.'))][1]";

    private static final String XPATH_ORIGIN_INPUT =
            XPATH_ORIGIN_CONTAINER + "//input[contains(@data-qa-type,'.value.input')]";
    private static final String XPATH_DESTINATION_INPUT =
            XPATH_DESTINATION_CONTAINER + "//input[contains(@data-qa-type,'.value.input')]";

    private static final String XPATH_DATE_FIELD =
            "//div[contains(@data-qa-type,'DateTextInput')]"
                    + " | //*[normalize-space()='Когда']/ancestor::div[@data-qa-type][1]";

    private static final String XPATH_PASSENGERS_FIELD =
            "//div[@data-qa-type='InputBox_not-focused'][.//text()[contains(.,'Пассажиры')]]"
                    + " | //div[@data-qa-file='PassengersSelector']";

    private static final String XPATH_PASSENGERS_ARROW =
            "//div[@data-qa-type='Arrow_notopened' or @data-qa-type='Arrow_opened']";

    private static final String XPATH_SEARCH_BUTTON =
            "//button[@data-qa-type='uikit/button' and normalize-space()='Найти']";

    private static final String XPATH_COMPLEX_ROUTE_BUTTON =
            "//button[normalize-space()='Сложный маршрут']";

    private static final String XPATH_WORK_TRIP_TOGGLE =
            "//input[@data-qa-type='WorkTripToggle.input']";

    private static final String XPATH_SUGGESTIONS =
            "//li[@data-qa-type='itemColumn']";

    private static final String XPATH_CALENDAR_CELL =
            "//*[@role='gridcell' and contains(@data-qa-type,'CalendarItem')]";

    public FlightSearchPage(WebDriver driver) {
        super(driver);
    }

    public FlightSearchPage open() {
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

    public boolean isSearchButtonVisible() {
        return isDisplayedFast(By.xpath(XPATH_SEARCH_BUTTON));
    }

    public boolean isDepartureDateVisible() {
        return isPresent(By.xpath(XPATH_DATE_FIELD));
    }

    public boolean isComplexRouteButtonVisible() {
        return isPresent(By.xpath(XPATH_COMPLEX_ROUTE_BUTTON));
    }

    public boolean isWorkTripTogglePresent() {
        return isPresent(By.xpath(XPATH_WORK_TRIP_TOGGLE));
    }

    public FlightSearchPage typeOrigin(String city) {
        setFieldValue(XPATH_ORIGIN_CONTAINER, XPATH_ORIGIN_INPUT, city);
        return this;
    }

    public FlightSearchPage typeDestination(String city) {
        setFieldValue(XPATH_DESTINATION_CONTAINER, XPATH_DESTINATION_INPUT, city);
        return this;
    }

    public FlightSearchPage clearOrigin() {
        setFieldValue(XPATH_ORIGIN_CONTAINER, XPATH_ORIGIN_INPUT, "");
        return this;
    }

    public FlightSearchPage clearDestination() {
        setFieldValue(XPATH_DESTINATION_CONTAINER, XPATH_DESTINATION_INPUT, "");
        return this;
    }

    /**
     * Универсальный ввод: кликаем контейнер (открывается дропдаун Suggest),
     * затем используем JS-сеттер чтобы корректно обновить React-controlled input,
     * после этого реальный sendKeys эмулирует пользовательский набор (keydown/keyup),
     * чтобы React показал отфильтрованные подсказки.
     */
    private void setFieldValue(String containerXpath, String inputXpath, String text) {
        WebElement container = waitVisible(By.xpath(containerXpath));
        scrollTo(container);
        try { container.click(); } catch (Exception e) { jsClick(container); }

        WebElement input = driver.findElement(By.xpath(inputXpath));
        // 1) сбросить текущее значение через React-friendly сеттер
        setReactInputValue(input, "");
        // 2) сфокусировать input через JS — sendKeys будет уходить именно сюда
        jsFocus(input);
        if (!text.isEmpty()) {
            // 3) симулируем реальный набор
            driver.switchTo().activeElement().sendKeys(text);
        }
    }

    public boolean isSuggestionsVisible() {
        return isDisplayedFast(By.xpath(XPATH_SUGGESTIONS));
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

    /** Есть ли среди видимых подсказок хотя бы одна, чей текст содержит подстроку. */
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

    public FlightSearchPage chooseFirstSuggestion() {
        waitVisible(By.xpath(XPATH_SUGGESTIONS));
        // Используем клавиатурную навигацию: Down → Enter.
        // Это надёжнее, чем кликать по li[itemColumn], которое React
        // может перерисовывать или менять до click-евента.
        WebElement active = driver.switchTo().activeElement();
        active.sendKeys(Keys.ARROW_DOWN);
        active.sendKeys(Keys.ENTER);
        return this;
    }

    public String getOriginValue() {
        // input визуально невидим, но атрибут value читается без проблем
        return driver.findElement(By.xpath(XPATH_ORIGIN_INPUT)).getAttribute("value");
    }

    public String getDestinationValue() {
        return driver.findElement(By.xpath(XPATH_DESTINATION_INPUT)).getAttribute("value");
    }

    public FlightSearchPage openDepartureDatePicker() {
        WebElement el = waitClickable(By.xpath(XPATH_DATE_FIELD));
        scrollTo(el);
        el.click();
        return this;
    }

    public boolean isCalendarVisible() {
        return isDisplayedFast(By.xpath(XPATH_CALENDAR_CELL));
    }

    public boolean isCalendarHidden() {
        try {
            return shortWait.until(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath(XPATH_CALENDAR_CELL)));
        } catch (Exception e) {
            return !isPresent(By.xpath(XPATH_CALENDAR_CELL));
        }
    }

    public FlightSearchPage closeCalendarWithEscape() {
        pressEscape();
        return this;
    }

    public FlightSearchPage openPassengersPanel() {
        WebElement el = waitClickable(By.xpath(XPATH_PASSENGERS_FIELD));
        scrollTo(el);
        el.click();
        return this;
    }

    public boolean isPassengersPanelOpen() {
        // Признак открытой панели — стрелка перешла в состояние 'opened',
        // или появилась всплывашка с текстом 'Эконом'/'Бизнес'.
        return isPresent(By.xpath("//div[@data-qa-type='Arrow_opened']"))
                || isPresent(By.xpath("//*[contains(@data-qa-type,'popover') "
                + "or contains(@data-qa-type,'popup') or @role='dialog']"
                + "[.//text()[contains(.,'Эконом') or contains(.,'Бизнес')]]"));
    }

    public FlightSearchPage clickComplexRoute() {
        WebElement btn = waitClickable(By.xpath(XPATH_COMPLEX_ROUTE_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    public FlightSearchPage toggleWorkTrip() {
        WebElement toggle = driver.findElement(By.xpath(XPATH_WORK_TRIP_TOGGLE));
        scrollTo(toggle);
        // input[type=checkbox] часто скрыт визуально — кликаем по обёртке label/span
        try {
            toggle.click();
        } catch (Exception e) {
            WebElement wrapper = driver.findElement(
                    By.xpath("//span[@data-qa-type='WorkTripToggle']"));
            wrapper.click();
        }
        return this;
    }

    public boolean isWorkTripChecked() {
        try {
            WebElement el = driver.findElement(By.xpath(XPATH_WORK_TRIP_TOGGLE));
            return el.isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    public FlightSearchPage clickSearch() {
        WebElement btn = waitClickable(By.xpath(XPATH_SEARCH_BUTTON));
        scrollTo(btn);
        btn.click();
        return this;
    }

    /** Дождаться появления страницы выдачи рейсов. */
    public boolean isResultsOpened() {
        try {
            return wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("search"),
                    ExpectedConditions.urlContains("flights/routes"),
                    ExpectedConditions.urlContains("?from="),
                    ExpectedConditions.urlMatches(".*from=[A-Z]{3}.*to=[A-Z]{3}.*"),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(.,'найдено') or contains(.,'Загружаем') "
                                    + "or contains(.,'рейс')]"))));
        } catch (Exception e) {
            return false;
        }
    }

    /** true, если URL за короткий таймаут не изменился — поиск не запустился. */
    public boolean stayedOnSearchForm(String previousUrl) {
        try {
            shortWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(previousUrl)));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
