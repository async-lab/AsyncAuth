package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.client.ManagerClient
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

open class BaseScreenOnConnecting(
    title: Component,
    protected val ctx: Supplier<NetworkEvent.Context>,
    protected val deadline: Long,
) : Screen(title) {
    private var heart = 0

    init {
        ManagerClient.attachContext(ctx)
    }

    override fun tick() {
        super.tick()
        if (!ctx.get().networkManager.isConnected) {
            this.onClose()
        }
        if (heart++ > 200) {
            ManagerClient.sendHeartbeat()
            heart = 0
        }
    }
}
