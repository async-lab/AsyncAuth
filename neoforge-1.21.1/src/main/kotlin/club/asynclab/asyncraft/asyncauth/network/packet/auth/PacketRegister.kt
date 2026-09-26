package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketRegister(
    private val username: String,
    private val password: String,
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketRegister> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketRegister> =
            CustomPacketPayload.Type(NetworkHandler.channelId("register"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketRegister> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketRegister) {
            buf.writeUtf(payload.username)
            buf.writeUtf(payload.password)
        }

        private fun decode(buf: FriendlyByteBuf) = PacketRegister(buf.readUtf(), buf.readUtf())

        fun handle(packet: PacketRegister, ctx: IPayloadContext) {
            ctx.enqueueWork {
                // 与 PacketLogin 保持一致：代理（Velocity 现代化转发）下发的用户名与客户端提交的不一致时拒绝
                val listener = ctx.listener() as? ServerConfigurationPacketListenerImpl
                val enforcedName = listener?.owner?.name
                if (enforcedName != null && !enforcedName.equals(packet.username, ignoreCase = true)) {
                    ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(false)
                    ctx.reply(
                        PacketResponse(
                            status = AuthStatus.WRONG_PASSWORD,
                            finish = false,
                            token = null,
                            expiresAt = 0L,
                            autoLogin = false
                        )
                    )
                    return@enqueueWork
                }

                val resolvedName = enforcedName ?: packet.username
                val status = ModContext.Server.MANAGER_AUTH.register(resolvedName, packet.password)
                val authenticated = status == AuthStatus.SUCCESS

                ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).set(authenticated)
                // 注册成功等价于登录成功：必须同时下发凭证并要求客户端回复 PacketFinish，
                // 否则本次配置阶段永远不会结束，连接会一直挂在服务器与代理上（会话泄漏）。
                val tokenData = if (authenticated) ManagerTokenServer.issueToken(resolvedName) else null
                ctx.reply(
                    PacketResponse(
                        status = status,
                        finish = authenticated,
                        token = tokenData?.token,
                        expiresAt = tokenData?.expiresAt ?: 0L,
                        autoLogin = false
                    )
                )
            }
        }
    }
}
