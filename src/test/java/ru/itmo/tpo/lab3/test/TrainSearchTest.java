package ru.itmo.tpo.lab3.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.itmo.tpo.lab3.page.TrainSearchPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UC-05: Поиск ЖД-билетов.
 * Актор: Гость.
 *
 * На форме поезда «Откуда»/«Куда»/«Когда» пусты, «Пассажиры» = '1 взрослый'.
 * Кнопка поиска 'Найти на OneTwoTrip' — внешний редирект.
 */
public class TrainSearchTest extends BaseTest {

    private TrainSearchPage trainPage;

    @BeforeEach
    void openTrainForm() {
        trainPage = new TrainSearchPage(driver).open();
    }

    // ============== Основной поток ==============

    @Test
    @DisplayName("Форма поиска ЖД содержит поля 'Откуда', 'Куда', 'Когда', пассажиры и кнопку")
    void formHasMandatoryElements() {
        assertTrue(trainPage.isOriginInputVisible(),      "Нет поля 'Откуда'");
        assertTrue(trainPage.isDestinationInputVisible(), "Нет поля 'Куда'");
        assertTrue(trainPage.isDateInputVisible(),        "Нет поля 'Когда'");
        assertTrue(trainPage.isPassengersInputVisible(),  "Нет поля 'Пассажиры'");
        assertTrue(trainPage.isSearchButtonVisible(),     "Нет кнопки поиска");
    }

    @Test
    @DisplayName("Поле 'Пассажиры' имеет дефолтное значение '1 взрослый'")
    void passengersHasDefaultValue() {
        String val = trainPage.getPassengersValue();
        assertTrue(val != null && val.toLowerCase().contains("взросл"),
                "Поле пассажиров должно быть заполнено по умолчанию, получено: " + val);
    }

    @Test
    @DisplayName("Ввод в поле 'Откуда' показывает выпадающий listbox подсказок")
    void originAutocompleteShowsSuggestions() {
        trainPage.typeOrigin("Моск");
        assertTrue(trainPage.isSuggestionsListboxVisible()
                        || trainPage.isSuggestionsVisible(),
                "При вводе должен показываться список подсказок");
    }

    // ============== Краевые случаи ==============

    @Nested
    @DisplayName("Краевые случаи поиска ЖД-билетов")
    class EdgeCases {

        @Test
        @DisplayName("Краевой: поиск с пустой формой не запускает выдачу")
        void emptyForm_doesNotProceed() {
            String before = driver.getCurrentUrl();
            trainPage.clickSearch();
            assertTrue(trainPage.stayedOnSearchForm(before),
                    "С пустой формой поиск не должен переходить к выдаче");
        }

        @Test
        @DisplayName("Краевой: заполнено только 'Откуда' — поиск не идёт")
        void onlyOriginFilled_doesNotProceed() {
            String before = driver.getCurrentUrl();
            trainPage.typeOrigin("Москва").chooseFirstSuggestion();
            trainPage.clickSearch();
            assertTrue(trainPage.stayedOnSearchForm(before),
                    "При незаполненном 'Куда' поиск ЖД не должен запускаться");
        }

        @Test
        @DisplayName("Краевой: ввод бессмыслицы — ни одна подсказка не содержит введённой строки")
        void nonExistentCity_noSuggestionContainsInput() {
            String junk = "Кфтыкчоувапролд";
            trainPage.typeOrigin(junk);
            assertFalse(trainPage.anySuggestionContains(junk),
                    "Подсказок, содержащих '" + junk + "', быть не должно");
        }

        @Test
        @DisplayName("Краевой: повторный набор города после очистки снова открывает подсказки")
        void retypingAfterClear_showsSuggestionsAgain() {
            trainPage.typeOrigin("Москва").chooseFirstSuggestion();
            trainPage.clearOrigin();
            trainPage.typeOrigin("Каз");
            assertTrue(trainPage.isSuggestionsListboxVisible()
                            || trainPage.isSuggestionsVisible(),
                    "После очистки и повторного ввода автокомплит должен снова работать");
        }
    }
}
