import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    groovy
    application
    jacoco
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.node-gradle.node") version "3.1.0"
    id("com.coditory.integration-test") version "1.2.1"
    id("net.ltgt.errorprone") version "2.0.1"
    id("com.adarshr.test-logger") version "3.0.0"
    id("org.sonarqube") version "2.7"
}

application {
    mainClass.set("pl.allegro.tech.leaders.hackathon.HackathonServer")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
        vendor.set(JvmVendorSpec.ADOPTOPENJDK)
    }
}

group = "pl.allegro.tech.leaders"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.7.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.retry:spring-retry")
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.12.3")
    implementation("org.zalando:problem-spring-web:0.22.0")
    implementation("com.github.slugify:slugify:2.4")
    implementation("io.projectreactor:reactor-core:3.4.6")
    implementation("io.vavr:vavr:0.10.3")
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.0")

    testImplementation("org.codehaus.groovy:groovy-all:3.0.8")
    testImplementation(platform("org.spockframework:spock-bom:2.0-groovy-3.0"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("io.projectreactor:reactor-test:3.4.6")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.9")

    testRuntimeOnly("cglib:cglib-nodep:3.3.0")
    testRuntimeOnly("org.objenesis:objenesis:3.1")

    integrationImplementation("org.codehaus.groovy:groovy-json:2.5.14")
    integrationImplementation("org.spockframework:spock-spring")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.0.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

task<NpmTask>("webpack") {
    inputs.file("package-lock.json").withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file("webpack.config.js").withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir("src/main/js").withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir("$buildDir/resources/main/static")
    outputs.cacheIf { true }
    dependsOn("npmInstall")
    args.set(listOf("run", "webpack"))
}

task<NpmTask>("watch") {
    args.set(listOf("run", "watch"))
}

node {
    version.set("16.3.0")
    npmVersion.set("7.13.0")
    download.set(true)
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
