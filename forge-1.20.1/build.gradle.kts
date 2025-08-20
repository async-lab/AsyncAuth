import club.asynclab.asyncauth.Deps
import club.asynclab.asyncauth.Process
import club.asynclab.asyncauth.Props
import club.asynclab.asyncauth.api.toMap

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
    id("net.minecraftforge.gradle")
    id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

minecraft {
    Process.configureGenerally(this)(
        minecraftMappingChannel,
        minecraftMappingVersion,
        sourceSets.main.get(),
        project(":common").sourceSets.main.get()
    )
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

tasks.jar { finalizedBy("reobfJar") }

tasks.compileJava { outputs.upToDateWhen { false } }
tasks.shadowJar { Process.configureGenerally(this)(shade, fullShade) }
reobf { create("shadowJar") {} }
tasks.build { dependsOn("shadowJar") }
