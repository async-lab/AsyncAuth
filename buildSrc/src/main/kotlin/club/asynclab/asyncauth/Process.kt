package club.asynclab.asyncauth

import club.asynclab.asyncauth.Utils.relocateToShadowed
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

object Process {
    fun ShadowJar.configureGenerally(shade: Configuration, fullShade: Configuration) {
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

    fun Project.createGenerateTemplate(props: Map<String, String>) = tasks.register<Copy>("generateTemplates") {
        val src = file(project.projectDir.resolve("src/main/templates/"))
        val dst = layout.buildDirectory.dir("generated/sources/templates/")
        inputs.properties(props)

        from(src)
        into(dst)
        expand(props)
    }.also {
        val sourceSets = the<SourceSetContainer>()
        sourceSets["main"].java.srcDirs(listOf("java", "kotlin").map(it.get().destinationDir::resolve))
        rootProject.the<IdeaModel>().project.settings.taskTriggers.afterSync(it)
        project.the<EclipseModel>().synchronizationTasks(it)
    }
}