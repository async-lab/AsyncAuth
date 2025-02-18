package club.asynclab.asyncraft.asyncauth.event.server;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.AuthManager;
import club.asynclab.asyncraft.asyncauth.DatabaseManager;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientInitializePacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

import java.awt.*;

@Mod.EventBusSubscriber(modid = AsyncAuth.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.DEDICATED_SERVER)
public class PlayerEventHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!DatabaseManager.checkPlayerName(player.getName().getString())) {
            player.connection.disconnect(Component.literal("Please choose a different player name.\n请更换一个游戏名称"));
            return;
        }
        NetworkHandler.INSTANCE.sendTo(new ClientInitializePacket(AuthManager.hasRegistered(player)),player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        AuthManager.playerJoin(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        AuthManager.markUnverified(player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = ((ServerPlayer) event.getEntity());
            verify(player, event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            verify(player, event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerBreak(BlockEvent.BreakEvent event) {
        verify(event.getPlayer(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        verify(event.getEntity(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDamaged(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            verify(((Player) entity),event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrop(ItemTossEvent event) {
        ServerPlayer player = ((ServerPlayer) event.getPlayer());
        if (!AuthManager.isVerified(player)) {
            ItemStack item = event.getEntity().getItem();
            player.getInventory().add(item);
            event.setCanceled(true);
        }
    }

    private static void verify(Player player, Event event) {
        if (!AuthManager.isVerified((ServerPlayer) player)) {
            event.setCanceled(true);
        }
    }

}
