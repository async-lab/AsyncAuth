package club.asynclab.asyncraft.asyncauth.util

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

fun CustomPacketPayload.toConfigurationPacket(): ServerboundCustomPayloadPacket {
    return ServerboundCustomPayloadPacket(this)
}
