package club.asyncraft.asyncauth.common.util;

import club.asyncraft.asyncauth.server.config.MyModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class MessageUtils {

    public static void sendMessage(PlayerEntity player, String msg) {
        player.sendMessage(new StringTextComponent(msg.replace("&","§")),Util.NIL_UUID);
    }

    public static void sendMessageOnClient(String msg) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(msg.replace("&","§")),Util.NIL_UUID);
    }

    public static String convertMessage(String msg) {
        return MyModConfig.prefix.get().replace("&","§") + "§r" + msg.replace("&","§");
    }

}
