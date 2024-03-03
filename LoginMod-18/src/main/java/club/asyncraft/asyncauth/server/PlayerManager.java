package club.asyncraft.asyncauth.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerManager {

    private static final Set<String> loginPlayers = new HashSet<>();

    public static boolean loginPlayer(ServerPlayer player) {
        String uuid = player.getStringUUID();
        if (loginPlayers.contains(uuid)) {
            return false;
        } else {
            loginPlayers.add(uuid);
            return true;
        }
    }

    public static boolean logoutPlayer(ServerPlayer player) {
        String uuid = player.getStringUUID();
        if (!loginPlayers.contains(uuid)) {
            return false;
        } else {
            loginPlayers.remove(uuid);
            return true;
        }
    }

    public static boolean hasLogin(Player player) {
        return loginPlayers.contains(player.getStringUUID());
    }

}
