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
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketResponse(
    private val status: AuthStatus,
    private val finish: Boolean,
    private val token: String? = null,
    private val expiresAt: Long = 0L,
    private val autoLogin: Boolean = false
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketResponse> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketResponse> =
            CustomPacketPayload.Type(NetworkHandler.channelId("response"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketResponse> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketResponse) {
            buf.writeEnum(payload.status)
            buf.writeBoolean(payload.finish)
            buf.writeBoolean(payload.token != null)
            if (payload.token != null) {
                buf.writeUtf(payload.token)
                buf.writeLong(payload.expiresAt)
            }
            buf.writeBoolean(payload.autoLogin)
        }

        private fun decode(buf: FriendlyByteBuf): PacketResponse {
            val status = buf.readEnum(AuthStatus::class.java)
            val finish = buf.readBoolean()
            val hasToken = buf.readBoolean()
            val token = if (hasToken) buf.readUtf() else null
            val expiresAt = if (hasToken) buf.readLong() else 0L
            val autoLogin = buf.readBoolean()
            return PacketResponse(status, finish, token, expiresAt, autoLogin)
        }

        fun handle(packet: PacketResponse, ctx: IPayloadContext) {
            ctx.enqueueWork {
                UtilToast.toast(UtilComponent.getTranslatableComponent(Lang.Auth.from(packet.status)))
                if (packet.status == AuthStatus.SUCCESS) {
                    val screen = Minecraft.getInstance().screen
                    if (screen is BaseScreenOnConnecting) {
                        screen.onClose()
                    }
                }
                ManagerClient.handleResponse(packet.status, packet.token, packet.expiresAt, packet.autoLogin)
                if (packet.finish) {
                    ctx.reply(PacketFinish())
                }
            }
        }
    }
}
