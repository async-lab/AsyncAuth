package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketLoginResponse(private val status: AuthStatus) {
    companion object {
        fun encode(packet: PacketLoginResponse, byteBuf: FriendlyByteBuf) {
            byteBuf.writeEnum(packet.status)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketLoginResponse(byteBuf.readEnum(AuthStatus::class.java))
        fun handle(packet: PacketLoginResponse, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                val screen = Minecraft.getInstance().screen as? ScreenLogin ?: return@enqueueWork
                if (packet.status != AuthStatus.SUCCESS) {
                    UtilToast.toast(TranslatableComponent(packet.status.msgPath()))
                    return@enqueueWork
                }

                screen.onClose()
                NetworkHandler.LOGIN.reply(PacketFinish(), ctx.get())
                UtilToast.toast(TranslatableComponent(AuthStatus.SUCCESS.msgPath()))
            }

            ctx.get().packetHandled = true
        }
    }
}