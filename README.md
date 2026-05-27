# TPO Lab 3 — Functional UI tests for T-Bank Travel

Автоматизированные функциональные тесты сайта <https://www.tbank.ru/travel/>
на Java + Selenium WebDriver + JUnit 5 + Gradle.

* **Page Object** — пакет `ru.itmo.tpo.lab3.page`.
* **Только XPath**-локаторы (требование лабораторной).
* **`WebDriverWait` + `ExpectedConditions`** — никакого `Thread.sleep()`.
* **Кросс-браузерный запуск** в Chrome и Firefox — по отдельности или
  параллельно через `test-parallel.sh`.

## Документация

| Файл                                                         | Содержание                                |
|--------------------------------------------------------------|-------------------------------------------|
| [docs/use-cases.md](docs/use-cases.md)                       | Карточки 10 прецедентов использования     |
| [docs/use-case-diagram.md](docs/use-case-diagram.md)         | UseCase-диаграмма (PlantUML + Mermaid)    |
| [checklist.md](checklist.md)                                 | Чек-лист тестового покрытия (30 проверок) |
| [docs/report/report.md](docs/report/report.md)               | Полный отчёт по лабораторной              |

## Требования

| Компонент      | Версия / комментарий                              |
|----------------|----------------------------------------------------|
| JDK            | 17+                                                |
| Gradle         | через `./gradlew` (jar лежит в `gradle/wrapper/`)  |
| Google Chrome  | актуальная — драйвер ставится автоматически        |
| Mozilla Firefox| актуальная — драйвер ставится автоматически        |

Драйверы (`chromedriver`, `geckodriver`) скачиваются автоматически через
WebDriverManager при первом запуске — устанавливать вручную не нужно.

## Структура проекта

```
.
├── build.gradle.kts                   # Gradle сборка (Java + JUnit 5 + Selenium)
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat              # Gradle wrapper
├── test-parallel.sh                   # параллельный запуск Chrome+Firefox
├── checklist.md                       # чек-лист покрытия
├── docs/
│   ├── use-cases.md                   # карточки UC
│   ├── use-case-diagram.md            # UseCase-диаграмма
│   └── report/report.md               # отчёт по лабораторной
└── src/test/java/ru/itmo/tpo/lab3/
    ├── DriverFactory.java             # создание Chrome/Firefox драйвера
    ├── page/                          # Page Objects
    │   ├── BasePage.java
    │   ├── MainPage.java
    │   ├── FlightSearchPage.java
    │   ├── HotelSearchPage.java
    │   ├── TrainSearchPage.java
    │   └── TourPage.java
    └── test/                          # JUnit 5 тесты (один класс на UC)
        ├── BaseTest.java
        ├── MainPageTest.java                  # UC-01
        ├── NavigationTest.java                # UC-02
        ├── FlightSearchTest.java              # UC-03
        ├── HotelSearchTest.java               # UC-04
        ├── TrainSearchTest.java               # UC-05
        ├── TourPageTest.java                  # UC-06
        ├── SwapCitiesTest.java                # UC-07
        ├── DatePickerTest.java                # UC-08
        ├── PassengersTest.java                # UC-09
        └── CrossSectionAutocompleteTest.java  # UC-10
```

## Запуск тестов

### Только Chrome

```bash
./gradlew testChrome
```

### Только Firefox

```bash
./gradlew testFirefox
```

### Chrome и Firefox параллельно

```bash
./test-parallel.sh
```

Запустить параллельно только нужный класс/метод:

```bash
./test-parallel.sh '*MainPageTest*'
./test-parallel.sh 'ru.itmo.tpo.lab3.test.FlightSearchTest.fullOneWayFlightSearchOpensResults'
```

### Стандартный `./gradlew test`

```bash
./gradlew test                    # браузер по умолчанию (chrome)
./gradlew test -Dbrowser=firefox  # явно указать firefox
```

### Headless-режим

```bash
./gradlew testChrome -Dheadless=true
```

## Где смотреть результаты

После прогона:
* HTML-отчёт Chrome:  `build/reports/tests/testChrome/index.html`
* HTML-отчёт Firefox: `build/reports/tests/testFirefox/index.html`
* JUnit XML:          `build/test-results/testChrome/`, `build/test-results/testFirefox/`

## Как добавлять новые тесты

1. При необходимости — расширьте/создайте Page Object в
   `src/test/java/ru/itmo/tpo/lab3/page/` (только XPath-локаторы).
2. Унаследуйте тестовый класс от `BaseTest` (он сам создаст и закроет
   браузер согласно `-Dbrowser`).
3. Используйте методы `waitVisible / waitClickable / waitForUrl` —
   никаких `Thread.sleep`.
4. Добавьте описание сценария в [checklist.md](checklist.md) и
   [docs/report/report.md](docs/report/report.md).
