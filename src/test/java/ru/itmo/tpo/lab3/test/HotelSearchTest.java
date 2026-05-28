package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FavoritesPage;
import ru.itmo.tpo.lab3.page.HotelCheckoutPage;
import ru.itmo.tpo.lab3.page.HotelDetailsPage;
import ru.itmo.tpo.lab3.page.HotelResultsPage;
import ru.itmo.tpo.lab3.page.HotelSearchPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-2: Выбор отеля (https://www.tbank.ru/travel/hotels/).
 *
 * Покрывает include UC-14 (город) и extend-фильтры UC-15..UC-18.
 */
public class HotelSearchTest extends BaseTest {

    private static final String CITY = "Сочи";

    private HotelResultsPage performBaseSearch() {
        return new HotelSearchPage(driver).open()
                .typeDestination(CITY).chooseFirstSuggestion()
                .clickSearch();
    }

    // ============ Основной поток UC-2 ============

    @Test
    @DisplayName("UC-2 e2e: город → Искать → открывается страница выдачи отелей")
    void endToEndSearchOpensResults() {
        HotelResultsPage results = performBaseSearch();
        assertTrue(results.isOpened(),
                "После заполненной формы должна открываться выдача отелей");
    }

    // ============ Include UC-14 (город) ============

    @Test
    @DisplayName("UC-14: ввод города показывает выпадающий listbox подсказок и заполняет поле")
    void destinationAutocompleteFillsField() {
        HotelSearchPage page = new HotelSearchPage(driver).open()
                .typeDestination(CITY);
        assertTrue(page.isSuggestionsVisible(),
                "Должен показаться список подсказок для города '" + CITY + "'");
        page.chooseFirstSuggestion();
        String value = page.getDestinationValue();
        assertTrue(value != null && !value.isBlank(),
                "После выбора подсказки поле должно быть заполнено, получено: " + value);
    }

    // ============ Extend UC-15 (сортировка по цене) ============

    @Test
    @DisplayName("UC-15: сортировка 'Сначала дешевле' → цены идут не убывающим порядком")
    void sortByCheaperResultsAreNonDecreasing() {
        HotelResultsPage results = performBaseSearch().sortByCheapest();
        Assumptions.assumeTrue(results.cardsCount() >= 2,
                "Недостаточно карточек для проверки сортировки — пропускаем");

        List<Long> prices = results.readPrices();
        Assumptions.assumeTrue(prices.size() >= 2,
                "Не удалось распарсить цены из карточек — пропускаем");

        for (int i = 1; i < prices.size(); i++) {
            assertTrue(prices.get(i) >= prices.get(i - 1),
                    "Карточка #" + i + " дешевле предыдущей: "
                            + prices.get(i) + " < " + prices.get(i - 1));
        }
    }

    // ============ Extend UC-16 (спецпредложения) ============

    @Test
    @DisplayName("UC-16: фильтр 'Спецпредложения' → в карточках виден индикатор скидки")
    void specialOfferFilterShowsDiscountInCards() {
        HotelResultsPage results = performBaseSearch()
                .toggleFilterByLabel("спецпредлож");
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Нет отелей со спецпредложениями — пропускаем");

        assertTrue(results.allCardsHaveDiscountIndicator(),
                "Все карточки должны содержать индикатор скидки / спецпредложения");
    }

    // ============ Extend UC-17 (бесплатная отмена) ============

    @Test
    @DisplayName("UC-17: фильтр 'Бесплатная отмена' → во всех карточках есть метка")
    void freeCancellationFilterShowsFreeCancelInCards() {
        HotelResultsPage results = performBaseSearch()
                .toggleFilterByLabel("бесплатная отмена");
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Нет отелей с бесплатной отменой — пропускаем");

        assertTrue(results.allCardsMentionFreeCancellation(),
                "Все карточки должны иметь метку 'Бесплатная отмена'");
    }

    // ============ Extend UC-18 (избранное) ============

    @Test
    @DisplayName("UC-18: отель, добавленный в избранное, виден в разделе 'Избранное'")
    void hotelAddedFromCardAppearsInFavorites() {
        HotelResultsPage results = performBaseSearch();
        Assumptions.assumeTrue(results.cardsCount() > 0, "Нет отелей в выдаче — пропускаем");

        String hotelTitle = results.addFirstHotelToFavorites();
        Assumptions.assumeTrue(hotelTitle != null && !hotelTitle.isBlank(),
                "Не удалось прочитать название отеля — пропускаем");

        FavoritesPage favorites = new FavoritesPage(driver).open();
        assertTrue(favorites.containsHotelTitle(hotelTitle),
                "Отель '" + hotelTitle + "' должен присутствовать в избранном");
    }

    @Test
    @DisplayName("UC-2 шаг 7: клик по карточке открывает страницу отеля")
    void clickingHotelCardOpensDetails() {
        HotelResultsPage results = performBaseSearch();
        Assumptions.assumeTrue(results.cardsCount() > 0, "Нет отелей в выдаче — пропускаем");

        HotelDetailsPage details = results.openFirstCard();
        assertTrue(details.isOpened(),
                "После клика по карточке должна открыться страница отеля");
    }

    // ============ Extend UC-22 (переход к бронированию) ============

    @Test
    @DisplayName("UC-22: 'Выбрать номер' открывает /travel/hotels/new/checkout")
    void selectRoomOpensHotelCheckout() {
        HotelResultsPage results = performBaseSearch();
        Assumptions.assumeTrue(results.cardsCount() > 0, "Нет отелей в выдаче — пропускаем");

        HotelDetailsPage details = results.openFirstCard();
        Assumptions.assumeTrue(details.isOpened(),
                "Страница отеля не открылась — пропускаем");

        HotelCheckoutPage checkout = details.selectRoom();
        assertTrue(checkout.isOpened(),
                "URL должен содержать '" + HotelCheckoutPage.URL_FRAGMENT
                        + "', получено: " + checkout.currentUrl());
    }
}
