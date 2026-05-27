# TPO Lab 3 — Functional UI tests for T-Bank Travel


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
