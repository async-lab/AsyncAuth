package club.asyncraft.asyncauth.server.event;

import club.asyncraft.asyncauth.common.network.ClientInitializeMessage;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.network.NetworkDirection;

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
        PlayerEntity player = event.getPlayer();
        CommonPacketManager.clientInitializeChannel.sendTo(new ClientInitializeMessage(SqlUtils.isRegistered(player)), ((ServerPlayerEntity) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }


}
