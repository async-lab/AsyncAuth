package club.asyncraft.asyncauth.common.util;

import club.asyncraft.asyncauth.server.util.i18n.TranslationContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class MessageUtils {

    public static void sendMessageOnServer(PlayerEntity player, String msg) {
        player.sendMessage(new StringTextComponent(msg.replace("&","§")), Util.NIL_UUID);
    }

    public static void sendConfigMessageOnServer(PlayerEntity player,String path) {
        player.sendMessage(new StringTextComponent(TranslationContext.translate(path)),Util.NIL_UUID);
    }

    public static void sendMessageOnClient(String msg) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(msg.replace("&","§")),Util.NIL_UUID);
    }

    public static boolean isLoginPrefix(String prefix) {
        for (String s : CommandTabSupport.LOGIN_COMMANDS) {
            if (s.equalsIgnoreCase(prefix))
                return true;
        }
        return false;
    }

    public static boolean isRegisterPrefix(String prefix) {
        for (String s : CommandTabSupport.REGISTER_COMMANDS) {
            if (s.equalsIgnoreCase(prefix))
                return true;
        }
        return false;
    }

}
