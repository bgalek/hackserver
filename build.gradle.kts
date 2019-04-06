import com.moowork.gradle.node.npm.NpmTask

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
    groovy
    application
    id("com.moowork.node") version "1.2.0"
    id("com.gradle.build-scan") version "2.1"
    id("com.coditory.integration-test") version "1.0.5"
}

application {
    mainClassName = "pl.allegro.tech.leaders.hackathon.HackathonServer"
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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.retry:spring-retry")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.8")
    implementation("org.zalando:problem-spring-web:0.22.0")
    implementation("com.github.slugify:slugify:2.3")
    implementation("io.projectreactor:reactor-core:3.2.5.RELEASE")
    implementation("io.vavr:vavr:0.9.3")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.spockframework:spock-core:1.3-groovy-2.5")
    testImplementation("io.projectreactor:reactor-test:3.2.5.RELEASE")
    testImplementation("cglib:cglib-nodep:3.2.10")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.0")
    testImplementation("org.objenesis:objenesis:3.0.1")

    integrationImplementation("org.spockframework:spock-spring:1.3-RC1-groovy-2.5")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")

    integrationRuntime("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.2.0")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

task<NpmTask>("webpack") {
    dependsOn("npmInstall")
    setArgs(listOf("run", "webpack"))
}

task<NpmTask>("watch") {
    setArgs(listOf("run", "watch"))
}

node {
    version = "11.6.0"
    npmVersion = "6.5.0"
    download = true
}

tasks {

    processResources {
        // TODO: After adding this task dependency running unit tests in IDE slowed down drastically. You could specify task caching.
        dependsOn("webpack")
    }

    withType<Test> {
        testLogging {
            setExceptionFormat("full")
            events("passed", "skipped", "failed")
        }
    }
}

val distTar = tasks.getByName("distTar")
distTar.enabled = false

val bootDistTar = tasks.getByName("bootDistTar")
bootDistTar.enabled = false
