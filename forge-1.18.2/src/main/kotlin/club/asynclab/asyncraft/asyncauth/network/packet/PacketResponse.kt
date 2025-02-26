package club.asynclab.asyncraft.asyncauth.network.packet

import club.asynclab.asyncraft.asyncauth.common.enumeration.LoginResultStatus
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class PacketResponse(private val status: LoginResultStatus) {
    companion object {
        fun encode(packet: PacketResponse, byteBuf: FriendlyByteBuf) {
            byteBuf.writeEnum(packet.status)
        }

        fun decode(byteBuf: FriendlyByteBuf) = PacketResponse(byteBuf.readEnum(LoginResultStatus::class.java))
        fun handle(packet: PacketResponse, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork {
                if (packet.status != LoginResultStatus.SUCCESS) {
                    ctx.get().networkManager.disconnect(TranslatableComponent(packet.status.msgPath()))
                } else {
                    NetworkHandler.LOGIN.reply(PacketFinish(), ctx.get())
                }
            }

            ctx.get().packetHandled = true
        }
    }
}