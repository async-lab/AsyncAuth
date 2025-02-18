package club.asynclab.asyncraft.asyncauth.util;

import com.mojang.brigadier.LiteralMessage;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;

public class MessageUtils {

    public static void sendMessage(Player player, String msg) {
        player.sendMessage(ComponentUtils.fromMessage(new LiteralMessage(resolveMessage(msg))),player.getUUID());
    }

    public static String getTranslatedMessage(String key) {
        return new TranslatableComponent(key).getString();
    }

    private static String resolveMessage(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            return msg.replace("&","§");
        } else return "";
    }

}
