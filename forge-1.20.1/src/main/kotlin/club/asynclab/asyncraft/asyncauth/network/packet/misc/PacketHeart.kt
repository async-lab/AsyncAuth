package club.asynclab.asyncraft.asyncauth.network.packet.misc

import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketHeart(private val confirmed: Boolean = false) {
    companion object {
        fun encode(packet: PacketHeart, byteBuf: FriendlyByteBuf) {
            byteBuf.writeBoolean(packet.confirmed)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketHeart(byteBuf.readBoolean())
        fun handle(packet: PacketHeart, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                if (!packet.confirmed) NetworkHandler.LOGIN.reply(PacketHeart(true), ctx.get())
            }

            ctx.get().packetHandled = true
        }
    }
}