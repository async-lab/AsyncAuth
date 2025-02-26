import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"

//    id("com.gradleup.shadow") version "8.3.2" apply false
//    kotlin("jvm") version "1.9.23" apply false
//    kotlin("kapt") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.23" apply false
    kotlin("plugin.lombok") version "1.9.23" apply false
}

subprojects {
    plugins.apply("java")
    plugins.apply("eclipse")
    plugins.apply("idea")
    plugins.apply("maven-publish")
    plugins.apply("java-library")
    plugins.apply("com.gradleup.shadow")
    plugins.apply("org.jetbrains.gradle.plugin.idea-ext")
    plugins.apply("org.jetbrains.kotlin.jvm")
    plugins.apply("org.jetbrains.kotlin.kapt")
    plugins.apply("org.jetbrains.kotlin.plugin.serialization")
    plugins.apply("org.jetbrains.kotlin.plugin.lombok")

    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://jitpack.io") }
        maven("Kotlin for Forge") { url = uri("https://thedarkcolour.github.io/KotlinForForge/") }
        maven("Jared's maven") { url = uri("https://maven.blamejared.com/") }
        maven("Aquaculture") { url = uri("https://girafi.dk/maven/") }
        maven("ModMaven") { url = uri("https://modmaven.dev") }
        maven { url = uri("https://www.cursemaven.com"); content { includeGroup("curse.maven") } }
        mavenLocal()
        mavenCentral()
    }

    configure<IdeaModel> {
        module.isDownloadJavadoc = true
        module.isDownloadSources = true
    }

    configure<KaptExtension> { keepJavacAnnotationProcessors = true }

    tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }

    tasks.withType<ShadowJar> {
//        relocateToShadowPath
    }
}

