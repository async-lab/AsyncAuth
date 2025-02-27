package club.asynclab.asyncraft.asyncauth.network.packet.heart

import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketPong {
    companion object {
        fun encode(packet: PacketPong, byteBuf: FriendlyByteBuf) {}
        fun decode(byteBuf: FriendlyByteBuf) = PacketPong()
        fun handle(packet: PacketPong, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().packetHandled = true
        }
    }
}