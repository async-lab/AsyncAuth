package club.asyncraft.asyncauth.client.event;

import club.asyncraft.asyncauth.common.network.ClientMessageDTO;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.client.ClientModContext;
import club.asyncraft.asyncauth.common.network.login.LoginRequestPacketMessage;
import club.asyncraft.asyncauth.common.network.register.RegisterRequestPacketMessage;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
        ClientMessageDTO message = ClientModContext.message;
        if (MessageUtils.isLoginPrefix(args[0])) {
            event.setCanceled(true);
            if (ClientModContext.hasLogin) {
                MessageUtils.sendMessageOnClient(message.getAlreadyLoginMsg());
                return;
            }
            if (args.length != 2) {
                MessageUtils.sendMessageOnClient(message.getLoginCommandUsage().replace("{cmd}",args[0]));
                return;
            }
            CommonPacketManager.loginRequestChannel.sendToServer(new LoginRequestPacketMessage(args[1]));
        } else if (MessageUtils.isRegisterPrefix(args[0])) {
            event.setCanceled(true);
            if (ClientModContext.hasLogin) {
                MessageUtils.sendMessageOnClient(message.getAlreadyLoginMsg());
                return;
            }
            if (args.length != 3) {
                MessageUtils.sendMessageOnClient(message.getRegisterCommandUsage().replace("{cmd}",args[0]));
                return;
            }
            if (!args[1].equals(args[2])) {
                MessageUtils.sendMessageOnClient(message.getPasswordUnconformity());
                return;
            }
            if (args[1].length() < message.getMinLength()) {
                MessageUtils.sendMessageOnClient(message.getPasswordTooShort().replace("{min}",String.valueOf(message.getMinLength())));
                return;
            }
            CommonPacketManager.registerRequestChannel.sendToServer(new RegisterRequestPacketMessage(args[1]));
        }
    }

}
