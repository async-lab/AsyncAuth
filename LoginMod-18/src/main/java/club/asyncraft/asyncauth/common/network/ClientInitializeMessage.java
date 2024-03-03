package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.client.ClientModContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientInitializeMessage {

    private Boolean enable;

    public static void encode(ClientInitializeMessage message, FriendlyByteBuf byteBuf) {
        byteBuf.writeBoolean(message.getEnable());
    }

    public static ClientInitializeMessage decode(FriendlyByteBuf byteBuf) {
        ClientInitializeMessage message = new ClientInitializeMessage();
        message.enable = byteBuf.readBoolean();
        return message;
    }


    public static void clientReceivedPacketHandle(ClientInitializeMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> ClientInitializeHandler.clientReceivedPacketHandle(message,ctx));
        });
        ctx.get().setPacketHandled(true);
    }

    public static class ClientInitializeHandler {

        public static void clientReceivedPacketHandle(ClientInitializeMessage message, Supplier<NetworkEvent.Context> ctx) {
            ClientModContext.enable = message.enable;
        }

    }

}
