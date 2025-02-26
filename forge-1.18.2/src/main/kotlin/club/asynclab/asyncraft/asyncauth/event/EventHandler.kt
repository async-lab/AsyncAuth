package club.asynclab.asyncraft.asyncauth.event

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.registry.ModCommands
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent


class EventHandler {
    @EventBusSubscriber(modid = BuiltConstantsCommon.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
    object ForgeEventBoth {}

    @EventBusSubscriber(
        modid = BuiltConstantsCommon.MOD_ID,
        bus = EventBusSubscriber.Bus.FORGE,
        value = [Dist.DEDICATED_SERVER]
    )
    object ForgeEventServer {
        @SubscribeEvent
        fun onServerAboutToStart(event: ServerAboutToStartEvent) {
            ModSetting.onServerAboutToStart(event)
            ModContext.Server.onServerAboutToStart(event)
        }

        @SubscribeEvent
        fun onRegisterCommands(event: RegisterCommandsEvent) {
            ModCommands.onRegisterCommands(event)
        }
    }

    @EventBusSubscriber(modid = BuiltConstantsCommon.MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
    object ForgeEventClient {}

    @EventBusSubscriber(modid = BuiltConstantsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    object ModEventBoth {
        @SubscribeEvent
        fun onCommonSetup(event: FMLCommonSetupEvent) {
            NetworkHandler.onCommonSetup(event)
        }
    }

    @EventBusSubscriber(
        modid = BuiltConstantsCommon.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = [Dist.DEDICATED_SERVER]
    )
    object ModEventServer {}

    @EventBusSubscriber(modid = BuiltConstantsCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ModEventClient {}
}