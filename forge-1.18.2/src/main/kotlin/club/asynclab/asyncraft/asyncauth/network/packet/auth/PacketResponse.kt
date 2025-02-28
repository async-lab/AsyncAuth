package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketResponse(private val status: AuthStatus, private val finish: Boolean) {
    companion object {
        fun encode(packet: PacketResponse, byteBuf: FriendlyByteBuf) {
            byteBuf.writeEnum(packet.status)
            byteBuf.writeBoolean(packet.finish)
        }

        fun decode(byteBuf: FriendlyByteBuf) =
            PacketResponse(byteBuf.readEnum(AuthStatus::class.java), byteBuf.readBoolean())

        fun handle(packet: PacketResponse, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                UtilToast.toast(TranslatableComponent(Lang.Auth.from(packet.status)))
                if (packet.status == AuthStatus.SUCCESS) Minecraft.getInstance().screen?.onClose()
                if (packet.finish) NetworkHandler.LOGIN.reply(PacketFinish(), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}