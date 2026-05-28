package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.FlightCheckoutPage;
import ru.itmo.tpo.lab3.page.FlightResultsPage;
import ru.itmo.tpo.lab3.page.FlightSearchPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-1: Выбор авиабилетов (https://www.tbank.ru/travel/flights/).
 *
 * Покрывает include-шаги (UC-7 маршрут, UC-8 дата) и extend-фильтры
 * выдачи (UC-9..UC-13).
 *
 * Все тесты — только UI-проверки: фильтры, отображение в карточках,
 * корректность переходов. Никакого ввода персональных данных.
 */
public class FlightSearchTest extends BaseTest {

    private static final String ORIGIN = "Москва";
    private static final String DESTINATION = "Сочи";

    /** Заполняет форму известным маршрутом и нажимает «Найти». */
    private FlightResultsPage performBaseSearch() {
        return new FlightSearchPage(driver).open()
                .typeOrigin(ORIGIN).chooseFirstSuggestion()
                .typeDestination(DESTINATION).chooseFirstSuggestion()
                .clickSearch();
    }

    // ============ Основной поток UC-1 ============

    @Test
    @DisplayName("UC-1 e2e: маршрут → найти → открывается страница выдачи рейсов")
    void endToEndSearchOpensResults() {
        FlightResultsPage results = performBaseSearch();
        assertTrue(results.isOpened(),
                "После корректно заполненной формы должна открываться страница выдачи");
    }

    // ============ Include UC-7 (маршрут) ============

    @Test
    @DisplayName("UC-7: ввод в 'Откуда' открывает подсказки, выбор заполняет поле")
    void originAutocompleteFillsField() {
        FlightSearchPage page = new FlightSearchPage(driver).open()
                .typeOrigin(ORIGIN);
        assertTrue(page.isSuggestionsVisible(), "Должны появиться подсказки городов");
        page.chooseFirstSuggestion();
        String value = page.getOriginValue();
        assertTrue(value != null && !value.isBlank(),
                "После выбора подсказки поле 'Откуда' должно быть заполнено, получено: " + value);
    }

    @Test
    @DisplayName("UC-7: ввод в 'Куда' открывает подсказки, выбор заполняет поле")
    void destinationAutocompleteFillsField() {
        FlightSearchPage page = new FlightSearchPage(driver).open()
                .typeDestination(DESTINATION);
        assertTrue(page.isSuggestionsVisible(), "Должны появиться подсказки городов");
        page.chooseFirstSuggestion();
        String value = page.getDestinationValue();
        assertTrue(value != null && !value.isBlank(),
                "После выбора подсказки поле 'Куда' должно быть заполнено, получено: " + value);
    }

    // ============ Include UC-8 (дата) ============

    @Test
    @DisplayName("UC-8: клик по полю даты открывает виджет календаря")
    void datePickerOpensCalendarWidget() {
        FlightSearchPage page = new FlightSearchPage(driver).open()
                .openDatePicker();
        assertTrue(page.isCalendarOpened(),
                "Календарь должен открыться при клике по полю 'Когда'");
    }

    // ============ Extend UC-9 (без пересадок) ============

    @Test
    @DisplayName("UC-9: фильтр 'Без пересадок' → все карточки прямые (0 пересадок)")
    void directFlightsFilterLeavesOnlyDirectFlights() {
        FlightResultsPage results = performBaseSearch()
                .toggleFilterByLabel("без пересадок");
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "В выдаче нет рейсов после фильтра 'Без пересадок' — пропускаем");

        List<Integer> stops = results.readStopsPerCard();
        for (Integer s : stops) {
            assertTrue(s != null && s == 0,
                    "После фильтра 'Без пересадок' карточка должна быть прямой, получено: " + s);
        }
    }

    @Test
    @DisplayName("UC-9: фильтр '1 пересадка' → в каждой карточке хотя бы 1 промежуточный пункт")
    void oneStopFilterShowsFlightsWithStops() {
        FlightResultsPage results = performBaseSearch()
                .toggleFilterByLabel("1 пересадка");
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "В выдаче нет рейсов с 1 пересадкой — пропускаем");

        List<Integer> stops = results.readStopsPerCard();
        for (Integer s : stops) {
            assertTrue(s != null && s >= 1,
                    "После фильтра '1 пересадка' должна быть хотя бы одна пересадка, получено: " + s);
        }
    }

    // ============ Extend UC-10 (сортировка по цене) ============

    @Test
    @DisplayName("UC-10: сортировка 'Сначала дешевле' → цены идут не убывающим порядком")
    void sortByCheaperResultsAreNonDecreasing() {
        FlightResultsPage results = performBaseSearch().sortByCheapest();
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

    // ============ Extend UC-11 (фильтр авиакомпании) ============

    @Test
    @DisplayName("UC-11: фильтр авиакомпании → во всех карточках упоминается выбранная компания")
    void airlineFilterKeepsOnlySelectedAirline() {
        FlightResultsPage results = performBaseSearch();
        List<String> airlines = results.readAirlineFilterOptions();
        Assumptions.assumeFalse(airlines.isEmpty(),
                "Не найдено фильтра по авиакомпаниям — пропускаем");

        String airline = airlines.get(0);
        results.toggleFilterByLabel(airline.toLowerCase());
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Нет рейсов выбранной авиакомпании — пропускаем");

        assertTrue(results.allCardsContainAirline(airline),
                "Все карточки должны упоминать авиакомпанию '" + airline + "'");
    }

    // ============ Extend UC-12 (фильтр «С багажом») ============

    @Test
    @DisplayName("UC-12: фильтр 'С багажом' → во всех карточках виден индикатор багажа")
    void withBaggageFilterShowsBaggageInCards() {
        FlightResultsPage results = performBaseSearch()
                .toggleFilterByLabel("багаж");
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Нет рейсов с включённым багажом — пропускаем");

        assertTrue(results.allCardsMentionIncludedBaggage(),
                "После фильтра 'С багажом' каждая карточка должна упоминать багаж");
    }

    // ============ Extend UC-13 (время вылета) ============

    @Test
    @DisplayName("UC-13: в карточках после поиска у каждой есть метка времени вылета")
    void departureTimeIsPresentOnCards() {
        FlightResultsPage results = performBaseSearch();
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Выдача пуста — пропускаем");

        List<String> times = results.readFirstTimeOnEachCard();
        assertFalse(times.isEmpty(),
                "В выдаче должно читаться время вылета хотя бы у одной карточки");
        for (String t : times) {
            assertTrue(t.matches("\\d{1,2}:\\d{2}"),
                    "Время должно быть в формате HH:MM, получено: " + t);
        }
    }

    // ============ Extend UC-21 (переход к покупке) ============

    @Test
    @DisplayName("UC-21: 'Выбрать' → 'Продолжить' открывает /travel/flights/checkout/")
    void selectAndContinueOpensFlightCheckout() {
        FlightResultsPage results = performBaseSearch();
        Assumptions.assumeTrue(results.cardsCount() > 0,
                "Выдача пуста — нечего выбирать, пропускаем");

        FlightCheckoutPage checkout = results.selectFirstAndContinue();
        assertTrue(checkout.isOpened(),
                "URL должен содержать '" + FlightCheckoutPage.URL_FRAGMENT
                        + "', получено: " + checkout.currentUrl());
    }
}
