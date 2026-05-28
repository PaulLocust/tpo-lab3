package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Страница результатов поиска авиабилетов.
 *
 * Используется для UC-9..UC-13: применение фильтров и сортировки,
 * чтение карточек рейсов и проверка их соответствия выбранным условиям.
 */
public class FlightResultsPage extends BasePage {

    private static final String XPATH_FLIGHT_CARD =
            "//div[@data-qa-type='ticketCardItem' or @data-qa-type='flightCard' "
                    + "or contains(@data-qa-type,'TicketCard')]";

    private static final String XPATH_CARD_PRICE =
            ".//*[contains(@data-qa-type,'price') or contains(@data-qa-type,'Price')]";

    private static final String XPATH_CARD_AIRLINE =
            ".//*[contains(@data-qa-type,'airline') or contains(@data-qa-type,'Airline') "
                    + "or contains(@data-qa-type,'carrier')]";

    private static final String XPATH_CARD_BAGGAGE =
            ".//*[contains(translate(., 'БАГЖ', 'багж'),'багаж')]";

    private static final String XPATH_CARD_SEGMENTS =
            ".//*[contains(@data-qa-type,'segment') or contains(@data-qa-type,'route')]";

    private static final String XPATH_CARD_STOPS_LABEL =
            ".//*[contains(translate(., 'ПЕРСАДКАБЗ', 'персадкабз'),'пересад') "
                    + "or contains(translate(., 'ПРЯМОЙ', 'прямой'),'прямой')]";

    private static final String XPATH_CARD_DEPARTURE_TIME =
            ".//*[contains(@data-qa-type,'departure') and contains(@data-qa-type,'time')]";

    private static final String XPATH_FILTER_LABEL_FMT =
            "//label[.//text()[contains(translate(., 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ',"
                    + " 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'),'%s')]]";

    private static final String XPATH_SORT_BUTTON_CHEAPER =
            "//*[self::button or self::a or self::div]"
                    + "[contains(translate(., 'ДЕШВЛ', 'дешвл'),'дешев')]";

    private static final String XPATH_SELECT_BUTTON =
            ".//*[self::button or self::a]"
                    + "[contains(translate(., 'ВЫБРАТЬ', 'выбрать'),'выбрать')]";

    private static final String XPATH_CONTINUE_BUTTON =
            "//*[self::button or self::a]"
                    + "[contains(translate(., 'ПРОДОЛЖИТЬ', 'продолжить'),'продолжить')]";

    public FlightResultsPage(WebDriver driver) {
        super(driver);
    }

    /** Дождаться, что страница выдачи отрисована (по url-фрагменту или карточкам). */
    public FlightResultsPage awaitResults() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("search"),
                    ExpectedConditions.urlContains("from="),
                    ExpectedConditions.urlMatches(".*from=[A-Z]{3}.*to=[A-Z]{3}.*"),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_FLIGHT_CARD))));
        } catch (Exception ignored) {
        }
        return this;
    }

    public boolean isOpened() {
        String url = driver.getCurrentUrl();
        return url.contains("search") || url.contains("from=") || isAnyCardPresent();
    }

    public boolean isAnyCardPresent() {
        return isPresent(By.xpath(XPATH_FLIGHT_CARD));
    }

    public int cardsCount() {
        return driver.findElements(By.xpath(XPATH_FLIGHT_CARD)).size();
    }

    /** Кликает чекбокс/радио фильтра по подписи (без учёта регистра). */
    public FlightResultsPage toggleFilterByLabel(String labelFragmentLowercase) {
        String xpath = String.format(XPATH_FILTER_LABEL_FMT, labelFragmentLowercase);
        WebElement label = waitClickable(By.xpath(xpath));
        scrollTo(label);
        try { label.click(); } catch (Exception e) { jsClick(label); }
        return this;
    }

    /** Включает сортировку «Сначала дешевле». */
    public FlightResultsPage sortByCheapest() {
        WebElement sort = waitClickable(By.xpath(XPATH_SORT_BUTTON_CHEAPER));
        scrollTo(sort);
        try { sort.click(); } catch (Exception e) { jsClick(sort); }
        return this;
    }

    public List<Long> readPrices() {
        List<Long> prices = new ArrayList<>();
        for (WebElement card : driver.findElements(By.xpath(XPATH_FLIGHT_CARD))) {
            try {
                WebElement p = card.findElement(By.xpath(XPATH_CARD_PRICE));
                long val = parsePriceAsLong(p.getText());
                if (val > 0) prices.add(val);
            } catch (Exception ignored) {
            }
        }
        return prices;
    }

    /** Проверяет, что цены идут не убывающим порядком. */
    public boolean pricesNonDecreasing() {
        List<Long> prices = readPrices();
        if (prices.size() < 2) return false;
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) return false;
        }
        return true;
    }

    /**
     * Для каждой карточки возвращает количество пересадок:
     * 0 — прямой, n — n пересадок (читается из текста карточки).
     */
    public List<Integer> readStopsPerCard() {
        List<Integer> result = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d+)\\s*пересад", Pattern.CASE_INSENSITIVE);
        for (WebElement card : driver.findElements(By.xpath(XPATH_FLIGHT_CARD))) {
            String text = card.getText().toLowerCase();
            if (text.contains("прямой") || text.contains("без пересад")) {
                result.add(0);
                continue;
            }
            Matcher m = p.matcher(text);
            if (m.find()) {
                try {
                    result.add(Integer.parseInt(m.group(1)));
                } catch (NumberFormatException ignored) {
                    result.add(1);
                }
            } else {
                result.add(-1);
            }
        }
        return result;
    }

    /** true, если все карточки в выдаче содержат заданную авиакомпанию. */
    public boolean allCardsContainAirline(String airlineFragment) {
        String needle = airlineFragment.toLowerCase();
        List<WebElement> cards = driver.findElements(By.xpath(XPATH_FLIGHT_CARD));
        if (cards.isEmpty()) return false;
        for (WebElement card : cards) {
            String text = card.getText().toLowerCase();
            if (!text.contains(needle)) return false;
        }
        return true;
    }

    /** true, если все карточки упоминают багаж (включённый, не «без багажа»). */
    public boolean allCardsMentionIncludedBaggage() {
        List<WebElement> cards = driver.findElements(By.xpath(XPATH_FLIGHT_CARD));
        if (cards.isEmpty()) return false;
        for (WebElement card : cards) {
            String text = card.getText().toLowerCase();
            boolean mentionsBaggage = text.contains("багаж");
            boolean explicitlyWithout = text.contains("без багаж");
            if (!mentionsBaggage || explicitlyWithout) return false;
        }
        return true;
    }

    /** Считывает первую найденную в карточке метку времени вида HH:MM. */
    public List<String> readFirstTimeOnEachCard() {
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("\\b(\\d{1,2}:\\d{2})\\b");
        for (WebElement card : driver.findElements(By.xpath(XPATH_FLIGHT_CARD))) {
            Matcher m = p.matcher(card.getText());
            if (m.find()) result.add(m.group(1));
        }
        return result;
    }

    /**
     * UC-21: на первой карточке нажимает «Выбрать», затем «Продолжить».
     * Возвращает {@link FlightCheckoutPage}, которая ждёт нужного URL.
     */
    public FlightCheckoutPage selectFirstAndContinue() {
        WebElement firstCard = waitVisible(By.xpath("(" + XPATH_FLIGHT_CARD + ")[1]"));
        scrollTo(firstCard);
        WebElement select = firstCard.findElement(By.xpath(XPATH_SELECT_BUTTON));
        try { select.click(); } catch (Exception e) { jsClick(select); }

        WebElement cont = waitClickable(By.xpath(XPATH_CONTINUE_BUTTON));
        scrollTo(cont);
        try { cont.click(); } catch (Exception e) { jsClick(cont); }

        return new FlightCheckoutPage(driver).awaitOpened();
    }

    /** Извлекает имена авиакомпаний с боковой панели фильтров (для UC-11). */
    public List<String> readAirlineFilterOptions() {
        List<String> options = new ArrayList<>();
        for (WebElement label : driver.findElements(
                By.xpath("//*[contains(translate(., 'АВИАКОМПНЯ', 'авиакомпня'),'авиакомпан')]"
                        + "/following::label[position()<=20]"))) {
            String text = label.getText().trim();
            if (!text.isEmpty()) options.add(text);
        }
        return options;
    }
}
