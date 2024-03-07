package club.asyncraft.asyncauth.client.event;

import club.asyncraft.asyncauth.client.ClientModContext;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

public class ClientPlayerEventHandler {

    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        ClientModContext.enable = false;
        ClientModContext.hasLogin = false;
    }
}
