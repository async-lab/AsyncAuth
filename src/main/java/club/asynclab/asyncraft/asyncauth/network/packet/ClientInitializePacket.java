package club.asynclab.asyncraft.asyncauth.network.packet;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.ClientContext;
import club.asynclab.asyncraft.asyncauth.util.GuiUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 确认用户是否注册，客户端接收
 */
@AllArgsConstructor
@NoArgsConstructor
public class ClientInitializePacket {

    private Boolean isRegistered;

    @SneakyThrows
    public static void encode(ClientInitializePacket packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeBoolean(packet.isRegistered);
    }

    @SneakyThrows
    public static ClientInitializePacket decode(FriendlyByteBuf byteBuf) {
        ClientInitializePacket packet = new ClientInitializePacket();
        packet.isRegistered = byteBuf.readBoolean();
        return packet;
    }

    public static void handle(ClientInitializePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> {
                AsyncAuth.isEnabled = true;
                ClientContext.markUnverified(packet.isRegistered);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
