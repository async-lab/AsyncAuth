package club.asynclab.asyncraft.asyncauth.network

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.network.packet.*
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.network.HandshakeHandler
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel


object NetworkHandler {
    private const val VERSION = "1.0"
    private var id = 0

    val LOGIN: SimpleChannel = NetworkRegistry.newSimpleChannel(
        AsyncAuth.resourceLocation("login"),
        { VERSION },
        VERSION::equals,
        VERSION::equals
    )

    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGIN.messageBuilder(PacketInit::class.java, id++)
            .loginIndex(BasePacketLogin::loginIndex, BasePacketLogin::loginIndex::set)
            .encoder(PacketInit::encode)
            .decoder(PacketInit::decode)
            .consumer(PacketInit::handle)
            .buildLoginPacketList { local ->
                if (local) emptyList() else listOf(org.apache.commons.lang3.tuple.Pair.of("init", PacketInit()))
            }
            .add()
        LOGIN.messageBuilder(PacketAuth::class.java, id++)
            .encoder(PacketAuth::encode)
            .decoder(PacketAuth::decode)
            .consumer(PacketAuth::handle)
            .add()
        LOGIN.messageBuilder(PacketResponse::class.java, id++)
            .encoder(PacketResponse::encode)
            .decoder(PacketResponse::decode)
            .consumer(PacketResponse::handle)
            .add()
        LOGIN.messageBuilder(PacketPing::class.java, id++)
            .encoder(PacketPing::encode)
            .decoder(PacketPing::decode)
            .consumer(PacketPing::handle)
            .add()
        LOGIN.messageBuilder(PacketPong::class.java, id++)
            .encoder(PacketPong::encode)
            .decoder(PacketPong::decode)
            .consumer(PacketPong::handle)
            .add()
        LOGIN.messageBuilder(PacketFinish::class.java, id++)
            .loginIndex(BasePacketLogin::loginIndex, BasePacketLogin::loginIndex::set)
            .encoder(PacketFinish::encode)
            .decoder(PacketFinish::decode)
            .consumer(HandshakeHandler.indexFirst { _, packet, ctx ->
                PacketFinish.handle(packet, ctx)
            })
            .add()
    }
}