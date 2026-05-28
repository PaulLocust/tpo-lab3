package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Страница отдельного отеля.
 * Используется в UC-18 для добавления отеля в избранное и проверки,
 * что он туда попал.
 */
public class HotelDetailsPage extends BasePage {

    public static final String FAVORITES_URL = "https://www.tbank.ru/travel/favorites/";

    private static final String XPATH_FAVORITE_BUTTON =
            "//*[contains(@data-qa-type,'favorite') or contains(@aria-label,'збранн') "
                    + "or contains(@aria-label,'Избранн')]";

    private static final String XPATH_HOTEL_TITLE =
            "//h1 | //*[contains(@data-qa-type,'hotelName') or contains(@data-qa-type,'title')][1]";

    private static final String XPATH_SELECT_ROOM_BUTTON =
            "//*[self::button or self::a]"
                    + "[contains(translate(., 'ВЫБРАТЬНОМЕ', 'выбратьноме'),'выбрать номер')]";

    public HotelDetailsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOpened() {
        return isPresent(By.xpath(XPATH_HOTEL_TITLE))
                || driver.getCurrentUrl().contains("hotel");
    }

    public String getTitle() {
        try {
            return waitVisible(By.xpath(XPATH_HOTEL_TITLE)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public HotelDetailsPage addToFavorites() {
        WebElement btn = waitClickable(By.xpath(XPATH_FAVORITE_BUTTON));
        scrollTo(btn);
        try { btn.click(); } catch (Exception e) { jsClick(btn); }
        return this;
    }

    public FavoritesPage openFavorites() {
        driver.get(FAVORITES_URL);
        return new FavoritesPage(driver);
    }

    /**
     * UC-22: клик «Выбрать номер» → переход на страницу бронирования.
     */
    public HotelCheckoutPage selectRoom() {
        WebElement btn = waitClickable(By.xpath(XPATH_SELECT_ROOM_BUTTON));
        scrollTo(btn);
        try { btn.click(); } catch (Exception e) { jsClick(btn); }
        return new HotelCheckoutPage(driver).awaitOpened();
    }
}
