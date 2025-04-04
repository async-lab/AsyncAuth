import club.asynclab.asyncauth.Deps
import club.asynclab.asyncauth.Process.configureGenerally
import club.asynclab.asyncauth.Props
import club.asynclab.asyncauth.api.toMap
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val forgeVersion: String by project
val forgeVersionRange: String by project
val modLoader: String by project
val modLoaderVersionRange: String by project
val minecraftMappingChannel: String by project
val minecraftMappingVersion: String by project
val kotlinForForgeVersion: String by project
val kotlinForForgeVersionRange: String by project
val jeiVersion: String by project

val shade: Configuration by configurations.creating

val fullShade: Configuration by configurations.creating
shade.extendsFrom(fullShade)

val implementationWithShade: Configuration by configurations.creating
configurations.implementation.get().extendsFrom(implementationWithShade)
shade.extendsFrom(implementationWithShade)

val minecraftLibraryWithShade: Configuration by configurations.creating
configurations.minecraftLibrary.get().extendsFrom(minecraftLibraryWithShade)
shade.extendsFrom(minecraftLibraryWithShade)

version = "forge-$minecraftVersion-${Props.MOD_VERSION}"
group = Props.MOD_GROUP_ID
base.archivesName.set(Props.MOD_ID)
kotlin.jvmToolchain(17)

plugins {
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
    id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

minecraft {
    mappings(minecraftMappingChannel, minecraftMappingVersion)
    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    copyIdeResources.set(true)

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")

            mods {
                create(Props.MOD_ID) {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }

        create("client") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
        create("server") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
        create("gameTestServer") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
        create("data") {
            workingDirectory(project.file("run-data"))

            args(
                "--mod", Props.MOD_ID,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }
}

mixin {
    add(sourceSets.main.get(), "${Props.MOD_ID}.refmap.json")
    config("${Props.MOD_ID}.mixins.json")
}

dependencies {
    val mc = "net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}"
    val mx = "org.spongepowered:mixin:0.8.5:processor"
    val kff = "thedarkcolour:kotlinforforge:${kotlinForForgeVersion}"

    minecraft(mc)
    annotationProcessor(mx)

    implementation(kff)

    implementationWithShade(project(":common"))
    minecraftLibraryWithShade(Deps.HIKARI) {
        exclude("org.slf4j", "slf4j-api")
    }
    minecraftLibrary(Deps.MYSQL)
    fullShade(Deps.MYSQL)
}

val props = mapOf(
    "minecraft_version" to minecraftVersion,
    "minecraft_version_range" to minecraftVersionRange,
    "forge_version" to forgeVersion,
    "forge_version_range" to forgeVersionRange,
    "mod_loader" to modLoader,
    "mod_loader_version_range" to modLoaderVersionRange,
    "kotlin_for_forge_version" to kotlinForForgeVersion,
    "kotlin_for_forge_version_range" to kotlinForForgeVersionRange,
) + Props.toMap()

tasks.processResources {
    inputs.properties(props)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(props)
    }
}

sourceSets["main"].resources.srcDirs("src/generated/resources")

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to Props.MOD_ID,
                "Specification-Vendor" to Props.MOD_AUTHORS,
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to Props.MOD_AUTHORS,
                "Implementation-Timestamp" to DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                    .format(Date().toInstant().atOffset(ZoneOffset.UTC))
            )
        )
    }
    finalizedBy("reobfJar")
}

tasks.compileJava { outputs.upToDateWhen { false } }
tasks.shadowJar { configureGenerally(shade, fullShade) }
reobf { create("shadowJar") {} }
tasks.build { dependsOn("shadowJar") }
