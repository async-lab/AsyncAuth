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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.apache.commons.lang3.StringUtils;

@Mod.EventBusSubscriber(modid = "async_auth", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
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
        PlayerManager.subscribeUnLoginPlayer(player);
        CommonPacketManager.clientInitializeChannel.sendTo(new ClientInitializeMessage(true, TranslationContext.clientMessage), ((ServerPlayer) player).connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerManager.logoutPlayer((ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerMove(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!PlayerManager.hasLogin(player)) {
            player.teleportTo(player.xOld, player.yOld, player.zOld);
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        String prefix = StringUtils.split(event.getMessage())[0];
        if (StringUtils.isEmpty(prefix)) {
            return;
        }
        if (MessageUtils.isRegisterPrefix(prefix) || MessageUtils.isLoginPrefix(prefix)) {
            return;
        }
        ServerPlayer player = event.getPlayer();
        verify(player, event, true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = ((Player) event.getEntity());
            verify(player, event, false);
        }
    }

    /*@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerCommand(CommandEvent event) {
        Entity entity = event.getParseResults().getContext().getSource().getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            verify(player,event,true);
        }
    }*/

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            verify(player, event, false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerBreak(BlockEvent.BreakEvent event) {
        verify(event.getPlayer(), event, true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        verify(event.getPlayer(), event, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        verify(event.getPlayer(), event, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickEmpty event) {
        verify(event.getPlayer(), event, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickItem event) {
        verify(event.getPlayer(), event, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        verify(event.getPlayer(), event, false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDamaged(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            verify(((Player) entity),event,false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrop(ItemTossEvent event) {
        ServerPlayer player = ((ServerPlayer) event.getPlayer());
        if (!PlayerManager.hasLogin(player)) {
            ItemStack item = event.getEntityItem().getItem();
            player.getInventory().add(item);
            event.setCanceled(true);
        }
    }

    private static void verify(Player player, Event event, boolean sendMsg) {
        if (!PlayerManager.hasLogin(player)) {
            event.setCanceled(true);
            if (sendMsg) {
                sendUnLoginMessage(player);
            }
        }
    }

    private static void sendUnLoginMessage(Player player) {
        MessageUtils.sendConfigMessageOnServer(player, "login.un_login_info");
    }

}
