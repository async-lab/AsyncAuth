import club.asynclab.asyncauth.Deps
import club.asynclab.asyncauth.Process
import club.asynclab.asyncauth.Props
import club.asynclab.asyncauth.api.toMap
import org.gradle.api.tasks.compile.JavaCompile
import net.neoforged.gradle.common.extensions.MinecraftExtension

val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neo_version: String by project
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

version = "neoforge-$minecraftVersion-${Props.MOD_VERSION}"
group = Props.MOD_GROUP_ID
base.archivesName.set(Props.MOD_ID)
kotlin.jvmToolchain(21)

plugins {
    id("net.neoforged.gradle.userdev") version "7.1.4"
}

extensions.configure<MinecraftExtension>("minecraft") {
    modIdentifier(Props.MOD_ID)
    mappings.version(minecraftMappingChannel, minecraftMappingVersion)
    accessTransformers.file(file("src/main/resources/META-INF/accesstransformer.cfg"))
}

dependencies {
    val mc = "net.neoforged:neoforge:${neo_version}"
    val mx = "org.spongepowered:mixin:0.8.5:processor"
    val kff = "thedarkcolour:kotlinforforge:${kotlinForForgeVersion}"

    compileOnly(mc)
    runtimeOnly(mc)
    annotationProcessor(mx)

    implementation(kff)

    implementationWithShade(project(":common"))
    implementationWithShade(Deps.HIKARI) {
        exclude("org.slf4j", "slf4j-api")
    }
    implementationWithShade(Deps.JWT)
    implementationWithShade(Deps.MYSQL)
    fullShade(Deps.MYSQL)
}

val mixinRefmap = "${Props.MOD_ID}.refmap.json"

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(
        listOf(
            "-AoutRefMap=$mixinRefmap",
            "-AdefaultObfuscationEnv=named"
        )
    )
}

val props = mapOf(
    "minecraft_version" to minecraftVersion,
    "minecraft_version_range" to minecraftVersionRange,
    "neo_version" to neo_version,
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

tasks.shadowJar { Process.configureGenerally(this)(shade, fullShade) }
tasks.build { dependsOn("shadowJar") }
