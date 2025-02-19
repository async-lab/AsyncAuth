package club.asynclab.asyncraft.asyncauth.network.packet;

import club.asynclab.asyncraft.asyncauth.constant.ClientTextConstants;
import club.asynclab.asyncraft.asyncauth.util.ByteBufUtils;
import club.asynclab.asyncraft.asyncauth.util.ClientMessageUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class ClientMessagePacket {

    private ClientTextConstants text;

    private List<String> extendArgs;

    public static void encode(ClientMessagePacket packet, FriendlyByteBuf byteBuf) {
        ByteBufUtils.writeObject(byteBuf,packet.text);
        ByteBufUtils.writeObject(byteBuf,packet.extendArgs);
    }

    @SneakyThrows
    public static ClientMessagePacket decode(FriendlyByteBuf byteBuf) {
        ClientMessagePacket packet = new ClientMessagePacket();
        packet.text = (ClientTextConstants) ByteBufUtils.readObject(byteBuf);
        packet.extendArgs = (List<String>) ByteBufUtils.readObject(byteBuf);
        return packet;
    }

    public static void handle(ClientMessagePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> {
                ClientMessageUtils.displayTranslatableMessage(packet.text.getKey(),packet.extendArgs);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
