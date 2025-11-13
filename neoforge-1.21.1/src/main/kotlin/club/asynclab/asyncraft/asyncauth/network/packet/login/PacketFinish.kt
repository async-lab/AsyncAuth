package club.asynclab.asyncraft.asyncauth.network.packet.login

import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class PacketFinish : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<PacketFinish> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<PacketFinish> =
            CustomPacketPayload.Type(NetworkHandler.channelId("finish"))

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PacketFinish> =
            StreamCodec.of({ _, _ -> }, { PacketFinish() })

        fun handle(packet: PacketFinish, ctx: IPayloadContext) {
            ctx.enqueueWork {
                val authenticated = ctx.connection().channel().attr(NettyAttrKeys.AUTHENTICATED).get() == true
                if (!authenticated) {
                    ctx.disconnect(UtilComponent.getTranslatableComponent(Lang.Msg.UNAUTHORIZED))
                } else {
                    ctx.finishCurrentTask(NetworkHandler.AUTH_TASK_TYPE)
                }
            }
        }
    }
}
