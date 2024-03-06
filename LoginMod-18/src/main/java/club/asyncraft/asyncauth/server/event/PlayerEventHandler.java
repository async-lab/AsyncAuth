package club.asyncraft.asyncauth.server.event;

import club.asyncraft.asyncauth.common.network.ClientInitializeMessage;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.server.util.i18n.TranslationContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(modid = "${mod_id}",bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.DEDICATED_SERVER)
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

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getPlayer();
        CommonPacketManager.clientInitializeChannel.sendTo(new ClientInitializeMessage(SqlUtils.isRegistered(player), TranslationContext.clientMessage), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerManager.logoutPlayer((ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerMove(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        if (!PlayerManager.hasLogin(player)) {
            player.teleportTo(player.xOld,player.yOld,player.zOld);
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!PlayerManager.hasLogin(player)) {
            event.setCanceled(true);
            MessageUtils.sendConfigMessageOnServer(player,"login.un_login_info");
        }
    }

    @SubscribeEvent
    public static void onPlayerBreak(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            if (!PlayerManager.hasLogin(player)) {
                event.setCanceled(true);
            }
        }
    }
    //TODO 放置方块 破坏方块 交互物品  输入命令

}
