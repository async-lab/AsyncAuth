package club.asyncraft.asyncauth.common.util;

import club.asyncraft.asyncauth.server.config.MyModConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class MessageUtils {

    public static void sendMessage(Player player,String msg) {
        player.sendMessage(new TextComponent(msg.replace("&","§")), Util.NIL_UUID);
    }

    public static void sendMessageOnClient(String msg) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(msg.replace("&","§")),Util.NIL_UUID);
    }

    public static String convertMessage(String msg) {
        return MyModConfig.prefix.get().replace("&","§") + "§r" + msg.replace("&","§");
    }

}
