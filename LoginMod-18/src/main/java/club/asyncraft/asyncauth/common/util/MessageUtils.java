package club.asyncraft.asyncauth.common.util;

import club.asyncraft.asyncauth.server.util.i18n.TranslationContext;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class MessageUtils {

    public static void sendMessageOnServer(Player player, String msg) {
        player.sendMessage(new TextComponent(msg.replace("&","§")), Util.NIL_UUID);
    }

    public static void sendConfigMessageOnServer(Player player,String path) {
        player.sendMessage(new TextComponent(TranslationContext.translate(path)),Util.NIL_UUID);
    }

    public static void sendMessageOnClient(String msg) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(msg.replace("&","§")),Util.NIL_UUID);
    }

    public static boolean isLoginPrefix(String prefix) {
        String[] info = new String[]{"/login","/l"};
        for (String s : info) {
            if (s.equalsIgnoreCase(prefix))
                return true;
        }
        return false;
    }

    public static boolean isRegisterPrefix(String prefix) {
        String[] info = new String[]{"/register","/reg"};
        for (String s : info) {
            if (s.equalsIgnoreCase(prefix))
                return true;
        }
        return false;
    }

}
