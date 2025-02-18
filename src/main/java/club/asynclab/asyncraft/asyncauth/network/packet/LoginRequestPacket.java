package club.asynclab.asyncraft.asyncauth.network.packet;

import club.asynclab.asyncraft.asyncauth.AuthManager;
import club.asynclab.asyncraft.asyncauth.constant.LoginResultStatusCode;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.util.ByteBufUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 登录注册请求数据包，服务器接收
 */
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestPacket {

    private String password;

    public static void encode(LoginRequestPacket packet, FriendlyByteBuf byteBuf) {
        ByteBufUtils.writeString(byteBuf,packet.password);
    }

    public static LoginRequestPacket decode(FriendlyByteBuf byteBuf) {
        LoginRequestPacket packet = new LoginRequestPacket();
        packet.password = ByteBufUtils.readString(byteBuf);
        return packet;
    }

    public static void handle(LoginRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER,() -> () -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    if (AuthManager.hasRegistered(player)) {

                        NetworkHandler.INSTANCE.sendTo(new LoginResponsePacket(
                                AuthManager.auth(ctx.get().getSender(),packet.password) ? LoginResultStatusCode.LOGIN_SUCCESS : LoginResultStatusCode.WRONG_PASSWORD),
                                player.connection.connection,
                                NetworkDirection.PLAY_TO_CLIENT);
                    } else {
                        NetworkHandler.INSTANCE.sendTo(new LoginResponsePacket(AuthManager.register(ctx.get().getSender(),packet.password) ? LoginResultStatusCode.LOGIN_SUCCESS : LoginResultStatusCode.REG_PASS_SHORT),
                                player.connection.connection,
                                NetworkDirection.PLAY_TO_CLIENT);
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
