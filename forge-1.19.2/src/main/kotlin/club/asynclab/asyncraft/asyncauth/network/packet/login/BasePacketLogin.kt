package club.asynclab.asyncraft.asyncauth.network.packet.login

import java.util.function.IntSupplier

open class BasePacketLogin : IntSupplier {
    var loginIndex: Int = 0
    override fun getAsInt(): Int = this.loginIndex
}