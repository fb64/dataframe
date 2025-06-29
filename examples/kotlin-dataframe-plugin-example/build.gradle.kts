import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    kotlin("jvm") version "2.2.20-dev-3524"
    kotlin("plugin.dataframe") version "2.2.20-dev-3524"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://packages.jetbrains.team/maven/p/kt/dev/")
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:1.0.0-Beta2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

configure<KtlintExtension> {
    version = "1.6.0"
    // rules are set up through .editorconfig
}
