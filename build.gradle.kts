plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "io.devtory"
version = "0.0.1-SNAPSHOT"
description = "devtory3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // jackson kotlin module
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Redis
    implementation("org.redisson:redisson-spring-boot-starter:4.1.0")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // db
    runtimeOnly("com.mysql:mysql-connector-j")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
