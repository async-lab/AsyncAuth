package club.asyncraft.asyncauth.server.event;

import club.asyncraft.asyncauth.common.network.ClientInitializeMessage;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkDirection;

public class PlayerEventHandler {

/*
    public static void onPlayerLoginClientSide(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.getServer() != null) {
            return;
        }
    }
*/

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        CommonPacketManager.clientInitializeChannel.sendTo(new ClientInitializeMessage(SqlUtils.isRegistered(player)), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerManager.logoutPlayer((ServerPlayer) event.getPlayer());
    }

    public static void onPlayerMove(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        if (!PlayerManager.hasLogin(player)) {
            player.teleportTo(player.xOld,player.yOld,player.zOld);
        }
    }

}
