package club.asynclab.asyncraft.asyncauth.event.client;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.ClientContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsyncAuth.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientPlayerEventHandler {

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ClientContext.logout();
    }

}
