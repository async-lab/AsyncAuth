package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.client.ClientModContext;
import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientInitializeMessage {

    private Boolean enable;

    private ClientMessageDTO clientMessage;

    @SneakyThrows
    public static void encode(ClientInitializeMessage message, ByteBuf byteBuf) {
        byteBuf.writeBoolean(message.getEnable());
        ByteBufUtils.writeObject(byteBuf,message.getClientMessage());
    }

    @SneakyThrows
    public static ClientInitializeMessage decode(ByteBuf byteBuf) {
        ClientInitializeMessage message = new ClientInitializeMessage();
        message.enable = byteBuf.readBoolean();
        message.clientMessage = (ClientMessageDTO) ByteBufUtils.readObject(byteBuf);
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
            ClientModContext.message = message.getClientMessage();
        }

    }

}
