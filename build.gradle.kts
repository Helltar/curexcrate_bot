plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.helltar"
version = "1.5.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.annimon:tgbots-module:8.0.0") {
        exclude("org.telegram", "telegrambots-webhook")
    }

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("ch.qos.logback:logback-classic:1.5.16")
}

application {
    mainClass.set("bot.CurexcrateBot")
}
