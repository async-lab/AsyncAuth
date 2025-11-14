package club.asynclab.asyncraft.asyncauth.client

import club.asynclab.asyncraft.asyncauth.client.gui.ScreenLogin
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegister
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketTokenAuth
import club.asynclab.asyncraft.asyncauth.network.packet.misc.PacketHeart
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import net.minecraft.client.Minecraft
import java.net.InetSocketAddress
import java.util.Locale
import java.util.function.Supplier
import net.minecraftforge.network.NetworkEvent

object ManagerClient {
    private var ctxSupplier: Supplier<NetworkEvent.Context>? = null
    private var pendingDeadline: Long = 0L
    private var lastAttemptUsername: String? = null
    private var autoLoginInProgress = false

    fun attachContext(ctx: Supplier<NetworkEvent.Context>) {
        ctxSupplier = ctx
    }

    fun sendHeartbeat() {
        val ctx = ctxSupplier ?: return
        NetworkHandler.LOGIN.reply(PacketHeart(), ctx.get())
    }

    fun login(username: String, password: String) {
        val ctx = ctxSupplier ?: return
        lastAttemptUsername = username
        NetworkHandler.LOGIN.reply(PacketLogin(username, password), ctx.get())
    }

    fun register(username: String, password: String) {
        val ctx = ctxSupplier ?: return
        lastAttemptUsername = username
        NetworkHandler.LOGIN.reply(PacketRegister(username, password), ctx.get())
    }

    fun tryAutoLogin(ctx: Supplier<NetworkEvent.Context>, deadline: Long): Boolean {
        attachContext(ctx)
        pendingDeadline = deadline
        val serverKey = resolveServerKey() ?: return false
        val stored = ManagerTokenClient.getValidToken(serverKey) ?: return false
        autoLoginInProgress = true
        lastAttemptUsername = stored.username
        NetworkHandler.LOGIN.reply(PacketTokenAuth(stored.username, stored.token), ctx.get())
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
        } else {

        }

    }

    fun reopenLoginScreen() {
        val ctx = ctxSupplier ?: return
        autoLoginInProgress = false
        if (Minecraft.getInstance().screen !is ScreenLogin) {
            val deadline = pendingDeadline.takeIf { it > 0 } ?: (System.currentTimeMillis() / 1000 + 60)
            Minecraft.getInstance().setScreen(ScreenLogin(ctx, deadline))
        }
    }

    fun cancelAutoLogin() {
        autoLoginInProgress = false
        reopenLoginScreen()
    }

    private fun resolveServerKey(): String? {
        val mc = Minecraft.getInstance()
        val currentServer = mc.currentServer
        if (currentServer != null && currentServer.ip.isNotBlank()) {
            return currentServer.ip.lowercase(Locale.ROOT)
        }

        val networkManager = ctxSupplier?.get()?.networkManager ?: return null
        val remote = networkManager.remoteAddress
        return when (remote) {
            is InetSocketAddress -> "${remote.hostString.lowercase(Locale.ROOT)}:${remote.port}"
            else -> remote.toString().lowercase(Locale.ROOT)
        }
    }
}
