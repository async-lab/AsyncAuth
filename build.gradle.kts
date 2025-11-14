import club.asynclab.asyncauth.Props
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

buildscript {
    repositories {
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.aliyun.com/repository/public/")
        mavenCentral()
    }
}

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
    kotlin("plugin.serialization") version "2.2.20" apply false
    kotlin("plugin.lombok") version "2.2.20" apply false
}

subprojects {
    listOf(
        "java",
        "eclipse",
        "idea",
        "maven-publish",
        "java-library",
        "com.gradleup.shadow",
        "org.jetbrains.gradle.plugin.idea-ext",
        "org.jetbrains.kotlin.jvm",
        "org.jetbrains.kotlin.kapt",
        "org.jetbrains.kotlin.plugin.serialization"
    ).forEach(plugins::apply)

    repositories {
//        maven("https://maven.neoforged.net/releases")
        maven("https://jitpack.io")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://modmaven.dev")
        maven("https://neoforged.forgecdn.net/releases")
        maven("https://libraries.minecraft.net")
        maven("https://neoforged.forgecdn.net/mojang-meta")
        maven("https://www.cursemaven.com") { content { includeGroup("curse.maven") } }
        mavenLocal()
        mavenCentral()
    }

    tasks.withType<Jar>().configureEach {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to Props.MOD_ID,
                    "Specification-Vendor" to Props.MOD_AUTHORS,
                    "Specification-Version" to "1", // We are version 1 of ourselves
                    "Implementation-Title" to Props.MOD_NAME,
                    "Implementation-Version" to Props.MOD_VERSION,
                    "Implementation-Vendor" to Props.MOD_AUTHORS,
                    "Implementation-Timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                        .format(Date().toInstant().atOffset(ZoneOffset.UTC))
                )
            )
        }
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

