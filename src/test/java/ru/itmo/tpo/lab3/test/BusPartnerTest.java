package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.BusPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-5: Выбор билетов на автобус.
 * Тестируется UC-19 — переход на партнёрский сайт.
 */
public class BusPartnerTest extends BaseTest {

    @Test
    @DisplayName("UC-5 / UC-19: клик по партнёрской ссылке открывает внешнюю страницу")
    void partnerLinkOpensExternalPage() {
        BusPage page = new BusPage(driver).open();
        Assumptions.assumeTrue(page.isAnyPartnerLinkVisible(),
                "На разделе автобусов не найдено партнёрской ссылки — пропускаем");

        String url = page.openFirstPartnerLink();
        assertTrue(url != null && !url.isBlank(), "После клика URL должен быть не пуст");
        assertTrue(!url.equals(BusPage.URL),
                "URL должен измениться после клика по партнёрской ссылке, получено: " + url);
    }
}
