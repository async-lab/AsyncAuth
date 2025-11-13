package club.asynclab.asyncraft.asyncauth.network

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegister
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketResponse
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketInit
import club.asynclab.asyncraft.asyncauth.network.packet.misc.PacketHeart
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.HandlerThread

object NetworkHandler {
    private const val VERSION = "1.0"

    val AUTH_TASK_TYPE: ConfigurationTask.Type =
        ConfigurationTask.Type(AsyncAuth.resourceLocation("authentication"))

    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        if (!ModSetting.enabled.get()) return
        val registrar = event.registrar(VERSION)
            .optional()
            .versioned(VERSION)
            .executesOn(HandlerThread.MAIN)

        registrar.commonToClient(PacketInit.TYPE, PacketInit.STREAM_CODEC, PacketInit::handle)
        registrar.commonToClient(PacketResponse.TYPE, PacketResponse.STREAM_CODEC, PacketResponse::handle)
        registrar.commonBidirectional(PacketHeart.TYPE, PacketHeart.STREAM_CODEC, PacketHeart::handle)

        registrar.commonToServer(PacketLogin.TYPE, PacketLogin.STREAM_CODEC, PacketLogin::handle)
        registrar.commonToServer(PacketRegister.TYPE, PacketRegister.STREAM_CODEC, PacketRegister::handle)
        registrar.commonToServer(PacketFinish.TYPE, PacketFinish.STREAM_CODEC, PacketFinish::handle)
    }

    fun registerConfigurationTasks(event: RegisterConfigurationTasksEvent) {
        if (!ModSetting.enabled.get()) return
        event.register(AuthConfigurationTask(event.listener, ModSetting.timeout.get()))
    }

    fun sendToServer(payload: CustomPacketPayload) {
        PacketDistributor.sendToServer(payload)
    }

    fun sendToPlayer(player: ServerPlayer, payload: CustomPacketPayload) {
        PacketDistributor.sendToPlayer(player, payload)
    }

    fun channelId(path: String): ResourceLocation = AsyncAuth.resourceLocation(path)
}
