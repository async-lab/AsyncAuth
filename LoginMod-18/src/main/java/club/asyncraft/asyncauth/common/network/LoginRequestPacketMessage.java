package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
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
            if (!SqlUtils.isRegistered(player)){
                MessageUtils.sendMessage(player,MessageUtils.convertMessage(MyModConfig.unRegisteredMsg.get()));
                return;
            }
            if (SqlUtils.checkPassword(player.getName().getString(), message.getPassword())) {
                //TODO 处理登录请求(执行登录命令)
                MinecraftServer server = ctx.get().getSender().getServer();
                CommandSourceStack source = server.createCommandSourceStack();
                server.getCommands().performCommand(source,"/authme forcelogin " + player.getName().getString());
            } else {
                MessageUtils.sendMessage(player,MessageUtils.convertMessage(MyModConfig.passwordIncorrect.get()));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
