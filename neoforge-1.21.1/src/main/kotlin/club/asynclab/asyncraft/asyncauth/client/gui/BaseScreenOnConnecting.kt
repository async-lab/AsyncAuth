package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.client.ManagerClient
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class BaseScreenOnConnecting(
    title: Component,
    protected val deadline: Long,
) : Screen(title) {
    private var heartTicker = 0

    override fun tick() {
        super.tick()
        val mc = Minecraft.getInstance()
        if (mc.connection == null && mc.level == null) {
            return
        }
        if (mc.connection == null) {
            onClose()
            return
        }

        if (++heartTicker > 200) {
            ManagerClient.sendHeartbeat()
            heartTicker = 0
        }
    }
}
