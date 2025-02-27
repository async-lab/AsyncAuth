package club.asynclab.asyncraft.asyncauth.common.network

import io.netty.util.AttributeKey

object NettyAttrKeys {
    val AUTHENTICATED: AttributeKey<Boolean> = AttributeKey.valueOf("authenticated")
}