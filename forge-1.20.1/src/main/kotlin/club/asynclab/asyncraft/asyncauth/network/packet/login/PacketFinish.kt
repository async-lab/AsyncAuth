package club.asynclab.asyncraft.asyncauth.network.packet.login

import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketFinish : BasePacketLogin() {
    companion object {
        fun encode(packet: PacketFinish, byteBuf: FriendlyByteBuf) {}
        fun decode(byteBuf: FriendlyByteBuf) = PacketFinish()
        fun handle(packet: PacketFinish, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                if (ctx.get().attr(NettyAttrKeys.AUTHENTICATED).get() != true) {
                    ctx.get().networkManager.disconnect(UtilComponent.getTranslatableComponent(Lang.Msg.UNAUTHORIZED))
                }
            }

            ctx.get().packetHandled = true
        }
    }
}