import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.topru.rawhttp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    // https://mvnrepository.com/artifact/com.athaydes.rawhttp/rawhttp-core
    //runtime group: 'com.athaydes.rawhttp', name: 'rawhttp-core', version: '1.1.0'

    implementation("com.athaydes.rawhttp:rawhttp-core:2.4.1")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}