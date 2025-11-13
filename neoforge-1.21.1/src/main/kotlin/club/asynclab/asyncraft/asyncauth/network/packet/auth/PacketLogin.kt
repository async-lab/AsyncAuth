package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

/**
 * 登录注册请求数据包，服务器接收
 */
class PacketLogin(
    private val username: String,
    private val password: String,
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketLogin> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketLogin> =
            CustomPacketPayload.Type(NetworkHandler.channelId("login"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketLogin> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketLogin) {
            buf.writeUtf(payload.username)
            buf.writeUtf(payload.password)
        }

        private fun decode(buf: FriendlyByteBuf) = PacketLogin(buf.readUtf(), buf.readUtf())

        fun handle(packet: PacketLogin, ctx: IPayloadContext) {
            ctx.enqueueWork {
                val status = ModContext.Server.MANAGER_AUTH.login(packet.username, packet.password)
                ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(status == AuthStatus.SUCCESS)
                ctx.reply(PacketResponse(status, status == AuthStatus.SUCCESS))
            }
        }
    }
}
