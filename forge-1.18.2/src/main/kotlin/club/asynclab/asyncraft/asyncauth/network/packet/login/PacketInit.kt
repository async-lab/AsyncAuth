package club.asynclab.asyncraft.asyncauth.network.packet.login

import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.time.Instant
import java.util.function.Supplier

class PacketInit(
    private val timeout: Int,
) : BasePacketLogin() {
    companion object {
        fun encode(packet: PacketInit, byteBuf: FriendlyByteBuf) {
            byteBuf.writeInt(packet.timeout)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketInit(byteBuf.readInt())
        fun handle(packet: PacketInit, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                Minecraft.getInstance().pushGuiLayer(ScreenLogin(ctx, Instant.now().epochSecond + packet.timeout))
            }
            ctx.get().packetHandled = true
        }
    }
}