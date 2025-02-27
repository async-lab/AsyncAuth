package club.asynclab.asyncraft.asyncauth.network

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLoginResponse
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegister
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegisterResponse
import club.asynclab.asyncraft.asyncauth.network.packet.heart.PacketPing
import club.asynclab.asyncraft.asyncauth.network.packet.heart.PacketPong
import club.asynclab.asyncraft.asyncauth.network.packet.login.BasePacketLogin
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketFinish
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketInit
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
                if (local)
                    emptyList()
                else
                    listOf(org.apache.commons.lang3.tuple.Pair.of("init", PacketInit(ModSetting.timeout.get())))
            }
            .add()
        LOGIN.messageBuilder(PacketLogin::class.java, id++)
            .encoder(PacketLogin::encode)
            .decoder(PacketLogin::decode)
            .consumer(PacketLogin::handle)
            .add()
        LOGIN.messageBuilder(PacketLoginResponse::class.java, id++)
            .encoder(PacketLoginResponse::encode)
            .decoder(PacketLoginResponse::decode)
            .consumer(PacketLoginResponse::handle)
            .add()
        LOGIN.messageBuilder(PacketRegister::class.java, id++)
            .encoder(PacketRegister::encode)
            .decoder(PacketRegister::decode)
            .consumer(PacketRegister::handle)
            .add()
        LOGIN.messageBuilder(PacketRegisterResponse::class.java, id++)
            .encoder(PacketRegisterResponse::encode)
            .decoder(PacketRegisterResponse::decode)
            .consumer(PacketRegisterResponse::handle)
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