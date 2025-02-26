package club.asynclab.asyncraft.asyncauth.common.util

import java.io.BufferedReader

object UtilResource {
    fun <R> String.useIfResourceExist(action: (BufferedReader) -> R): R? {
        return UtilResource.javaClass.getResourceAsStream(this)?.bufferedReader()?.use(action)
    }
}