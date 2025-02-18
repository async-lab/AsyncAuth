package club.asynclab.asyncraft.asyncauth.network.packet;

import club.asynclab.asyncraft.asyncauth.ClientContext;
import club.asynclab.asyncraft.asyncauth.ModConfig;
import club.asynclab.asyncraft.asyncauth.constant.LoginResultStatusCode;
import club.asynclab.asyncraft.asyncauth.gui.LoginScreen;
import club.asynclab.asyncraft.asyncauth.gui.RegisterScreen;
import club.asynclab.asyncraft.asyncauth.util.ByteBufUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 登录响应数据包，客户端接收
 */
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponsePacket {

    private int loginResultCode;

    private Map<String,String> extentArgs;

    @SneakyThrows
    public static void encode(LoginResponsePacket packet, FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(packet.loginResultCode);
        ByteBufUtils.writeObject(byteBuf,packet.extentArgs);
    }

    @SneakyThrows
    public static LoginResponsePacket decode(FriendlyByteBuf byteBuf) {
        LoginResponsePacket packet = new LoginResponsePacket();
        packet.loginResultCode = byteBuf.readInt();
        packet.extentArgs = (Map<String, String>) ByteBufUtils.readObject(byteBuf);
        return packet;
    }

    public static void handle(LoginResponsePacket packet, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> () -> {

                Screen screen = ClientContext.loginScreen;
                if (screen != null) {
                    if (screen instanceof LoginScreen loginScreen) {

                        if (packet.loginResultCode == LoginResultStatusCode.WRONG_PASSWORD) {
                            loginScreen.displayWrongPasswordInfo();
                        } else if (packet.loginResultCode == LoginResultStatusCode.LOGIN_SUCCESS){
                            ClientContext.markVerified();
                        }

                    } else if (screen instanceof RegisterScreen registerScreen) {

                        if (packet.loginResultCode == LoginResultStatusCode.REG_PASS_SHORT) {
                            registerScreen.displayPasswordTooShortMessage(packet.extentArgs.get("minlength"));
                        } else if (packet.loginResultCode == LoginResultStatusCode.LOGIN_SUCCESS){
                            ClientContext.markVerified();
                        }

                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
