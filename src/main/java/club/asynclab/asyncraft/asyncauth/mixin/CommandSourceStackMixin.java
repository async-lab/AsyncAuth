package club.asynclab.asyncraft.asyncauth.mixin;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.AuthManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin {

    @Inject(
            method = "hasPermission",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onCheckPermission(int level, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = ((CommandSourceStack)(Object)this).getEntity();
        if (entity instanceof ServerPlayer player) {
            if (AsyncAuth.isEnabled && !AuthManager.isVerified(player)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

}
