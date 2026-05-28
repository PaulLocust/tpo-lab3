package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.ExcursionPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-6: Выбор экскурсий.
 * Тестируется UC-19 — переход на партнёрский сайт.
 */
public class ExcursionPartnerTest extends BaseTest {

    @Test
    @DisplayName("UC-6 / UC-19: клик по партнёрской ссылке открывает внешнюю страницу")
    void partnerLinkOpensExternalPage() {
        ExcursionPage page = new ExcursionPage(driver).open();
        Assumptions.assumeTrue(page.isAnyPartnerLinkVisible(),
                "На разделе экскурсий не найдено партнёрской ссылки — пропускаем");

        String url = page.openFirstPartnerLink();
        assertTrue(url != null && !url.isBlank(), "После клика URL должен быть не пуст");
        assertTrue(!url.equals(ExcursionPage.URL),
                "URL должен измениться после клика по партнёрской ссылке, получено: " + url);
    }
}
