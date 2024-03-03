package club.asyncraft.asyncauth.client.event;

import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.client.ClientModContext;
import club.asyncraft.asyncauth.common.network.LoginRequestPacketMessage;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import net.minecraftforge.client.event.ClientChatEvent;
import org.apache.commons.lang3.StringUtils;

public class LoginCommandHandler {

    public static void onPlayerChat(ClientChatEvent event) {
        if (!ClientModContext.enable) {
            return;
        }
        String msg = event.getMessage().trim();
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        String[] args = StringUtils.split(msg);
/*        if (msg.startsWith("/login")) {
            event.setCanceled(true);
            return;
        }*/
        if (isLoginPrefix(args[0])) {
            event.setCanceled(true);
            if (ClientModContext.hasLogin) {
                MessageUtils.sendMessageOnClient(MessageUtils.convertMessage(MyModConfig.alreadyLogin.get()));
                return;
            }
            if (args.length != 2) {
                MessageUtils.sendMessageOnClient(MessageUtils.convertMessage(MyModConfig.loginCommandUsage.get().replace("{cmd}",args[0])));
                return;
            }
            CommonPacketManager.loginRequestChannel.sendToServer(new LoginRequestPacketMessage(args[1]));
        }
    }

    private static boolean isLoginPrefix(String prefix) {
        String[] info = new String[]{"/login","/l"};
        for (String s : info) {
            if (s.equalsIgnoreCase(prefix))
                return true;
        }
        return false;
    }

}
