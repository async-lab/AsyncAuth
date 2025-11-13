package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.misc.PacketHeart
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

open class BaseScreenOnConnecting(
    title: Component,
    protected val deadline: Long,
) : Screen(title) {
    private var heartTicker = 0

    override fun tick() {
        super.tick()
        val connection = Minecraft.getInstance().connection?.connection
        if (connection == null || !connection.isConnected) {
            onClose()
            return
        }

        if (++heartTicker > 200) {
            NetworkHandler.sendToServer(PacketHeart())
            heartTicker = 0
        }
    }
}
