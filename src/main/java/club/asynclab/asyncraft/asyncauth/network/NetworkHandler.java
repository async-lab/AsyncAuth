package club.asynclab.asyncraft.asyncauth.network;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientInitializePacket;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientQuitPacket;
import club.asynclab.asyncraft.asyncauth.network.packet.LoginRequestPacket;
import club.asynclab.asyncraft.asyncauth.network.packet.LoginResponsePacket;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkHandler {
    private static final String PROTOCOL = "1.0";
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(AsyncAuth.MODID, "main"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals
        );
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(ID++, ClientInitializePacket.class,
                ClientInitializePacket::encode,
                ClientInitializePacket::decode,
                ClientInitializePacket::handle
        );
        INSTANCE.registerMessage(ID++, LoginRequestPacket.class,
                LoginRequestPacket::encode,
                LoginRequestPacket::decode,
                LoginRequestPacket::handle
        );

        INSTANCE.registerMessage(ID++, LoginResponsePacket.class,
                LoginResponsePacket::encode,
                LoginResponsePacket::decode,
                LoginResponsePacket::handle
        );

        INSTANCE.registerMessage(ID++, ClientQuitPacket.class,
                (msg,buf) -> {},
                buf -> new ClientQuitPacket(),
                (msg,ctx) -> {
                    ServerPlayer player = ctx.get().getSender();
                    if (player != null)
                        player.connection.disconnect(new TextComponent("UnAuthorized"));
                }
        );
    }

}
