package ru.itmo.tpo.lab3.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Главная страница раздела "Путешествия" T-Банка.
 * Отвечает за загрузку URL и переходы в подразделы Авиа/Отели/Поезда/Туры.
 */
public class MainPage extends BasePage {

    public static final String URL = "https://www.tbank.ru/travel/";

    private static final String XPATH_NAV_AVIA   = "//a[contains(@href,'/travel/flights') and normalize-space()='Авиа']";
    private static final String XPATH_NAV_HOTELS = "//a[contains(@href,'/travel/hotels') and normalize-space()='Отели']";
    private static final String XPATH_NAV_TRAINS = "//a[contains(@href,'/travel/trains') and normalize-space()='Поезда']";
    private static final String XPATH_NAV_TOURS  = "//a[contains(@href,'/travel/tours') and normalize-space()='Туры']";

    private static final String XPATH_NAV_AVIA_FB   = "//a[contains(@href,'/travel/flights')]";
    private static final String XPATH_NAV_HOTELS_FB = "//a[contains(@href,'/travel/hotels')]";
    private static final String XPATH_NAV_TRAINS_FB = "//a[contains(@href,'/travel/trains')]";
    private static final String XPATH_NAV_TOURS_FB  = "//a[contains(@href,'/travel/tours')]";

    private static final String XPATH_TBANK_LOGO = "//a[contains(@href,'tbank.ru')]//*[name()='svg'] | //a[@href='/' or @href='https://www.tbank.ru/']";

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage open() {
        driver.get(URL);
        return this;
    }

    public boolean isAviaTabVisible() {
        return isDisplayedFast(By.xpath(XPATH_NAV_AVIA)) || isPresent(By.xpath(XPATH_NAV_AVIA_FB));
    }

    public boolean isHotelsTabVisible() {
        return isDisplayedFast(By.xpath(XPATH_NAV_HOTELS)) || isPresent(By.xpath(XPATH_NAV_HOTELS_FB));
    }

    public boolean isTrainsTabVisible() {
        return isDisplayedFast(By.xpath(XPATH_NAV_TRAINS)) || isPresent(By.xpath(XPATH_NAV_TRAINS_FB));
    }

    public boolean isToursTabVisible() {
        return isDisplayedFast(By.xpath(XPATH_NAV_TOURS)) || isPresent(By.xpath(XPATH_NAV_TOURS_FB));
    }

    public boolean isLogoVisible() {
        return isPresent(By.xpath(XPATH_TBANK_LOGO));
    }

    public FlightSearchPage clickAvia() {
        clickNav(XPATH_NAV_AVIA, XPATH_NAV_AVIA_FB);
        waitForUrl("/travel/flights");
        return new FlightSearchPage(driver);
    }

    public HotelSearchPage clickHotels() {
        clickNav(XPATH_NAV_HOTELS, XPATH_NAV_HOTELS_FB);
        waitForUrl("/travel/hotels");
        return new HotelSearchPage(driver);
    }

    public TrainSearchPage clickTrains() {
        clickNav(XPATH_NAV_TRAINS, XPATH_NAV_TRAINS_FB);
        waitForUrl("/travel/trains");
        return new TrainSearchPage(driver);
    }

    public TourPage clickTours() {
        clickNav(XPATH_NAV_TOURS, XPATH_NAV_TOURS_FB);
        waitForUrl("/travel/tours");
        return new TourPage(driver);
    }

    private void clickNav(String primaryXpath, String fallbackXpath) {
        WebElement link = isPresent(By.xpath(primaryXpath))
                ? waitClickable(By.xpath(primaryXpath))
                : waitClickable(By.xpath(fallbackXpath));
        scrollTo(link);
        link.click();
    }
}
