package club.asynclab.asyncraft.asyncauth;

import club.asynclab.asyncraft.asyncauth.constant.ClientTextConstants;
import club.asynclab.asyncraft.asyncauth.util.ServerMessageUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {

    private static final Set<UUID> verifiedPlayers = ConcurrentHashMap.newKeySet();

    public static void playerJoin(ServerPlayer player) {
        player.setInvisible(true);
        player.setInvulnerable(true);
    }

    public static void markVerified(ServerPlayer player) {
        player.setInvisible(false);
        player.setInvulnerable(false);
        verifiedPlayers.add(player.getUUID());
    }

    public static boolean isVerified(ServerPlayer player) {
        return FMLEnvironment.dist == Dist.CLIENT ? ClientContext.isLoggedIn : verifiedPlayers.contains(player.getUUID());
    }

    public static void markUnverified(ServerPlayer player) {
        verifiedPlayers.remove(player.getUUID());
    }

    public static boolean hasRegistered(ServerPlayer player) {
        return DatabaseManager.isRegistered(player);
    }

    public static boolean changePassword(ServerPlayer player,String password) {
        return DatabaseManager.changePassword(player,password);
    }

    public static boolean auth(ServerPlayer player,String password) {
        boolean result = checkPassword(player,password);
        if (result) markVerified(player);
        return result;
    }

    /**
     * 检查密码是否正确
     */
    public static boolean checkPassword(ServerPlayer player,String password) {
        return DatabaseManager.checkPassword(player,password);
    }

    /**
     * 校验密码是否符合规定
     */
    public static boolean verifyPasswordText(ServerPlayer serverPlayer,String password) {
        int minLength = ModConfig.minLength.get();
        Optional<ServerPlayer> optionalPlayer = Optional.ofNullable(serverPlayer);
        if (password.contains(" ")) {
            optionalPlayer.ifPresent(player ->
                ServerMessageUtils.sendTranslatableMessage(player,ClientTextConstants.PASSWORD_CONTAINS_SPACE, List.of())
            );
        }
        if (password.length() < minLength) {
            optionalPlayer.ifPresent(player ->
                    ServerMessageUtils.sendTranslatableMessage(player, ClientTextConstants.Change_PASSWORD_Too_Short, List.of(String.valueOf(minLength)))
            );
            return false;
        }
        return true;
    }

    public static boolean register(ServerPlayer player,String password) {
        if (password.length() < ModConfig.minLength.get()) return false;
        boolean result = DatabaseManager.register(player,password);
        if (result) markVerified(player);
        return result;
    }

}
