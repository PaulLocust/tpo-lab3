plugins {
    java
}

group = "ru.itmo.tpo.lab3"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// UTF-8 для исходников и для всех task'ов
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    // JVM тестов будет писать stdout/stderr в UTF-8
    jvmArgs("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
    systemProperty("file.encoding", "UTF-8")
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Selenium 4
    testImplementation("org.seleniumhq.selenium:selenium-java:4.20.0")

    // WebDriverManager — автоматически качает chromedriver/geckodriver
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.4")

    // Логирование
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("browser", System.getProperty("browser", "chrome"))
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = false
    }
}

val testFilter: String? = System.getProperty("tests")

// Запуск только в Chrome:  ./gradlew testChrome
val testChrome by tasks.registering(Test::class) {
    description = "Запуск всех функциональных тестов в Chrome"
    group = "verification"
    useJUnitPlatform()
    systemProperty("browser", "chrome")
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    if (testFilter != null) filter { includeTestsMatching(testFilter) }
    outputs.upToDateWhen { false }
    reports {
        html.outputLocation = layout.buildDirectory.dir("reports/tests/testChrome")
        junitXml.outputLocation = layout.buildDirectory.dir("test-results/testChrome")
    }
}

// Запуск только в Firefox: ./gradlew testFirefox
val testFirefox by tasks.registering(Test::class) {
    description = "Запуск всех функциональных тестов в Firefox"
    group = "verification"
    useJUnitPlatform()
    systemProperty("browser", "firefox")
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    if (testFilter != null) filter { includeTestsMatching(testFilter) }
    outputs.upToDateWhen { false }
    reports {
        html.outputLocation = layout.buildDirectory.dir("reports/tests/testFirefox")
        junitXml.outputLocation = layout.buildDirectory.dir("test-results/testFirefox")
    }
}
