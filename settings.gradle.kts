pluginManagement {
    repositories {
        maven { url = uri("https://maven.neoforged.net/releases") }
        maven { url = uri("https://maven.parchmentmc.org") }
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

fileTree(rootProject.projectDir)
    .matching { include("*/build.gradle.kts") }
    .files.map { it.parentFile.name }
    .filter { it != "buildSrc" }
    .forEach { include(it) }