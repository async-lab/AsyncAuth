package club.asynclab.asyncraft.asyncauth.network.packet.auth

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketResponse(private val status: AuthStatus, private val finish: Boolean) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketResponse> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketResponse> =
            CustomPacketPayload.Type(NetworkHandler.channelId("response"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketResponse> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketResponse) {
            buf.writeEnum(payload.status)
            buf.writeBoolean(payload.finish)
        }

        private fun decode(buf: FriendlyByteBuf) =
            PacketResponse(buf.readEnum(AuthStatus::class.java), buf.readBoolean())

        fun handle(packet: PacketResponse, ctx: IPayloadContext) {
            ctx.enqueueWork {
                UtilToast.toast(UtilComponent.getTranslatableComponent(Lang.Auth.from(packet.status)))
                if (packet.status == AuthStatus.SUCCESS) {
                    Minecraft.getInstance().screen?.onClose()
                }
                if (packet.finish) {
                    NetworkHandler.sendToServer(PacketFinish())
                }
            }
        }
    }
}
