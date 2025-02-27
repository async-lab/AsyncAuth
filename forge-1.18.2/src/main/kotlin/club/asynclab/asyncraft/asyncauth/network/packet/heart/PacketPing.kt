package club.asynclab.asyncraft.asyncauth.network.packet.heart

import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketPing {
    companion object {
        fun encode(packet: PacketPing, byteBuf: FriendlyByteBuf) {}
        fun decode(byteBuf: FriendlyByteBuf) = PacketPing()
        fun handle(packet: PacketPing, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                NetworkHandler.LOGIN.reply(PacketPong(), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}