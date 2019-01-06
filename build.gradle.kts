buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.1.1.RELEASE")
    }
}

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

plugins {
    java
    application
}

application {
    mainClassName = "pl.allegro.tech.leaders.HackathonServer"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

group = "pl.allegro.tech.leaders"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.8")
    implementation("org.zalando:problem-spring-web:0.22.0")
    implementation("com.github.slugify:slugify:2.3")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.2.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude("junit", "junit") }
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    testCompile("org.junit.jupiter:junit-jupiter-params:5.2.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.2.0")
}