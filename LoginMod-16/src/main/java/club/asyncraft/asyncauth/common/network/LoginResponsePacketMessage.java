package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.client.ClientModContext;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponsePacketMessage {

    private Boolean isLoginSuccess;

    public static void encode(LoginResponsePacketMessage message, ByteBuf byteBuf) {
        byteBuf.writeBoolean(message.getIsLoginSuccess());
    }

    public static LoginResponsePacketMessage decode(ByteBuf byteBuf) {
        LoginResponsePacketMessage message = new LoginResponsePacketMessage();
        message.isLoginSuccess = byteBuf.readBoolean();
        return message;
    }

    public static void clientReceivePacketHandle(LoginResponsePacketMessage message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> ClientPacketHandler.handle(message,ctx));
        });
        ctx.get().setPacketHandled(true);
    }

    public static class ClientPacketHandler {
        public static void handle(LoginResponsePacketMessage message, Supplier<NetworkEvent.Context> ctx) {
            ClientModContext.hasLogin = message.getIsLoginSuccess();
        }
    }

}
