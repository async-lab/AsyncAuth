plugins {
    `kotlin-dsl`
}

repositories {
    maven { url = uri("https://maven.neoforged.net/releases") }
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    maven { url = uri("https://maven.minecraftforge.net") }
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.2")
    implementation("org.jetbrains.gradle.plugin.idea-ext:org.jetbrains.gradle.plugin.idea-ext.gradle.plugin:1.1.7")
    implementation("net.minecraftforge.gradle:net.minecraftforge.gradle.gradle.plugin:6.0.43")
}
