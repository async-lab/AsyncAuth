package club.asyncraft.asyncauth.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class CommonPacketManager {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel clientInitializeChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("login_mod", "player_check"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel loginRequestChannel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("login_mod", "player_login"),
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
    }
}
