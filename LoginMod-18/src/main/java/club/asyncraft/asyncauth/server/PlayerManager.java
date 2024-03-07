package club.asyncraft.asyncauth.server;

import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private static final Set<String> unLoginPlayers = new HashSet<>();
    private static final Set<String> unRegisterPlayers = new HashSet<>();

    private static int count = 0;

    public static void sendLoginInfo(TickEvent.ServerTickEvent event) {
        if (unLoginPlayers.isEmpty() && unRegisterPlayers.isEmpty()) {
            count = 0;
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            count++;
            if (count >= MyModConfig.ticks.get()) {
                count = 0;
                MinecraftServer serverInstance = ServerModContext.serverInstance;
                serverInstance.executeIfPossible(() -> {
                    synchronized (PlayerManager.class) {
                        PlayerList playerList = serverInstance.getPlayerList();
                        unLoginPlayers.forEach(s -> {
                            ServerPlayer player = playerList.getPlayer(UUID.fromString(s));
                            if (player != null) {
                                MessageUtils.sendConfigMessageOnServer(player,"login.login_info");
                            }
                        });
                        unRegisterPlayers.forEach(s -> {
                            ServerPlayer player = playerList.getPlayer(UUID.fromString(s));
                            if (player != null) {
                                MessageUtils.sendConfigMessageOnServer(player,"login.register_info");
                            }
                        });
                    }
                });
            }
        }
    }

    public static void loginPlayer(ServerPlayer player) {
        String uuid = player.getStringUUID();
        unLoginPlayers.remove(uuid);
        unRegisterPlayers.remove(uuid);
    }

    public static void subscribeUnLoginPlayer(Player player) {
        String uuid = player.getStringUUID();
        if (SqlUtils.isRegistered(player)) {
            if (unLoginPlayers.contains(uuid)) {
                return;
            }
            unLoginPlayers.add(uuid);
        } else {
            if (unRegisterPlayers.contains(uuid)) {
                return;
            }
            unRegisterPlayers.add(uuid);
        }
    }

    public static void logoutPlayer(ServerPlayer player) {
        String uuid = player.getStringUUID();
        unLoginPlayers.remove(uuid);
        unRegisterPlayers.remove(uuid);
    }

    public static boolean hasLogin(Player player) {
        String uuid = player.getStringUUID();
        return !(unLoginPlayers.contains(uuid) || unRegisterPlayers.contains(uuid));
    }

}
