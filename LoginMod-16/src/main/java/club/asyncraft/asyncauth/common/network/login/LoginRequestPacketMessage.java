package club.asyncraft.asyncauth.common.network.login;

import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestPacketMessage {

    private String password;

    public static void encode(LoginRequestPacketMessage message, ByteBuf byteBuf) {
        ByteBufUtils.writeString(byteBuf,message.getPassword());
    }

    public static LoginRequestPacketMessage decode(ByteBuf byteBuf) {
        LoginRequestPacketMessage message = new LoginRequestPacketMessage();
        message.password = ByteBufUtils.readString(byteBuf);
        return message;
    }

    public static void serverReceivePacketHandle(LoginRequestPacketMessage message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            player.getServer().execute(() -> {

                if (!SqlUtils.isRegistered(player)){
                    MessageUtils.sendConfigMessageOnServer(player,"login.un_registered");
                    return;
                }
                if (SqlUtils.checkPassword(player, message.getPassword())) {
                    /*MinecraftServer server = ctx.get().getSender().getServer();
                    CommandSourceStack source = server.createCommandSourceStack();
                    server.getCommands().performCommand(source,"/authme forcelogin " + player.getName().getString());*/
                    PlayerManager.loginPlayer(player);
                    MessageUtils.sendConfigMessageOnServer(player,"login.success");
                } else {
                    MessageUtils.sendConfigMessageOnServer(player,"login.password_incorrect");
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
