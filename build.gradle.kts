import com.moowork.gradle.node.npm.NpmTask

plugins {
    java
    groovy
    application
    id("org.springframework.boot") version "2.1.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("com.moowork.node") version "1.2.0"
    id("com.gradle.build-scan") version "2.1"
    id("com.coditory.integration-test") version "1.0.5"
    id("net.ltgt.errorprone") version "0.8"
    id("com.adarshr.test-logger") version "1.6.0"
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
    errorprone("com.google.errorprone:error_prone_core:2.3.3")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.retry:spring-retry")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.9.8")
    implementation("org.zalando:problem-spring-web:0.22.0")
    implementation("com.github.slugify:slugify:2.3")
    implementation("io.projectreactor:reactor-core:3.2.5.RELEASE")
    implementation("io.vavr:vavr:0.9.3")
    implementation("io.micrometer:micrometer-registry-prometheus:1.1.4")

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
    inputs.file("package-lock.json").withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file("webpack.config.js").withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir("src/main/js").withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir("$buildDir/resources/main/static")
    outputs.cacheIf { true }
    dependsOn("npmInstall")
    setArgs(listOf("run", "webpack"))
}

task<NpmTask>("watch") {
    setArgs(listOf("run", "watch"))
}

node {
    version = "12.3.0"
    npmVersion = "6.9.0"
    download = true
}

tasks {
    processResources {
        dependsOn("webpack")
    }
}

val distTar = tasks.getByName("distTar")
distTar.enabled = false

val bootDistTar = tasks.getByName("bootDistTar")
bootDistTar.enabled = false
