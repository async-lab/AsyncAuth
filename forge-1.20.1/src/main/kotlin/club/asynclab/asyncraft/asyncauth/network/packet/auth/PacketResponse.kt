package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.client.ManagerClient
import club.asynclab.asyncraft.asyncauth.client.gui.BaseScreenOnConnecting
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketResponse(
    private val status: AuthStatus,
    private val finish: Boolean,
    private val token: String?,
    private val expiresAt: Long,
    private val autoLogin: Boolean
) {
    companion object {
        fun encode(packet: PacketResponse, byteBuf: FriendlyByteBuf) {
            byteBuf.writeEnum(packet.status)
            byteBuf.writeBoolean(packet.finish)
            byteBuf.writeBoolean(packet.token != null)
            if (packet.token != null) {
                byteBuf.writeUtf(packet.token)
                byteBuf.writeLong(packet.expiresAt)
            }
            byteBuf.writeBoolean(packet.autoLogin)
        }

        fun decode(byteBuf: FriendlyByteBuf): PacketResponse {
            val status = byteBuf.readEnum(AuthStatus::class.java)
            val finish = byteBuf.readBoolean()
            val hasToken = byteBuf.readBoolean()
            val token = if (hasToken) byteBuf.readUtf() else null
            val expiresAt = if (hasToken) byteBuf.readLong() else 0L
            val autoLogin = byteBuf.readBoolean()
            return PacketResponse(status, finish, token, expiresAt, autoLogin)
        }

        fun handle(packet: PacketResponse, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                UtilToast.toast(UtilComponent.getTranslatableComponent(Lang.Auth.from(packet.status)))
                val screen = Minecraft.getInstance().screen
                if (packet.status == AuthStatus.SUCCESS && screen is BaseScreenOnConnecting) {
                    screen.onClose()
                }
                ManagerClient.handleResponse(packet.status, packet.token, packet.expiresAt, packet.autoLogin)
                if (packet.finish) NetworkHandler.LOGIN.reply(PacketFinish(), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}
