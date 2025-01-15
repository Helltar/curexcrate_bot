plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.helltar"
version = "1.3.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.annimon:tgbots-module:8.0.0") {
        exclude("org.telegram", "telegrambots-webhook")
    }

    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.slf4j:slf4j-api:2.0.6")
}

application {
    mainClass.set("bot.CurExcRateBot")
}
