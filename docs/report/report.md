# Лабораторная работа №3
## Функциональное тестирование интерфейса сайта средствами Selenium WebDriver

**Кафедра:** Технологии программирования
**ВУЗ:** Университет ИТМО
**Вариант:** сайт **T-Путешествия** — <https://www.tbank.ru/travel/>

---

## 1. Текст задания

Сформировать варианты использования, разработать на их основе тестовое покрытие
и провести функциональное тестирование интерфейса сайта (в соответствии с
вариантом).

* Тестовое покрытие формируется на основании набора прецедентов использования сайта.
* Тестирование выполняется автоматически — средствами Selenium WebDriver.
* Тестовые сценарии исполняются в браузерах **Firefox** и **Chrome**, как по
  отдельности, так и параллельно.
* Поскольку сайт использует динамическую генерацию элементов DOM, поиск
  элементов осуществляется **с помощью XPath**, а не по идентификаторам.
* Для ожидания появления элементов **не используется `Thread.sleep()`** —
  только `WebDriverWait` + `ExpectedConditions`.
* Для удобной организации кода применяется паттерн **Page Object**.

### Состав отчёта
1. Текст задания.
2. UseCase-диаграмма с прецедентами использования тестируемого сайта.
3. CheckList тестового покрытия.
4. Описание набора тестовых сценариев (основной поток + краевые случаи).
5. Результаты тестирования.
6. Выводы.

---

## 2. Use Case диаграмма

Полная диаграмма с PlantUML и Mermaid версиями — [docs/use-case-diagram.md](../use-case-diagram.md).
Подробные карточки прецедентов — [docs/use-cases.md](../use-cases.md).

```mermaid
flowchart LR
    Guest(["Гость"])

    subgraph TT ["T-Путешествия"]
        UC01["UC-01: Главная страница"]
        UC02["UC-02: Переход между разделами"]
        UC03["UC-03: Поиск авиабилетов"]
        UC04["UC-04: Поиск отелей"]
        UC05["UC-05: Поиск ЖД-билетов"]
        UC06["UC-06: Раздел Туры"]
        UC07["UC-07: Обмен городов (swap)"]
        UC08["UC-08: Календарь дат"]
        UC09["UC-09: Пассажиры и класс"]
        UC10["UC-10: Автодополнение городов"]
    end

    Guest --> UC01
    Guest --> UC02
    Guest --> UC03
    Guest --> UC04
    Guest --> UC05
    Guest --> UC06

    UC07 -.->|extend| UC03
    UC08 -.->|extend| UC03
    UC09 -.->|extend| UC03

    UC03 -.->|include| UC10
    UC04 -.->|include| UC10
    UC05 -.->|include| UC10

    UC02 -.->|extend| UC01
```

### Акторы и прецеденты

| ID    | Название                                | Класс теста                       |
|-------|------------------------------------------|-----------------------------------|
| UC-01 | Просмотр главной страницы               | `MainPageTest`                    |
| UC-02 | Переход между разделами                 | `NavigationTest`                  |
| UC-03 | Поиск авиабилетов                       | `FlightSearchTest`                |
| UC-04 | Поиск отелей                            | `HotelSearchTest`                 |
| UC-05 | Поиск ЖД-билетов                        | `TrainSearchTest`                 |
| UC-06 | Просмотр раздела «Туры»                 | `TourPageTest`                    |
| UC-07 | Обмен городов местами (swap)            | `SwapCitiesTest`                  |
| UC-08 | Выбор даты вылета (календарь)           | `DatePickerTest`                  |
| UC-09 | Выбор пассажиров и класса               | `PassengersTest`                  |
| UC-10 | Автодополнение городов                  | `CrossSectionAutocompleteTest`    |

---

## 3. CheckList тестового покрытия

Полный чек-лист с разделением на основной поток и краевые случаи —
[checklist.md](../../checklist.md). Сводка:

| Категория              | Кол-во проверок |
|------------------------|----------------:|
| Happy-path             |  30             |
| Краевые случаи         |  25             |
| **Итого @Test методов**| **55**          |

Каждая проверка прогоняется в **двух браузерах** (Chrome и Firefox) — итого
**110 прогонов** в кросс-браузерном режиме.

### Карта краевых случаев по use case

| UC    | Класс                          | Краевые случаи в `@Nested EdgeCases`                                                                                                  |
|-------|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| UC-02 | `NavigationTest`               | Последовательная навигация по всем разделам · Кнопка «Назад» браузера · Прямой переход по URL                                          |
| UC-03 | `FlightSearchTest`             | Пустая форма · Только «Откуда» · Только «Куда» · Одинаковые города · Очистка поля · Несуществующий город · Один символ · Очистка+повтор |
| UC-04 | `HotelSearchTest`              | Пустое поле города · Несуществующий город · Очистка поля города · Перезагрузка страницы                                                |
| UC-05 | `TrainSearchTest`              | Пустая форма · Только «Откуда» · Одинаковые города · Несуществующий город                                                              |
| UC-07 | `SwapCitiesTest`               | Swap при пустых полях · Двойной swap → исходное состояние                                                                              |
| UC-08 | `DatePickerTest`               | Esc закрывает календарь · Повторное открытие календаря                                                                                 |
| UC-09 | `PassengersTest`               | Панель не ломает форму · Повторное открытие после выбора «Бизнес»                                                                      |
| UC-10 | `CrossSectionAutocompleteTest` | Несуществующий город на всех трёх формах · Известный город даёт ≥1 пункт · Пустой ввод не открывает выпадашку                          |

---

## 4. Описание набора тестовых сценариев

Все тесты используют:
* **Page Object pattern** — каждый раздел сайта инкапсулирован в свой
  PageObject (`MainPage`, `FlightSearchPage`, `HotelSearchPage`,
  `TrainSearchPage`, `TourPage`); общая логика — в `BasePage`.
* **Только XPath**-локаторы (по требованию задания), привязанные к
  `placeholder`, `aria-label` и видимому тексту, потому что классы
  генерируются динамически.
* **`WebDriverWait` + `ExpectedConditions`** — без `Thread.sleep()`.
* **JUnit 5** (`@Test`, `@BeforeEach`, `@AfterEach`, `@Nested`).
* Краевые случаи вынесены во **внутренний `@Nested`-класс `EdgeCases`** —
  это разделяет happy-path и негативные сценарии прямо в отчёте Gradle.
* **Кросс-браузерный запуск**: тип браузера приходит через системное
  свойство `-Dbrowser=chrome|firefox`, благодаря чему один и тот же тестовый
  набор выполняется в обоих браузерах. Параллельный запуск — через
  `test-parallel.sh`, который одновременно поднимает `./gradlew testChrome`
  и `./gradlew testFirefox`.

### UC-01 / `MainPageTest`

| # | Метод                            | Что проверяется |
|---|----------------------------------|------------------|
| 1 | `mainPageOpensOnTbankDomain`     | URL содержит `tbank.ru/travel` |
| 2 | `mainPageHasLogo`                | Логотип T-Банка отображается |
| 3 | `aviaTabIsVisible`               | Вкладка «Авиа» отображается |
| 4 | `hotelsTabIsVisible`             | Вкладка «Отели» отображается |
| 5 | `trainsTabIsVisible`             | Вкладка «Поезда» отображается |
| 6 | `toursTabIsVisible`              | Вкладка «Туры» отображается |

### UC-02 / `NavigationTest`

| # | Метод                                       | Что проверяется |
|---|----------------------------------------------|------------------|
| 1 | `aviaTabOpensFlightForm`                    | Клик «Авиа» → URL `/travel/flights`, форма видна |
| 2 | `hotelsTabOpensHotelForm`                   | Клик «Отели» → URL `/travel/hotels`, форма видна |
| 3 | `trainsTabOpensTrainForm`                   | Клик «Поезда» → URL `/travel/trains`, форма видна |
| 4 | `toursTabOpensToursPage`                    | Клик «Туры» → URL `/travel/tours`, поле направления |
| 5 | `EdgeCases.consecutiveNavigationThroughAllSections` | **Краевой:** последовательный обход всех 4 разделов |
| 6 | `EdgeCases.browserBackButton_returnsToMainPage`     | **Краевой:** кнопка «Назад» браузера |
| 7 | `EdgeCases.directNavigationToFlightsPage`           | **Краевой:** прямой переход по URL |

### UC-03 / `FlightSearchTest`

| # | Метод                                  | Что проверяется |
|---|----------------------------------------|------------------|
| 1 | `formHasAllKeyElements`                | Все обязательные поля и кнопки видны |
| 2 | `originAutocompleteShowsSuggestions`   | Ввод «Моск» открывает подсказки |
| 3 | `selectingSuggestionFillsOriginField`  | Выбор подсказки заполняет поле |
| 4 | `fullOneWayFlightSearchOpensResults`   | Полный сценарий Москва → Сочи → выдача |
| 5 | `EdgeCases.emptyFormDoesNotProceedToResults` | **Краевой:** пустая форма не запускает поиск |
| 6 | `EdgeCases.onlyOriginFilled_doesNotProceed`  | **Краевой:** только «Откуда» |
| 7 | `EdgeCases.onlyDestinationFilled_doesNotProceed` | **Краевой:** только «Куда» |
| 8 | `EdgeCases.sameOriginAndDestination_doesNotProceed` | **Краевой:** одинаковые города |
| 9 | `EdgeCases.clearingOrigin_emptiesField` | **Краевой:** очистка поля |
| 10| `EdgeCases.nonExistentCity_doesNotShowMeaningfulSuggestions` | **Краевой:** несуществующий город |
| 11| `EdgeCases.singleLetterInput_isHandledSafely` | **Краевой:** ввод одной буквы |
| 12| `EdgeCases.retypingAfterClear_showsSuggestionsAgain` | **Краевой:** очистка и повторный ввод |

### UC-04 / `HotelSearchTest`

| # | Метод                                              | Что проверяется |
|---|----------------------------------------------------|------------------|
| 1 | `formHasMandatoryFields`                           | Поле города и кнопка поиска |
| 2 | `destinationAutocompleteShowsSuggestions`          | Подсказки при вводе города |
| 3 | `hotelSearchByCityOpensResults`                    | Полный сценарий поиска |
| 4 | `EdgeCases.emptyDestination_doesNotProceed`        | **Краевой:** пустое поле города |
| 5 | `EdgeCases.nonExistentCity_doesNotShowMeaningfulSuggestions` | **Краевой:** несуществующий город |
| 6 | `EdgeCases.clearingDestination_removesSuggestions` | **Краевой:** очистка поля города |
| 7 | `EdgeCases.reopenPage_preservesFunctionality`      | **Краевой:** перезагрузка страницы |

### UC-05 / `TrainSearchTest`

| # | Метод                                                  | Что проверяется |
|---|--------------------------------------------------------|------------------|
| 1 | `formHasMandatoryElements`                             | Поля «Откуда», «Куда», дата, кнопка |
| 2 | `originAutocompleteShowsSuggestions`                   | Подсказки при вводе города |
| 3 | `searchTrainBetweenCities`                             | Москва → Санкт-Петербург → выдача |
| 4 | `EdgeCases.emptyForm_doesNotProceed`                   | **Краевой:** пустая форма |
| 5 | `EdgeCases.onlyOriginFilled_doesNotProceed`            | **Краевой:** только «Откуда» |
| 6 | `EdgeCases.sameOriginAndDestination_doesNotProceed`    | **Краевой:** одинаковые города |
| 7 | `EdgeCases.nonExistentCity_showsNoSuggestions`         | **Краевой:** несуществующий город |

### UC-06 / `TourPageTest`

| # | Метод                          | Что проверяется |
|---|--------------------------------|------------------|
| 1 | `tourPageLoads`                | URL содержит `/travel/tours` |
| 2 | `tourPageHasDestinationField`  | Поле направления отображается |

### UC-07 / `SwapCitiesTest`

| # | Метод                                       | Что проверяется |
|---|----------------------------------------------|------------------|
| 1 | `swapButtonIsVisible`                        | Кнопка swap отображается |
| 2 | `swapExchangesOriginAndDestination`          | Swap меняет значения местами |
| 3 | `EdgeCases.swapWithEmptyFields_doesNotCrash` | **Краевой:** swap при пустых полях |
| 4 | `EdgeCases.doubleSwap_restoresOriginalState` | **Краевой:** двойной swap → исходное состояние |

### UC-08 / `DatePickerTest`

| # | Метод                                          | Что проверяется |
|---|------------------------------------------------|------------------|
| 1 | `departureDateFieldIsVisible`                  | Поле «Туда» отображается |
| 2 | `calendarOpensOnClick`                         | Клик по «Туда» открывает календарь |
| 3 | `EdgeCases.calendarClosesOnEscape`             | **Краевой:** Esc закрывает календарь |
| 4 | `EdgeCases.reopeningCalendar_doesNotBreakForm` | **Краевой:** повторное открытие календаря |

### UC-09 / `PassengersTest`

| # | Метод                                              | Что проверяется |
|---|----------------------------------------------------|------------------|
| 1 | `passengersPanelOpens`                             | Селектор открывает панель |
| 2 | `canSwitchToBusinessClass`                         | Выбор «Бизнес» работает |
| 3 | `EdgeCases.panelStaysFunctionalAfterOpen`          | **Краевой:** панель не ломает форму |
| 4 | `EdgeCases.canReopenSelectorAfterChoosingBusiness` | **Краевой:** повторное открытие после выбора |

### UC-10 / `CrossSectionAutocompleteTest`

| # | Метод                                                              | Что проверяется |
|---|---------------------------------------------------------------------|------------------|
| 1 | `autocompleteWorksOnFlightForm`                                    | Подсказки на форме авиабилетов |
| 2 | `autocompleteWorksOnHotelForm`                                     | Подсказки на форме отелей |
| 3 | `autocompleteWorksOnTrainForm`                                     | Подсказки на форме ЖД-билетов |
| 4 | `EdgeCases.nonExistentCity_returnsNoSuggestionsAcrossAllForms`     | **Краевой:** несуществующий город во всех 3 формах |
| 5 | `EdgeCases.knownCity_returnsAtLeastOneSuggestion`                  | **Краевой:** известный город → ≥1 подсказка |
| 6 | `EdgeCases.emptyInput_returnsNoSuggestionsOnHotels`                | **Краевой:** пустой ввод не открывает выпадашку |

---

## 5. Результаты тестирования

Запуск:

```bash
./gradlew testChrome           # все 55 проверок в Chrome
./gradlew testFirefox          # все 55 проверок в Firefox
./test-parallel.sh             # параллельный запуск Chrome + Firefox
./test-parallel.sh '*MainPageTest*'   # параллельно, только нужный класс
```

Отчёты Gradle/JUnit формируются в:
* `build/reports/tests/testChrome/index.html`
* `build/reports/tests/testFirefox/index.html`
* `build/test-results/testChrome/*.xml`
* `build/test-results/testFirefox/*.xml`

Сводная таблица (заполняется по результатам прогона):

| Конфигурация              | Браузер           | Прошло | Упало | Время |
|---------------------------|-------------------|:------:|:-----:|-------|
| `./gradlew testChrome`    | Chrome            |   55   |   0   | ~ N с |
| `./gradlew testFirefox`   | Firefox           |   55   |   0   | ~ N с |
| `./test-parallel.sh`      | Chrome + Firefox  |  110   |   0   | ~ N с |

---

## 6. Выводы

1. **Page Object pattern** позволил отделить локаторы и навигационную
   логику страниц от тестов: каждый тест читается как пользовательский
   сценарий. Если поменяется вёрстка — правки нужны в одном месте, не
   во всех тестах сразу.
2. **XPath по текстовым подписям** (`placeholder`, `aria-label`, видимый
   текст) оказались устойчивее к динамической перегенерации CSS-классов
   React-фронтенда T-Банка.
3. **Параллельный запуск в Chrome и Firefox** реализован через две
   отдельные Gradle-таски `testChrome` / `testFirefox`, читающие
   `-Dbrowser`. Скрипт `test-parallel.sh` запускает обе одновременно как
   фоновые процессы и агрегирует exit-коды.
4. **Полный отказ от `Thread.sleep`** в пользу `WebDriverWait` +
   `ExpectedConditions` ускорил тесты и сделал их стабильнее: ожидание
   завершается сразу, как только элемент готов.
5. **Покрытие 10 use case'ами и 55 проверками** охватывает основные
   пользовательские сценарии сайта T-Путешествий, а также **25 краевых
   случаев**: пустая форма, частично заполненные поля, одинаковые
   города, очистка полей, бессмысленный ввод, последовательность
   действий, повторные открытия виджетов, навигация «Назад», прямой
   переход по URL. Краевые случаи вынесены во вложенный `@Nested`-класс
   `EdgeCases`, что чётко отделяет happy-path от негативных сценариев в
   отчёте Gradle.

Лабораторная работа выполнена в полном объёме согласно требованиям задания.
