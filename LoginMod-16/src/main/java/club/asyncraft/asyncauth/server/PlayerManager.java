package club.asyncraft.asyncauth.server;

import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.common.network.login.LoginResponsePacketMessage;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.NetworkDirection;

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
                serverInstance.execute(() -> {
                    synchronized (PlayerManager.class) {
                        PlayerList playerList = serverInstance.getPlayerList();
                        unLoginPlayers.forEach(s -> {
                            ServerPlayerEntity player = playerList.getPlayer(UUID.fromString(s));
                            if (player != null) {
                                MessageUtils.sendConfigMessageOnServer(player,"login.login_info");
                            }
                        });
                        unRegisterPlayers.forEach(s -> {
                            ServerPlayerEntity player = playerList.getPlayer(UUID.fromString(s));
                            if (player != null) {
                                MessageUtils.sendConfigMessageOnServer(player,"register.register_info");
                            }
                        });
                    }
                });
            }
        }
    }

    public static void loginPlayer(ServerPlayerEntity player) {
        String uuid = player.getStringUUID();
        unLoginPlayers.remove(uuid);
        unRegisterPlayers.remove(uuid);
        CommonPacketManager.loginResponseChannel.sendTo(new LoginResponsePacketMessage(true),player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        setPlayerSpeed(.1,player);
    }

    public static void subscribeUnLoginPlayer(ServerPlayerEntity player) {
        setPlayerSpeed(0,player);
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

    public static void logoutPlayer(ServerPlayerEntity player) {
        String uuid = player.getStringUUID();
        unLoginPlayers.remove(uuid);
        unRegisterPlayers.remove(uuid);
    }

    public static boolean hasLogin(PlayerEntity player) {
        String uuid = player.getStringUUID();
        return !(unLoginPlayers.contains(uuid) || unRegisterPlayers.contains(uuid));
    }

    private static void setPlayerSpeed(double value,PlayerEntity player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(value);
    }

}
