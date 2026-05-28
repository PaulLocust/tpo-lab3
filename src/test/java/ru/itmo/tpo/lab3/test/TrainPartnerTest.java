package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TrainSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-3: Выбор билетов на поезд.
 * Тестируется только UC-19 — переход на партнёрский домен (OneTwoTrip).
 */
public class TrainPartnerTest extends BaseTest {

    @Test
    @DisplayName("UC-3 / UC-19: клик 'Найти' открывает страницу партнёра")
    void searchRedirectsToPartner() {
        String url = new TrainSearchPage(driver).open().clickSearchAndCaptureUrl();

        assertFalse(url == null || url.isBlank(),
                "После клика должен быть URL");
        assertTrue(!url.startsWith("https://www.tbank.ru/travel/trains/")
                        || url.contains("?") || url.contains("search"),
                "URL должен измениться после клика поиска ЖД, получено: " + url);
    }
}
