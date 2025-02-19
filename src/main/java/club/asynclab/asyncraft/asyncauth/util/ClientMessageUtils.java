package club.asynclab.asyncraft.asyncauth.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientMessageUtils {

    public static void displayTranslatableMessage(String key, List<String> extendArgs) {
        String msg = Component.translatable(key).getString();

        for (String arg : extendArgs) {
            msg = msg.replaceFirst("%a",arg);
        }

        Minecraft.getInstance().player.displayClientMessage(Component.literal(msg),false);
    }

}
