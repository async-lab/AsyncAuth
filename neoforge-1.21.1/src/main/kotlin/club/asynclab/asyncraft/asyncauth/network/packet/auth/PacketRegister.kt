package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketRegister(
    private val username: String,
    private val password: String,
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketRegister> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketRegister> =
            CustomPacketPayload.Type(NetworkHandler.channelId("register"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketRegister> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketRegister) {
            buf.writeUtf(payload.username)
            buf.writeUtf(payload.password)
        }

        private fun decode(buf: FriendlyByteBuf) = PacketRegister(buf.readUtf(), buf.readUtf())

        fun handle(packet: PacketRegister, ctx: IPayloadContext) {
            ctx.enqueueWork {
                val status = ModContext.Server.MANAGER_AUTH.register(packet.username, packet.password)
                ctx.reply(PacketResponse(status, false))
            }
        }
    }
}
