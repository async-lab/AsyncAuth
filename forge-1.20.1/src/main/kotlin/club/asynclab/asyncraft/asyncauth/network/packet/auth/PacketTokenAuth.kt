package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.TokenValidationStatus
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketTokenAuth(private val username: String, private val token: String) {
    companion object {
        fun encode(packet: PacketTokenAuth, buf: FriendlyByteBuf) {
            buf.writeUtf(packet.username)
            buf.writeUtf(packet.token)
        }

        fun decode(buf: FriendlyByteBuf) = PacketTokenAuth(buf.readUtf(), buf.readUtf())

        fun handle(packet: PacketTokenAuth, ctxSupplier: Supplier<NetworkEvent.Context>) {
            val ctx = ctxSupplier.get()
            ctx.enqueueWork {
                val enforcedName = ctx.sender?.name?.string
                if (enforcedName != null && !enforcedName.equals(packet.username, ignoreCase = true)) {
                    ctx.attr(NettyAttrKeys.AUTHENTICATED).set(false)
                    NetworkHandler.LOGIN.reply(
                        PacketResponse(
                            status = AuthStatus.TOKEN_INVALID,
                            finish = false,
                            token = null,
                            expiresAt = 0L,
                            autoLogin = true
                        ),
                        ctx
                    )
                    return@enqueueWork
                }

                val resolvedName = enforcedName ?: packet.username
                val validation = ManagerTokenServer.validate(resolvedName, packet.token)
                when (validation) {
                    TokenValidationStatus.VALID -> {
                        ctx.attr(NettyAttrKeys.AUTHENTICATED).set(true)
                        NetworkHandler.LOGIN.reply(
                            PacketResponse(
                                status = AuthStatus.SUCCESS,
                                finish = true,
                                token = null,
                                expiresAt = 0L,
                                autoLogin = true
                            ),
                            ctx
                        )
                    }
                    TokenValidationStatus.EXPIRED,
                    TokenValidationStatus.INVALID -> {
                        ctx.attr(NettyAttrKeys.AUTHENTICATED).set(false)
                        NetworkHandler.LOGIN.reply(
                            PacketResponse(
                                status = AuthStatus.TOKEN_INVALID,
                                finish = false,
                                token = null,
                                expiresAt = 0L,
                                autoLogin = true
                            ),
                            ctx
                        )
                    }
                }
            }
            ctx.packetHandled = true
        }

    }
}
