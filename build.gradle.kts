import club.asynclab.asyncauth.Props
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")

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
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://jitpack.io")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("Jared's maven") { url = uri("https://maven.blamejared.com/") }
        maven("Aquaculture") { url = uri("https://girafi.dk/maven/") }
        maven("https://modmaven.dev")
        maven { url = uri("https://www.cursemaven.com"); content { includeGroup("curse.maven") } }
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

