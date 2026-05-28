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

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8")
    systemProperty("file.encoding", "UTF-8")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.seleniumhq.selenium:selenium-java:4.20.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.4")
    testImplementation("org.slf4j:slf4j-simple:2.0.9")
}

val testFilter: String? = System.getProperty("tests")

fun Test.applyCommonConfig(browser: String) {
    useJUnitPlatform()
    systemProperty("browser", browser)
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    if (testFilter != null) filter { includeTestsMatching(testFilter) }
    outputs.upToDateWhen { false }
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = false
    }
    reports {
        html.outputLocation = layout.buildDirectory.dir("reports/tests/test${browser.replaceFirstChar { it.uppercaseChar() }}")
        junitXml.outputLocation = layout.buildDirectory.dir("test-results/test${browser.replaceFirstChar { it.uppercaseChar() }}")
    }
}

// Запуск в Chrome:   ./gradlew testChrome
val testChrome by tasks.registering(Test::class) {
    description = "Запуск всех функциональных тестов в Chrome"
    group = "verification"
    applyCommonConfig("chrome")
}

// Запуск в Firefox:  ./gradlew testFirefox
val testFirefox by tasks.registering(Test::class) {
    description = "Запуск всех функциональных тестов в Firefox"
    group = "verification"
    applyCommonConfig("firefox")
}

// Запуск параллельно в обоих браузерах: ./gradlew testAll --parallel
// Gradle сам распараллелит таски, помеченные как независимые, при флаге --parallel.
val testAll by tasks.registering {
    description = "Запуск тестов одновременно в Chrome и Firefox (используйте --parallel)"
    group = "verification"
    dependsOn(testChrome, testFirefox)
}

tasks.named("test") {
    dependsOn(testChrome)
}
