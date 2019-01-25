import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version = "1.1.2"

plugins {
    kotlin("jvm") version "1.3.11"
    application
}

application {
    // mainClassName = "PriceApiKt"
    mainClassName = "io.ktor.server.netty.EngineMain"
}

group = "net.dryft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(("https://dl.bintray.com/kotlin/ktor"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.21")
    implementation("com.beust:klaxon:3.0.8")
    implementation("io.ktor:ktor-server-netty:$ktor_version")

    testCompile("io.kotlintest:kotlintest-runner-junit5:3.2.1")
    testCompile("io.ktor:ktor-server-test-host:$ktor_version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

