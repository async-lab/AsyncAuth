package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.common.util.ByteBufUtils;
import club.asyncraft.asyncauth.common.util.MessageUtils;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
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
            if (!SqlUtils.isRegistered(player)){
                MessageUtils.sendMessage(player,MessageUtils.convertMessage(MyModConfig.unRegisteredMsg.get()));
                return;
            }
            if (SqlUtils.checkPassword(player.getName().getString(), message.getPassword())) {
                MinecraftServer server = ctx.get().getSender().getServer();
                CommandSource source = server.createCommandSourceStack();
                server.getCommands().performCommand(source,"/authme forcelogin " + player.getName().getString());
            } else {
                MessageUtils.sendMessage(player,MessageUtils.convertMessage(MyModConfig.passwordIncorrect.get()));
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
