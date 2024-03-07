package club.asyncraft.asyncauth.common.network.register;

import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestPacketMessage {

    private String password;

    public static void encode(RegisterRequestPacketMessage message, FriendlyByteBuf byteBuf) {
        ByteBufUtils.writeString(byteBuf,message.getPassword());
    }

    public static RegisterRequestPacketMessage decode(FriendlyByteBuf byteBuf) {
        RegisterRequestPacketMessage message = new RegisterRequestPacketMessage();
        message.password = ByteBufUtils.readString(byteBuf);
        return message;
    }

    public static void serverReceivePacketHandle(RegisterRequestPacketMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            player.getServer().executeIfPossible(() -> {
                if (SqlUtils.isRegistered(player)) {
                    MessageUtils.sendConfigMessageOnServer(player,"register.already_register");
                    return;
                }
                SqlUtils.register(player,message.getPassword());
                PlayerManager.loginPlayer(player);
                MessageUtils.sendConfigMessageOnServer(player,"register.success");

            });
        });
        ctx.get().setPacketHandled(true);
    }

}
