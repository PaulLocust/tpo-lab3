# TPO Lab 3 — Functional UI tests for T-Bank Travel

Функциональное тестирование пользовательского интерфейса сайта
[tbank.ru/travel](https://www.tbank.ru/travel/) с помощью Selenium WebDriver
(Java + JUnit 5). Покрытие построено на основе 6 прецедентов использования
(см. [docs/use-cases.md](docs/use-cases.md)) — выбор авиабилетов, отелей,
билетов на поезд и автобус, тура, экскурсии.

Все тесты — только UI-проверки: фильтры выдачи, отображение информации
в карточках, корректность переходов в карточки и на партнёрские сайты.
Никакого ввода персональных данных (e-mail, телефон, оплата), потому что
сайт работает на проде.

## Структура проекта

```
.
├── build.gradle.kts                     # Gradle (Java + JUnit 5 + Selenium 4)
├── test-parallel.sh                     # параллельный запуск Chrome+Firefox
├── checklist.md                         # чек-лист покрытия
├── docs/
│   ├── use-cases.md                     # карточки UC
│   ├── use-case-diagram.md              # UseCase-диаграмма (PlantUML + Mermaid)
│   └── report/report.md                 # отчёт
└── src/test/java/ru/itmo/tpo/lab3/
    ├── DriverFactory.java               # создание Chrome/Firefox через WebDriverManager
    ├── page/                            # Page Objects (XPath, без ID)
    │   ├── BasePage.java
    │   ├── FlightSearchPage.java
    │   ├── FlightResultsPage.java
    │   ├── HotelSearchPage.java
    │   ├── HotelResultsPage.java
    │   ├── HotelDetailsPage.java
    │   ├── FavoritesPage.java
    │   ├── TrainSearchPage.java
    │   ├── TourPage.java
    │   ├── BusPage.java
    │   └── ExcursionPage.java
    └── test/                            # JUnit 5 тесты — по одному классу на UC
        ├── BaseTest.java
        ├── FlightSearchTest.java        # UC-1
        ├── HotelSearchTest.java         # UC-2
        ├── TrainPartnerTest.java        # UC-3
        ├── TourPartnerTest.java         # UC-4
        ├── BusPartnerTest.java          # UC-5
        └── ExcursionPartnerTest.java    # UC-6
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

### Chrome и Firefox одновременно (параллельно)

Вариант 1 — через Gradle (`--parallel` указывает Gradle на возможность
параллельного выполнения независимых задач):

```bash
./gradlew testAll --parallel
```

Вариант 2 — через bash-скрипт:

```bash
./test-parallel.sh
./test-parallel.sh '*HotelSearchTest*'
./test-parallel.sh 'ru.itmo.tpo.lab3.test.FlightSearchTest.endToEndSearchOpensResults'
```

### Стандартный `./gradlew test`

```bash
./gradlew test                       # запускает testChrome
./gradlew test -Dbrowser=firefox     # сменить браузер
```

### Headless-режим

```bash
./gradlew testChrome -Dheadless=true
```

## Где смотреть результаты

После прогона:
* HTML Chrome:  `build/reports/tests/testChrome/index.html`
* HTML Firefox: `build/reports/tests/testFirefox/index.html`
* JUnit XML:    `build/test-results/testChrome/`, `build/test-results/testFirefox/`
