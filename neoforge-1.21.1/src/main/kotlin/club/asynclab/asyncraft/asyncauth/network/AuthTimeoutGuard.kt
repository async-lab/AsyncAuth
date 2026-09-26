package club.asynclab.asyncraft.asyncauth.network

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import io.netty.channel.Channel
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 配置阶段认证超时兜底
 * 超时则主动断开连接，保证会话一定会被释放
 */
object AuthTimeoutGuard {

    /** 每个Channel当前挂起的超时任务，用于认证完成时取消 */
    private val timeouts = ConcurrentHashMap<Channel, ScheduledFuture<*>>()

    /**
     * 为一次配置阶段的认证注册超时任务
     */
    fun schedule(listener: ServerConfigurationPacketListener, timeoutSeconds: Int) {
        if (timeoutSeconds <= 0) return

        val connection = listener.connection
        val channel = connection.channel()
        // 同一连接只保留一个超时任务（例如 returnToWorld 后任务重新开始）
        cancel(channel)

        val future = channel.eventLoop().schedule({
            timeouts.remove(channel)
            if (channel.isActive) {
                AsyncAuth.LOGGER.warn(
                    "Authentication timed out after {}s, disconnecting {}",
                    timeoutSeconds,
                    connection.remoteAddress
                )
                disconnect(listener, UtilComponent.getTranslatableComponent(Lang.Msg.AUTH_TIMEOUT))
            }
        }, timeoutSeconds.toLong(), TimeUnit.SECONDS)

        timeouts[channel] = future
    }

    /**
     * 取消指定连接上挂起的超时任务（客户端已完成认证时调用）
     */
    fun cancel(channel: Channel) {
        timeouts.remove(channel)?.cancel(false)
    }

    /**
     * 断开连接
     */
    private fun disconnect(listener: ServerConfigurationPacketListener, reason: Component) {
        val commonListener = listener as? ServerCommonPacketListenerImpl
        if (commonListener == null) {
            AsyncAuth.LOGGER.warn(
                "Unexpected configuration listener {}, closing the connection directly",
                listener.javaClass.name
            )
            listener.connection.disconnect(reason)
            return
        }
        commonListener.disconnect(reason)
    }
}
