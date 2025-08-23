package club.asynclab.asyncraft.asyncauth.network.packet.auth

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
                val status = ModContext.Server.MANAGER_AUTH.register(packet.username, packet.password)
                NetworkHandler.LOGIN.reply(PacketResponse(status, false), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}