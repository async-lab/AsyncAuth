package club.asynclab.asyncraft.asyncauth.mixin;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.AuthManager;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerMixin {

    @Shadow public ServerPlayer player;

    // 拦截移动数据包
    @Inject(
            method = "handleMovePlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPlayerMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
            ci.cancel();
            player.teleportTo(player.getX(),player.getY(),player.getZ());
        }
    }

    // 拦截方块操作
    @Inject(
            method = "handleUseItemOn",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onBlockInteract(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
            ci.cancel();
        }
    }

    // 拦截聊天消息
    @Inject(
            method = "handleChat*",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onChatMessage(ServerboundChatPacket packet, CallbackInfo ci) {
        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
            ci.cancel();
        }
    }

    // 拦截物品交互
    @Inject(
            method = "handleUseItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onUseItem(ServerboundUseItemPacket packet, CallbackInfo ci) {
        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
            ci.cancel();
        }
    }

    // 拦截交互动作
    @Inject(
            method = "handleInteract",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "handleContainerClick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onInventoryMove(ServerboundContainerClickPacket packet, CallbackInfo ci) {

        if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) ci.cancel();

        /*// 拦截操作物品栏
        if (packet.getContainerId() == 0 && !AuthManager.isVerified(player)) {
            ci.cancel();

            // 强制同步客户端物品栏
            player.inventoryMenu.broadcastChanges();
        } else if (packet.getButtonNum() == 1 && packet.getClickType() == ClickType.THROW) { // 拦截丢弃物品
            if (!AuthManager.isVerified(player)) {
                ci.cancel();
            }
        }*/
    }



}
