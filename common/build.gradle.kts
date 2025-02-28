import club.asynclab.asyncauth.Deps
import club.asynclab.asyncauth.Props
import club.asynclab.asyncauth.api.toMap
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

val slf4jVersion: String by project

val delivery: Configuration by configurations.creating
configurations.api.get().extendsFrom(delivery)

version = Props.MOD_VERSION
group = Props.MOD_GROUP_ID
base.archivesName.set("${Props.MOD_ID}-common")
kotlin.jvmToolchain(8)

dependencies {
    val slf4jApi = "org.slf4j:slf4j-api:2.0.7"
    val netty = "io.netty:netty-all:4.1.117.Final"

    // mcLib
    compileOnly(slf4jApi)
    compileOnly(netty)

    // lib
    Deps.toMap().forEach { compileOnly(it.value) }

    // Kotlin
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
}

val props = Props.toMap()

val generateTemplates by tasks.registering(Copy::class) {
    val src = file("src/main/templates/java")
    val dst = layout.buildDirectory.dir("generated/sources/templates/java")
    inputs.properties(props)

    doFirst {
        dst.get().asFile.deleteRecursively()
        dst.get().asFile.mkdirs()
    }

    from(src)
    into(dst)
    expand(props)
}

rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)
project.eclipse.synchronizationTasks(generateTemplates)

val generateLang by tasks.registering(JavaExec::class) {
    classpath = sourceSets["main"].compileClasspath + sourceSets["main"].runtimeClasspath
    mainClass = "${Props.MOD_GROUP_ID}.${Props.MOD_ID}.common.misc.Lang"
    workingDir = file("src/generated/resources/assets/${Props.MOD_ID}/lang")
    doFirst {
        if (!workingDir.exists()) {
            workingDir.deleteRecursively()
            workingDir.mkdirs()
        }
    }
}

val processResourcesAgain by tasks.registering(ProcessResources::class) {
    dependsOn(generateLang)
}

tasks.jar.get().dependsOn(generateLang)

sourceSets["main"].resources.srcDirs("src/generated/resources")
sourceSets["main"].java.srcDirs(generateTemplates.map { it.destinationDir })
