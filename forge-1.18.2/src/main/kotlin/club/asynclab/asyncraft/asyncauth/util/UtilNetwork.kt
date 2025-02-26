package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.Connection
import net.minecraft.network.chat.TranslatableComponent

object UtilNetwork {
    fun Connection.disconnect() = this.disconnect(TranslatableComponent("multiplayer.disconnect.generic"))
}