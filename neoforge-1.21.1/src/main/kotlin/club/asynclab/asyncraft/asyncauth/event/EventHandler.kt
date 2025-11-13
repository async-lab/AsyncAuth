package club.asynclab.asyncraft.asyncauth.event

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.registry.ModCommands
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent


class EventHandler {
    @EventBusSubscriber(
        modid = BuiltConstantsCommon.MOD_ID,
        value = [Dist.DEDICATED_SERVER]
    )
    object ForgeEventServer {
        @SubscribeEvent
        fun onServerAboutToStart(event: ServerAboutToStartEvent) {
            ModSetting.onServerAboutToStart(event)
            ModContext.onServerAboutToStart(event)
        }

        @SubscribeEvent
        fun onRegisterCommands(event: RegisterCommandsEvent) {
            ModCommands.onRegisterCommands(event)
        }
    }

    @EventBusSubscriber(modid = BuiltConstantsCommon.MOD_ID)
    object ModEventBoth {
        @SubscribeEvent
        fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
            NetworkHandler.registerPayloads(event)
        }

        @SubscribeEvent
        fun onRegisterConfigurationTasks(event: RegisterConfigurationTasksEvent) {
            NetworkHandler.registerConfigurationTasks(event)
        }
    }
}
