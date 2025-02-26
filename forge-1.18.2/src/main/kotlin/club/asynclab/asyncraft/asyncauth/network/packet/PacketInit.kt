package club.asynclab.asyncraft.asyncauth.network.packet

import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * 确认用户是否注册，客户端接收
 */
class PacketInit(
//
) : BasePacketLogin() {
    companion object {
        fun encode(packet: PacketInit, byteBuf: FriendlyByteBuf) {}
        fun decode(byteBuf: FriendlyByteBuf) = PacketInit()
        fun handle(packet: PacketInit, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                Minecraft.getInstance().pushGuiLayer(ScreenLogin(ctx))
            }

            ctx.get().packetHandled = true
        }
    }
}