package club.asynclab.asyncraft.asyncauth.network.packet

import club.asynclab.asyncraft.asyncauth.common.enumeration.LoginResultStatus
import club.asynclab.asyncraft.asyncauth.common.network.Attributes
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * 登录注册请求数据包，服务器接收
 */
class PacketAuth(
    private val username: String,
    private val password: String,
) {

    companion object {
        fun encode(packet: PacketAuth, byteBuf: FriendlyByteBuf) {
            byteBuf.writeUtf(packet.username)
            byteBuf.writeUtf(packet.password)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketAuth(byteBuf.readUtf(), byteBuf.readUtf())
        fun handle(packet: PacketAuth, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {

                val manager = ModContext.Server.MANAGER_AUTH
                var status = LoginResultStatus.SUCCESS
                if (manager.isExists(packet.username)) {
                    if (!manager.login(packet.username, packet.password)) {
                        status = LoginResultStatus.WRONG_PASSWORD
                    }
                } else if (!manager.register(packet.username, packet.password)) {
                    status = LoginResultStatus.TOO_SHORT
                }

                ctx.get().attr(Attributes.LOGGED).set(status == LoginResultStatus.SUCCESS)

                NetworkHandler.LOGIN.reply(PacketResponse(status), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}