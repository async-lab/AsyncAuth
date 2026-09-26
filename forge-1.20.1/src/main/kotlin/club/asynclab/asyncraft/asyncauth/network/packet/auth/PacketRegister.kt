package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketRegister(
    private val username: String,
    private val password: String,
) {
    companion object {
        fun encode(packet: PacketRegister, byteBuf: FriendlyByteBuf) {
            byteBuf.writeUtf(packet.username)
            byteBuf.writeUtf(packet.password)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketRegister(byteBuf.readUtf(), byteBuf.readUtf())
        fun handle(packet: PacketRegister, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                // 与 PacketLogin 保持一致：服务端认证过的用户名与客户端提交的不一致时拒绝
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

                val status = ModContext.Server.MANAGER_AUTH.register(resolvedName, packet.password)
                val authenticated = status == AuthStatus.SUCCESS

                ctx.get().attr(NettyAttrKeys.AUTHENTICATED).set(authenticated)
                // 注册成功等价于登录成功：必须同时下发凭证并要求客户端回复 PacketFinish，
                // 否则登录握手永远不会结束，连接会一直挂着直到被超时踢掉。
                val tokenData = if (authenticated) ManagerTokenServer.issueToken(resolvedName) else null
                NetworkHandler.LOGIN.reply(
                    PacketResponse(
                        status = status,
                        finish = authenticated,
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
