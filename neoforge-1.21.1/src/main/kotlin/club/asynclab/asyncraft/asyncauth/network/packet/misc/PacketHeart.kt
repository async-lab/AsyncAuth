package club.asynclab.asyncraft.asyncauth.network.packet.misc

import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketHeart(private val confirmed: Boolean = false) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketHeart> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketHeart> =
            CustomPacketPayload.Type(NetworkHandler.channelId("heart"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketHeart> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketHeart) {
            buf.writeBoolean(payload.confirmed)
        }

        private fun decode(buf: FriendlyByteBuf) = PacketHeart(buf.readBoolean())

        fun handle(packet: PacketHeart, ctx: IPayloadContext) {
            ctx.enqueueWork {
                if (ctx.flow() == PacketFlow.SERVERBOUND && !packet.confirmed) {
                    ctx.reply(PacketHeart(true))
                }
            }
        }
    }
}
