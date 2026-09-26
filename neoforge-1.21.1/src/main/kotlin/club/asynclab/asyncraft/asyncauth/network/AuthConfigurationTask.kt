package club.asynclab.asyncraft.asyncauth.network

import club.asynclab.asyncraft.asyncauth.common.network.NettyAttrKeys
import club.asynclab.asyncraft.asyncauth.network.packet.login.PacketInit
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask
import java.util.function.Consumer

class AuthConfigurationTask(
    private val listener: ServerConfigurationPacketListener,
    private val timeoutSeconds: Int
) : ICustomConfigurationTask {

    override fun type(): ConfigurationTask.Type = NetworkHandler.AUTH_TASK_TYPE

    override fun run(sender: Consumer<CustomPacketPayload>) {
        listener.connection.channel().attr(NettyAttrKeys.AUTHENTICATED).set(false)
        // 客户端若在超时时间内没有完成认证（崩溃/版本不匹配/异常路径），
        // 服务端主动断开
        AuthTimeoutGuard.schedule(listener, timeoutSeconds)
        sender.accept(PacketInit(timeoutSeconds))
    }
}
