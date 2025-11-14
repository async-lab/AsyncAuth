package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * 登录注册请求数据包，服务器接收
 */
class PacketLogin(
    private val username: String,
    private val password: String,
) {

    companion object {
        fun encode(packet: PacketLogin, byteBuf: FriendlyByteBuf) {
            byteBuf.writeUtf(packet.username)
            byteBuf.writeUtf(packet.password)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketLogin(byteBuf.readUtf(), byteBuf.readUtf())
        fun handle(packet: PacketLogin, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                val enforcedName = ctx.get().sender?.name?.string
                if (enforcedName != null && !enforcedName.equals(packet.username, ignoreCase = true)) {
                    ctx.get().attr(NettyAttrKeys.AUTHENTICATED).set(false)
                    NetworkHandler.LOGIN.reply(
                        PacketResponse(
                            status = AuthStatus.WRONG_PASSWORD,
                            finish = false,
                            token = null,
                            expiresAt = 0L,
                            autoLogin = false
                        ),
                        ctx.get()
                    )
                    return@enqueueWork
                }

                val resolvedName = enforcedName ?: packet.username
                val status = ModContext.Server.MANAGER_AUTH.login(resolvedName, packet.password)
                ctx.get().attr(NettyAttrKeys.AUTHENTICATED).set(status == AuthStatus.SUCCESS)
                val tokenData = if (status == AuthStatus.SUCCESS) ManagerTokenServer.issueToken(resolvedName) else null
                NetworkHandler.LOGIN.reply(
                    PacketResponse(
                        status = status,
                        finish = status == AuthStatus.SUCCESS,
                        token = tokenData?.token,
                        expiresAt = tokenData?.expiresAt ?: 0L,
                        autoLogin = false
                    ),
                    ctx.get()
                )
            }

            ctx.get().packetHandled = true
        }

    }
}
