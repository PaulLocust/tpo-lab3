package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.itmo.tpo.lab3.DriverFactory;

import java.time.Duration;

/**
 * Базовый класс для всех тестов: поднимает выбранный браузер до каждого
 * теста и закрывает его после. Браузер задаётся системным свойством
 * {@code browser} (chrome/firefox).
 */
public abstract class BaseTest {

    protected static final long WAIT_SEC = 30L;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }
}
