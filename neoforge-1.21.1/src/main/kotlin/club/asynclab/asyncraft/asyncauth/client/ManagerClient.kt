package club.asynclab.asyncraft.asyncauth.client

import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegister
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketTokenAuth
import club.asynclab.asyncraft.asyncauth.network.packet.misc.PacketHeart
import club.asynclab.asyncraft.asyncauth.util.toConfigurationPacket
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import net.minecraft.network.Connection
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket
import java.net.InetSocketAddress
import java.util.*


object ManagerClient {

    private var pendingConnection: Connection? = null
    private var pendingDeadline: Long = 0L
    private var lastAttemptUsername: String? = null
    private var autoLoginInProgress = false

    fun sendHeartbeat() {
        NetworkHandler.sendToServer(PacketHeart())
    }

    fun login(username: String, password: String) {
        lastAttemptUsername = username
        pendingConnection?.send(PacketLogin(username, password).toConfigurationPacket())
    }

    fun register(username: String, password: String) {
        lastAttemptUsername = username
        pendingConnection?.send(PacketRegister(username, password).toConfigurationPacket())
    }

    fun tryAutoLogin(deadline: Long): Boolean {
        pendingDeadline = deadline
        val serverKey = resolveServerKey() ?: return false
        val stored = ManagerTokenClient.getValidToken(serverKey) ?: return false

        autoLoginInProgress = true
        lastAttemptUsername = stored.username
        pendingConnection?.send(PacketTokenAuth(stored.username, stored.token).toConfigurationPacket())
        return true
    }

    fun handleResponse(status: AuthStatus, token: String?, expiresAt: Long, autoLogin: Boolean) {
        if (autoLogin) {
            autoLoginInProgress = false
        }

        if (status == AuthStatus.SUCCESS) {
            if (autoLogin) {
                UtilToast.toast(UtilComponent.getTranslatableComponent(Lang.Msg.AUTO_LOGIN_SUCCESS))
            } else {
                val serverKey = resolveServerKey()
                val username = lastAttemptUsername ?: Minecraft.getInstance().user.name
                if (serverKey != null) {
                    ManagerTokenClient.storeToken(serverKey, username, token!!, expiresAt)
                }
            }
        } else if (autoLogin) {
            resolveServerKey()?.let(ManagerTokenClient::removeToken)
            reopenLoginScreen()
        }
    }

    fun reopenLoginScreen() {
        autoLoginInProgress = false
        val mc = Minecraft.getInstance()
        if (mc.screen !is ScreenLogin) {
            val deadline = pendingDeadline.takeIf { it > 0 } ?: (System.currentTimeMillis() / 1000 + 60)
            mc.setScreen(ScreenLogin(deadline))
        }
    }

    fun cancelAutoLogin() {
        autoLoginInProgress = false
        reopenLoginScreen()
    }

    fun attachConnection(connection: Connection) {
        pendingConnection = connection
    }

    fun disconnection() {
        if (pendingConnection == null) return
        pendingConnection!!.disconnect(UtilComponent.getLiteralComponent("exited"))
        pendingConnection = null
        Minecraft.getInstance().screen?.onClose()
    }

    private fun resolveServerKey(): String? {
        val mc = Minecraft.getInstance()
        val currentServer = mc.currentServer
        if (currentServer != null && currentServer.ip.isNotBlank()) {
            return currentServer.ip.lowercase(Locale.ROOT)
        }

        val connection = pendingConnection ?: return null

        val remote = connection.remoteAddress
        return when (remote) {
            is InetSocketAddress -> "${remote.hostString.lowercase(Locale.ROOT)}:${remote.port}"
            else -> remote.toString().lowercase(Locale.ROOT)
        }
    }
}
