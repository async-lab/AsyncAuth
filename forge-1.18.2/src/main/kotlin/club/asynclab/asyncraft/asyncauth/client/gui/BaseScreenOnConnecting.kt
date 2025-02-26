package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.PacketPing
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

open class BaseScreenOnConnecting(
    title: Component,
    protected val ctx: Supplier<NetworkEvent.Context>,
) : Screen(title) {
    private var heart = 0

    override fun tick() {
        super.tick()
        if (!ctx.get().networkManager.isConnected) {
            this.onClose()
        }

        if (heart++ > 100) {
            ctx.get().enqueueWork { NetworkHandler.LOGIN.reply(PacketPing(), ctx.get()) }
            heart = 0
        }
    }
}