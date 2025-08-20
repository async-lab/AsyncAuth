package club.asynclab.asyncraft.asyncauth.mixin;

import club.asynclab.asyncraft.asyncauth.util.UtilComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl {
    @Shadow
    private int tick;

    @Shadow
    public static int MAX_TICKS_BEFORE_LOGIN = 600;

    @Shadow
    public abstract void disconnect(Component reason);

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginPacketListenerImpl;tick:I", opcode = Opcodes.GETFIELD), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (MAX_TICKS_BEFORE_LOGIN != 0 && this.tick++ == MAX_TICKS_BEFORE_LOGIN) {
            this.disconnect(UtilComponent.getTranslatableComponent("multiplayer.disconnect.slow_login"));
        }
        ci.cancel();
    }
}
