package club.asynclab.asyncauth

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

object Utils {
    fun ShadowJar.relocateToShadowed(vararg paths: String): ShadowJar {
        paths.forEach { relocate(it, "${Props.MOD_GROUP_ID}.${Props.MOD_ID}.shadowed.$it") }
        return this
    }
}