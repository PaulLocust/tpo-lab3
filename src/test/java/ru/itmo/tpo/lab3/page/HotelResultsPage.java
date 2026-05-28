package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Страница выдачи отелей.
 *
 * Используется для UC-15..UC-18: применение фильтров «Спецпредложения»,
 * «Бесплатная отмена», сортировки по цене и добавления в избранное.
 */
public class HotelResultsPage extends BasePage {

    private static final String XPATH_HOTEL_CARD =
            "//div[contains(@data-qa-type,'hotelCard') or contains(@data-qa-type,'HotelCard') "
                    + "or @data-qa-type='hotel-card']";

    private static final String XPATH_CARD_PRICE =
            ".//*[contains(@data-qa-type,'price') or contains(@data-qa-type,'Price')]";

    private static final String XPATH_CARD_DISCOUNT =
            ".//*[contains(translate(., '%СКИДКА', '%скидка'),'скидк') "
                    + "or contains(translate(., 'SPECIAL', 'special'),'special') "
                    + "or contains(text(),'%')]";

    private static final String XPATH_CARD_FREE_CANCEL =
            ".//*[contains(translate(., 'БЕСПЛАТНОЯМЕНДЯ', 'бесплатноямендя'),'бесплатн') "
                    + "and contains(translate(., 'ОТМЕНЯДЯ', 'отменядя'),'отмен')]";

    private static final String XPATH_CARD_FAVORITE_BUTTON =
            ".//*[contains(@data-qa-type,'favorite') or contains(@aria-label,'збранн') "
                    + "or contains(@aria-label,'Избранн')]";

    private static final String XPATH_CARD_TITLE =
            ".//*[contains(@data-qa-type,'name') or contains(@data-qa-type,'title') "
                    + "or self::h2 or self::h3][1]";

    private static final String XPATH_SORT_CHEAPER =
            "//*[self::button or self::a or self::div]"
                    + "[contains(translate(., 'ДЕШВЛ', 'дешвл'),'дешев')]";

    private static final String XPATH_FILTER_LABEL_FMT =
            "//label[.//text()[contains(translate(., 'АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ',"
                    + " 'абвгдеёжзийклмнопрстуфхцчшщъыьэюя'),'%s')]]";

    public HotelResultsPage(WebDriver driver) {
        super(driver);
    }

    public HotelResultsPage awaitResults() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("hotels"),
                    ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_HOTEL_CARD))));
        } catch (Exception ignored) {
        }
        return this;
    }

    public boolean isOpened() {
        return driver.getCurrentUrl().contains("hotels") || isAnyCardPresent();
    }

    public boolean isAnyCardPresent() {
        return isPresent(By.xpath(XPATH_HOTEL_CARD));
    }

    public int cardsCount() {
        return driver.findElements(By.xpath(XPATH_HOTEL_CARD)).size();
    }

    public HotelResultsPage toggleFilterByLabel(String labelFragmentLowercase) {
        String xpath = String.format(XPATH_FILTER_LABEL_FMT, labelFragmentLowercase);
        WebElement label = waitClickable(By.xpath(xpath));
        scrollTo(label);
        try { label.click(); } catch (Exception e) { jsClick(label); }
        return this;
    }

    public HotelResultsPage sortByCheapest() {
        WebElement sort = waitClickable(By.xpath(XPATH_SORT_CHEAPER));
        scrollTo(sort);
        try { sort.click(); } catch (Exception e) { jsClick(sort); }
        return this;
    }

    public List<Long> readPrices() {
        List<Long> prices = new ArrayList<>();
        for (WebElement card : driver.findElements(By.xpath(XPATH_HOTEL_CARD))) {
            try {
                WebElement p = card.findElement(By.xpath(XPATH_CARD_PRICE));
                long val = parsePriceAsLong(p.getText());
                if (val > 0) prices.add(val);
            } catch (Exception ignored) {
            }
        }
        return prices;
    }

    public boolean pricesNonDecreasing() {
        List<Long> prices = readPrices();
        if (prices.size() < 2) return false;
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) return false;
        }
        return true;
    }

    /** Доля карточек, в которых видно процент скидки или метку Special offer. */
    public boolean allCardsHaveDiscountIndicator() {
        List<WebElement> cards = driver.findElements(By.xpath(XPATH_HOTEL_CARD));
        if (cards.isEmpty()) return false;
        for (WebElement card : cards) {
            String text = card.getText().toLowerCase();
            boolean hasPercent = text.contains("%");
            boolean hasSpecial = text.contains("special") || text.contains("спецпредлож");
            boolean hasDiscount = text.contains("скидк");
            if (!hasPercent && !hasSpecial && !hasDiscount) return false;
        }
        return true;
    }

    public boolean allCardsMentionFreeCancellation() {
        List<WebElement> cards = driver.findElements(By.xpath(XPATH_HOTEL_CARD));
        if (cards.isEmpty()) return false;
        for (WebElement card : cards) {
            String text = card.getText().toLowerCase();
            boolean mentions = text.contains("бесплатн") && text.contains("отмен");
            if (!mentions) return false;
        }
        return true;
    }

    /**
     * Запоминает название первого отеля и кликает иконку «в избранное» на нём.
     * Возвращает название, чтобы тест мог найти его в избранном.
     */
    public String addFirstHotelToFavorites() {
        WebElement firstCard = waitVisible(By.xpath("(" + XPATH_HOTEL_CARD + ")[1]"));
        scrollTo(firstCard);
        String title;
        try {
            title = firstCard.findElement(By.xpath(XPATH_CARD_TITLE)).getText();
        } catch (Exception e) {
            title = firstCard.getText().split("\n")[0];
        }
        WebElement fav = firstCard.findElement(By.xpath(XPATH_CARD_FAVORITE_BUTTON));
        scrollTo(fav);
        try { fav.click(); } catch (Exception e) { jsClick(fav); }
        return title;
    }

    /** Кликает по карточке отеля, открывая её страницу (UC-2 шаг 7). */
    public HotelDetailsPage openFirstCard() {
        WebElement firstCard = waitClickable(By.xpath("(" + XPATH_HOTEL_CARD + ")[1]"));
        String originalUrl = driver.getCurrentUrl();
        int handlesBefore = driver.getWindowHandles().size();
        scrollTo(firstCard);
        try { firstCard.click(); } catch (Exception e) { jsClick(firstCard); }

        try {
            wait.until(d -> d.getWindowHandles().size() > handlesBefore
                    || !d.getCurrentUrl().equals(originalUrl));
        } catch (Exception ignored) {
        }
        if (driver.getWindowHandles().size() > handlesBefore) {
            String current = driver.getWindowHandle();
            for (String h : driver.getWindowHandles()) {
                if (!h.equals(current)) {
                    driver.switchTo().window(h);
                    break;
                }
            }
        }
        return new HotelDetailsPage(driver);
    }
}
