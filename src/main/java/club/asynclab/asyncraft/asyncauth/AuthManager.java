package club.asynclab.asyncraft.asyncauth;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.system.CallbackI;

import javax.xml.crypto.Data;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {

    private static final Set<UUID> verifiedPlayers = ConcurrentHashMap.newKeySet();

    public static void markVerified(ServerPlayer player) {
        verifiedPlayers.add(player.getUUID());

//        MessageUtils.sendMessage(player,"&a" + MessageUtils.getTranslatedMessage("msg.asyncauth.login_success"));
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

    public static boolean auth(ServerPlayer player,String password) {
        boolean result = DatabaseManager.checkPassword(player,password);
        if (result) markVerified(player);
        return result;
    }

    public static boolean register(ServerPlayer player,String password) {
        if (password.length() < ModConfig.minLength.get()) return false;
        boolean result = DatabaseManager.register(player,password);
        if (result) markVerified(player);
        return result;
    }
}
