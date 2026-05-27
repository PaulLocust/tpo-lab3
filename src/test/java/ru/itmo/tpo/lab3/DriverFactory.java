package ru.itmo.tpo.lab3;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        return create(System.getProperty("browser", "chrome").toLowerCase());
    }

    public static WebDriver create(String browser) {
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addPreference("intl.accept_languages", "ru-RU, ru");
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                if (isHeadless()) {
                    firefoxOptions.addArguments("-headless");
                }
                return new FirefoxDriver(firefoxOptions);

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--lang=ru-RU");
                chromeOptions.addArguments("--remote-allow-origins=*");
                if (isHeadless()) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--window-size=1440,900");
                }
                return new ChromeDriver(chromeOptions);
        }
    }

    private static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }
}
