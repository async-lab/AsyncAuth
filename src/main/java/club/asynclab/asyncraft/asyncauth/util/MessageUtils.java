package club.asynclab.asyncraft.asyncauth.util;

import com.mojang.brigadier.LiteralMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;

public class MessageUtils {

    public static void sendMessage(Player player, String msg) {
        player.sendSystemMessage(ComponentUtils.fromMessage(new LiteralMessage(resolveMessage(msg))));
    }

    public static String getTranslatedMessage(String key) {
        return Component.translatable(key).getString();
    }

    private static String resolveMessage(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            return msg.replace("&","§");
        } else return "";
    }

}
