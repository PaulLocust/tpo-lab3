package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TourPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-4: Выбор тура.
 * Тестируется UC-20 — открытие карточки тура у партнёра-турагента.
 */
public class TourPartnerTest extends BaseTest {

    @Test
    @DisplayName("UC-4 / UC-20: клик 'Найти тур' открывает партнёрскую страницу")
    void firstPartnerCardOpensExternalPage() {
        TourPage page = new TourPage(driver).open();
        assertTrue(page.partnerButtonsCount() >= 1,
                "На странице туров должна быть хотя бы одна партнёрская карточка");

        String url = page.openFirstPartnerCard();
        assertTrue(url != null && !url.isBlank(), "После клика URL должен быть не пуст");
        assertTrue(!url.equals(TourPage.URL),
                "URL должен измениться после клика по партнёрской карточке, получено: " + url);
    }
}
