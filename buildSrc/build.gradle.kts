plugins {
    `kotlin-dsl`
}

repositories {
    maven { url = uri("https://maven.neoforged.net/releases") }
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.2")
}
