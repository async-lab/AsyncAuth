package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.TokenValidationStatus
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketTokenAuth(private val username: String, private val token: String) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<PacketTokenAuth> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketTokenAuth> =
            CustomPacketPayload.Type(NetworkHandler.channelId("token_auth"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketTokenAuth> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketTokenAuth) {
            buf.writeUtf(payload.username)
            buf.writeUtf(payload.token)
        }

        private fun decode(buf: FriendlyByteBuf) =
            PacketTokenAuth(buf.readUtf(), buf.readUtf())

        fun handle(packet: PacketTokenAuth, ctx: IPayloadContext) {
            ctx.enqueueWork {
                val listener = (ctx.listener() as? ServerConfigurationPacketListenerImpl)
                val enforcedName = listener?.owner?.name
                if (enforcedName != null && !enforcedName.equals(packet.username, ignoreCase = true)) {
                    ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(false)
                    ctx.reply(
                        PacketResponse(
                            status = AuthStatus.TOKEN_INVALID,
                            finish = false,
                            token = null,
                            expiresAt = 0L,
                            autoLogin = true
                        )
                    )
                    return@enqueueWork
                }

                val resolvedName = enforcedName ?: packet.username
                val validation = ManagerTokenServer.validate(resolvedName, packet.token)
                when (validation) {
                    TokenValidationStatus.VALID -> {
                        ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(true)
                        ctx.reply(
                            PacketResponse(
                                status = AuthStatus.SUCCESS,
                                finish = true,
                                token = null,
                                expiresAt = 0L,
                                autoLogin = true
                            )
                        )
                    }

                    TokenValidationStatus.EXPIRED,
                    TokenValidationStatus.INVALID -> {
                        ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(false)
                        ctx.reply(
                            PacketResponse(
                                status = AuthStatus.TOKEN_INVALID,
                                finish = false,
                                token = null,
                                expiresAt = 0L,
                                autoLogin = true
                            )
                        )
                    }
                }
            }
        }

    }
}
