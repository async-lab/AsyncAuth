package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent

object UtilComponent {
    @JvmStatic
    fun getTranslatableComponent(key: String) = TranslatableComponent(key)

    @JvmStatic
    fun getLiteralComponent(text: String) = TextComponent(text)
}