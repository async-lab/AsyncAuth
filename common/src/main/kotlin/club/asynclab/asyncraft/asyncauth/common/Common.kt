package club.asynclab.asyncraft.asyncauth.common

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Common {
    val LOGGER: Logger = LoggerFactory.getLogger("AsyncAuth-Common")

    fun pkg(path: String) = "${BuiltConstantsCommon.MOD_GROUP_ID}.${BuiltConstantsCommon.MOD_ID}.$path"
}