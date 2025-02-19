package club.asynclab.asyncraft.asyncauth.util;

import club.asynclab.asyncraft.asyncauth.constant.ClientTextConstants;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientMessagePacket;
import com.mojang.brigadier.LiteralMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.StringUtils;

public class MessageUtils {

    public static void sendMessage(Player player, String msg) {
        player.sendSystemMessage(ComponentUtils.fromMessage(new LiteralMessage(resolveMessage(msg))));
    }

    public static String getTranslatedMessage(String key) {
        return Component.translatable(key).getString();
    }

    public static void sendTranslatableMessage(ServerPlayer player, ClientTextConstants textConstants) {
        NetworkHandler.INSTANCE.sendTo(new ClientMessagePacket(textConstants),player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    private static String resolveMessage(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            return msg.replace("&","§");
        } else return "";
    }

}
