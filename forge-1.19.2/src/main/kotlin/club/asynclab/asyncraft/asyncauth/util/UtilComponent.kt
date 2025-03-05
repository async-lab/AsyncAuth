package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.chat.Component

object UtilComponent {

    fun getTranslatableComponent(key: String) : Component {
        return Component.translatable(key)
    }

    fun getLiteralComponent(text: String) : Component {
        return Component.literal(text)
    }

}