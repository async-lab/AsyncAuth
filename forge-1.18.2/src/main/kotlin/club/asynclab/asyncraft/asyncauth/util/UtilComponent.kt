package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent

object UtilComponent {
    fun getTranslatableComponent(key: String) = TranslatableComponent(key)
    fun getLiteralComponent(text: String) = TextComponent(text)
}