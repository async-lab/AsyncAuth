package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.server.util.i18n.TranslationContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestPacketMessage {

    private String password;

    public static void encode(LoginRequestPacketMessage message, FriendlyByteBuf byteBuf) {
        ByteBufUtils.writeString(byteBuf,message.getPassword());
    }

    public static LoginRequestPacketMessage decode(FriendlyByteBuf byteBuf) {
        LoginRequestPacketMessage message = new LoginRequestPacketMessage();
        message.password = ByteBufUtils.readString(byteBuf);
        return message;
    }

    public static void serverReceivePacketHandle(LoginRequestPacketMessage message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            player.getServer().executeIfPossible(() -> {

                if (!SqlUtils.isRegistered(player)){
                    MessageUtils.sendConfigMessageOnServer(player,"login.un_registered");
                    return;
                }
                if (SqlUtils.checkPassword(player, message.getPassword())) {
                    //TODO 处理登录请求(执行登录命令)
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
