plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "com.helltar"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("com.annimon:tgbots-module:6.5.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")

    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.6")
}

application {
    mainClass.set("CurExcRateBot")
}