package club.asynclab.asyncraft.asyncauth.network.packet.login

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.client.ManagerClient
import club.asynclab.asyncraft.asyncauth.client.gui.ScreenAutoLogin
import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.time.Instant

class PacketInit(
    private val timeout: Int,
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketInit> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketInit> =
            CustomPacketPayload.Type(NetworkHandler.channelId("init"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketInit> =
            StreamCodec.of(::encode, ::decode)

        private fun encode(buf: FriendlyByteBuf, payload: PacketInit) {
            buf.writeInt(payload.timeout)
        }

        private fun decode(buf: FriendlyByteBuf) = PacketInit(buf.readInt())

        fun handle(packet: PacketInit, ctx: IPayloadContext) {
            ctx.enqueueWork {

                val configConnection = ctx.connection()    // this is the
                ManagerClient.attachConnection(configConnection)

                val deadline = Instant.now().epochSecond + packet.timeout
                if (ManagerClient.tryAutoLogin(deadline)) {
                    Minecraft.getInstance().setScreen(ScreenAutoLogin(deadline))
                } else {
                    Minecraft.getInstance().setScreen(ScreenLogin(deadline))
                }

            }

        }
    }
}
