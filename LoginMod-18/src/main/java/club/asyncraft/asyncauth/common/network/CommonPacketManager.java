package club.asyncraft.asyncauth.common.network;

import club.asyncraft.asyncauth.common.network.login.LoginRequestPacketMessage;
import club.asyncraft.asyncauth.common.network.login.LoginResponsePacketMessage;
import club.asyncraft.asyncauth.common.network.register.RegisterRequestPacketMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CommonPacketManager {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel clientInitializeChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("async_auth", "player_check"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel loginRequestChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("async_auth", "player_login_request"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel loginResponseChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("async_auth", "player_login_response"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel registerRequestChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("async_auth", "player_register_request"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int channelIndex = 1;

    public static void init() {

        clientInitializeChannel.registerMessage(channelIndex++,
                ClientInitializeMessage.class,
                ClientInitializeMessage::encode,
                ClientInitializeMessage::decode,
                ClientInitializeMessage::clientReceivedPacketHandle
        );

        loginRequestChannel.registerMessage(channelIndex++,
                LoginRequestPacketMessage.class,
                LoginRequestPacketMessage::encode,
                LoginRequestPacketMessage::decode,
                LoginRequestPacketMessage::serverReceivePacketHandle
        );

        loginResponseChannel.registerMessage(channelIndex++,
                LoginResponsePacketMessage.class,
                LoginResponsePacketMessage::encode,
                LoginResponsePacketMessage::decode,
                LoginResponsePacketMessage::clientReceivePacketHandle
        );

        registerRequestChannel.registerMessage(channelIndex++,
                RegisterRequestPacketMessage.class,
                RegisterRequestPacketMessage::encode,
                RegisterRequestPacketMessage::decode,
                RegisterRequestPacketMessage::serverReceivePacketHandle
        );
    }
}
