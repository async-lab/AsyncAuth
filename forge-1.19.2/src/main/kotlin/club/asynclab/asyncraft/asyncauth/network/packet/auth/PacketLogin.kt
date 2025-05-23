package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
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
                val status = ModContext.Server.MANAGER_AUTH.login(packet.username, packet.password)
                ctx.get().attr(NettyAttrKeys.AUTHENTICATED).set(status == AuthStatus.SUCCESS)
                NetworkHandler.LOGIN.reply(PacketResponse(status, status == AuthStatus.SUCCESS), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}