package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.gui.ScreenRegister
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketRegisterResponse(private val status: AuthStatus) {
    companion object {
        fun encode(packet: PacketRegisterResponse, byteBuf: FriendlyByteBuf) {
            byteBuf.writeEnum(packet.status)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketRegisterResponse(byteBuf.readEnum(AuthStatus::class.java))
        fun handle(packet: PacketRegisterResponse, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                val screen = Minecraft.getInstance().screen as? ScreenRegister ?: return@enqueueWork
                if (packet.status != AuthStatus.SUCCESS) {
                    UtilToast.toast(TranslatableComponent(packet.status.msgPath()))
                    return@enqueueWork
                }

                screen.onClose()
                UtilToast.toast(TranslatableComponent(AuthStatus.SUCCESS.msgPath()))
            }

            ctx.get().packetHandled = true
        }
    }
}