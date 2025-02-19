package club.asynclab.asyncraft.asyncauth.network.packet;

import club.asynclab.asyncraft.asyncauth.constant.ClientTextConstants;
import club.asynclab.asyncraft.asyncauth.util.ByteBufUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@AllArgsConstructor
@NoArgsConstructor
public class ClientMessagePacket {

    private ClientTextConstants text;

    public static void encode(ClientMessagePacket packet, FriendlyByteBuf byteBuf) {
        ByteBufUtils.writeObject(byteBuf,packet.text);
    }

    @SneakyThrows
    public static ClientMessagePacket decode(FriendlyByteBuf byteBuf) {
        ClientMessagePacket packet = new ClientMessagePacket();
        packet.text = (ClientTextConstants) ByteBufUtils.readObject(byteBuf);
        return packet;
    }

    public static void handle(ClientMessagePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable(packet.text.getKey()),false);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
