package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.Connection

object UtilNetwork {
    fun Connection.disconnect() = this.disconnect(UtilComponent.getTranslatableComponent("multiplayer.disconnect.generic"))
}