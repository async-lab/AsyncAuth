package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object UtilComponent {
    @JvmStatic
    fun getTranslatableComponent(key: String): MutableComponent = Component.translatable(key)
    @JvmStatic
    fun getLiteralComponent(text: String): MutableComponent = Component.literal(text)
}