package club.asynclab.asyncraft.asyncauth.network.packet

import club.asynclab.asyncraft.asyncauth.common.network.Attributes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketFinish : BasePacketLogin() {
    companion object {
        fun encode(packet: PacketFinish, byteBuf: FriendlyByteBuf) {}
        fun decode(byteBuf: FriendlyByteBuf) = PacketFinish()
        fun handle(packet: PacketFinish, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                if (!ctx.get().attr(Attributes.LOGGED).get()) {
                    ctx.get().networkManager.disconnect(TranslatableComponent("msg.asyncauth.unauthorized"))
                }
            }
            ctx.get().packetHandled = true
        }
    }
}