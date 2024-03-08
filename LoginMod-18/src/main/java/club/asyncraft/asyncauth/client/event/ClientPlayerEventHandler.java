package club.asyncraft.asyncauth.client.event;

import club.asyncraft.asyncauth.AsyncAuth;
import club.asyncraft.asyncauth.client.ClientModContext;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsyncAuth.MOD_ID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ClientPlayerEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientModContext.enable = false;
        ClientModContext.hasLogin = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClick(InputEvent.ClickInputEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!hasLogin()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerScroll(InputEvent.MouseScrollEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!hasLogin()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerMouseActive(InputEvent.RawMouseEvent event) {
        if (!isEnabled()) {
            return;
        }
        if (!hasLogin()) {
            event.setCanceled(true);
        }
    }

    private static boolean hasLogin() {
        return ClientModContext.hasLogin;
    }

    private static boolean isEnabled() {
        return ClientModContext.enable;
    }
}
