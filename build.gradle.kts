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
    id("com.gradle.build-scan") version "2.1"
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

val integrationImplementation: Configuration by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

val integrationRuntime: Configuration by configurations.creating {
    extendsFrom(configurations.testRuntime.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.retry:spring-retry")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.8")
    implementation("org.zalando:problem-spring-web:0.22.0")
    implementation("com.github.slugify:slugify:2.3")
    implementation("io.projectreactor:reactor-core:3.2.5.RELEASE")

    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.codehaus.groovy:groovy-all:2.5.5")
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.5")
    testImplementation("io.projectreactor:reactor-test:3.2.5.RELEASE")

    integrationImplementation("com.github.tomakehurst:wiremock:2.20.0")
    integrationImplementation("org.spockframework:spock-spring:1.2-groovy-2.5")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("org.springframework.boot:spring-boot-starter-tomcat")

    integrationRuntime("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.2.0")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

sourceSets {
    create("integration") {
        withConvention(GroovySourceSet::class) {
            groovy.srcDir("src/integration/groovy")
            resources.srcDir("src/integration/resources")
            compileClasspath += sourceSets["main"].compileClasspath + sourceSets["test"].compileClasspath
            runtimeClasspath += sourceSets["main"].runtimeClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}

task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = sourceSets["integration"].output.classesDirs
    classpath = sourceSets["integration"].runtimeClasspath
    mustRunAfter(tasks["test"])
}

tasks {
    check {
        dependsOn("integrationTest")
    }

    withType<Test> {
        testLogging {
            setExceptionFormat("full")
            events("passed", "skipped", "failed")
        }
    }
}