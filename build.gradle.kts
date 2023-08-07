import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("jacoco")// This is to use Jacoco for coverage testing
    id("io.gitlab.arturbosch.detekt") version("1.23.0")
}
detekt {
    toolVersion = "1.23.0"
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.8.21")
        }
    }
}

group = "com.hrv.mart"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/hrv-mart/custom-pageable")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    // detekt pluginstasks.create
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
    // HRV-Mart dependency
    implementation("com.hrv.mart:custom-pageable:0.0.2")
    implementation("com.hrv.mart:user-library:0.0.3")
    // Test-Container
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:mongodb")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}
tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    // To run Jacoco Test Coverage Verification
    finalizedBy("jacocoTestCoverageVerification")
}
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            excludes = listOf(
                "${group}.backendreview.repository.ReviewRepository.kt.*"
            )
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}