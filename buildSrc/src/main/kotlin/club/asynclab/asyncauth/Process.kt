package club.asynclab.asyncauth

import club.asynclab.asyncauth.Utils.relocateToShadowed
import club.asynclab.asyncauth.misc.SimpleDelegator
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers
import java.io.File

object Process {
    fun configureGenerally(shadowJar: ShadowJar): (shade: Configuration, fullShade: Configuration) -> ShadowJar =
        { shade: Configuration, fullShade: Configuration ->
            shadowJar.apply {
                mergeServiceFiles()
                minimize {
                    fullShade.dependencies.forEach { exclude(dependency(it)) }
                }

                configurations = listOf(shade)

                relocateToShadowed(
                    "com.zaxxer.hikari",
                    "com.mysql.cj",
                    "com.mysql.jdbc",
                    "google.protobuf",
                    "com.google.protobuf",
                )
            }
        }

    fun configureGenerally(minecraft: UserDevExtension): (minecraftMappingChannel: String, minecraftMappingVersion: String, mainSourceSet: SourceSet, commonSourceSet: SourceSet) -> Unit =
        { minecraftMappingChannel: String, minecraftMappingVersion: String, mainSourceSet: SourceSet, commonSourceSet: SourceSet ->
            minecraft.apply {
                project.run {
                    mappings(minecraftMappingChannel, minecraftMappingVersion)

                    accessTransformer("src/main/resources/META-INF/accesstransformer.cfg")

                    copyIdeResources.set(true)

                    runs.apply {
                        configureEach {
                            workingDirectory(file("run"))
                            property("forge.logging.markers", "REGISTRIES")
                            property("forge.logging.console.level", "debug")

                            mods.apply {
                                create(Props.MOD_ID) {
                                    source(mainSourceSet)
                                    source(commonSourceSet)
                                }
                            }
                        }

                        create("client") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
                        create("server") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
                        create("gameTestServer") { property("forge.enabledGameTestNamespaces", Props.MOD_ID) }
                        create("data") {
                            workingDirectory(file("run-data"))

                            args(
                                "--mod", Props.MOD_ID,
                                "--all",
                                "--output", file("src/generated/resources/").absolutePath,
                                "--existing", file("src/main/resources/").absolutePath
                            )
                        }
                    }
                }
            }
        }

    fun createGenerateTemplates(project: Project): (props: Map<String, String>) -> SimpleDelegator<TaskProvider<Copy>> =
        { props: Map<String, String> ->
            project.run {
                SimpleDelegator({ name ->
                    tasks.register<Copy>(name) {
                        val src = file(projectDir.resolve("src/main/templates/"))
                        val dst = layout.buildDirectory.dir("generated/sources/templates/")
                        inputs.properties(props)

                        from(src)
                        into(dst)
                        expand(props)
                    }
                }, { generateTemplates ->
                    val sourceSets = the<SourceSetContainer>()
                    sourceSets["main"].java.srcDirs(generateTemplates.map { it.destinationDir }.map {
                        listOf("java", "kotlin").map(it::resolve)
                    })
                    rootProject.the<IdeaModel>().project.settings.taskTriggers.afterSync(generateTemplates)
                    the<EclipseModel>().synchronizationTasks(generateTemplates)
                })
            }
        }

    fun createGenerateFromBrother(project: Project): ((selfDirSet: SourceDirectorySet, brotherDirsProvider: () -> Iterable<File>, outputDir: String) -> SimpleDelegator<TaskProvider<Sync>>) =
        { selfDirSet: SourceDirectorySet, brotherDirsProvider: () -> Iterable<File>, outputDir: String ->
            project.run {
                SimpleDelegator({ name ->
                    val generatedDir = layout.projectDirectory.dir("src/generated/brother/$outputDir")
                    tasks.register<Sync>(name) {
                        description = "Generates sources from brother, excluding files that are already present."

                        from(brotherDirsProvider) {
                            exclude { element ->
                                val relativePath = element.relativePath.pathString
                                val existsInSelf = selfDirSet.srcDirs
                                    .filterNot { it.startsWith(generatedDir.asFile) }
                                    .any { it.resolve(relativePath).isFile }
                                if (existsInSelf) {
                                    println("Excluding '${relativePath}' because it is overridden.")
                                }
                                existsInSelf
                            }
                        }

                        exclude { element ->
                            if (!element.file.startsWith(generatedDir.asFile)) false
                            else {
                                val relativePath = element.relativePath.pathString
                                val existsInSelf = brotherDirsProvider()
                                    .none { it.resolve(relativePath).isFile }
                                if (existsInSelf) {
                                    println("Excluding '${relativePath}' because it is not existed in brother.")
                                }
                                existsInSelf
                            }
                        }
                        into(generatedDir)

                        doLast {
                            delete(generatedDir.asFile.walk().drop(1).filter { it.walk().none(File::isFile) }.toList())
                        }
                    }
                }, { generateFromBrother ->
                    selfDirSet.srcDirs(generateFromBrother)
                    tasks.named("compileJava") { dependsOn(generateFromBrother) }
                    rootProject.the<IdeaModel>().project.settings.taskTriggers.afterSync(generateFromBrother)
                    the<EclipseModel>().synchronizationTasks(generateFromBrother)
                    tasks.named<Delete>("clean") { delete(generateFromBrother) }
                })
            }
        }
}