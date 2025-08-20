import club.asynclab.asyncauth.Deps
import club.asynclab.asyncauth.Process
import club.asynclab.asyncauth.Props
import club.asynclab.asyncauth.api.toMap

val slf4jVersion: String by project

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

val generateTemplates by Process.createGenerateTemplates(project)(props)
sourceSets["main"].resources.srcDirs("src/generated/resources")

val generateLang by tasks.registering(JavaExec::class) {
    workingDir = file("src/generated/resources/assets/${Props.MOD_ID}/lang")
    inputs.files(sourceSets["main"].allJava)
    outputs.dir(workingDir)

    classpath = sourceSets["main"].compileClasspath + sourceSets["main"].runtimeClasspath
    mainClass = "${Props.MOD_GROUP_ID}.${Props.MOD_ID}.common.misc.Lang"
    doFirst {
        workingDir.deleteRecursively()
        workingDir.mkdirs()
    }
}

val processResourcesAgain by tasks.registering(ProcessResources::class) { dependsOn(generateLang) }
tasks.jar.get().dependsOn(generateLang)
