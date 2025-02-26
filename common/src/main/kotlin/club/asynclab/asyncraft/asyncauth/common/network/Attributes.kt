package club.asynclab.asyncraft.asyncauth.common.network

import io.netty.util.AttributeKey

object Attributes {
    val LOGGED: AttributeKey<Boolean> = AttributeKey.valueOf<Boolean>("logged")
}