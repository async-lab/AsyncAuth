package club.asynclab.asyncraft.asyncauth.util

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.common.util.UtilResource.useIfResourceExist

object Utils {
    fun asciiArt() {
        "/ascii-art.txt".useIfResourceExist { reader ->
            reader.lines().forEach { line -> AsyncAuth.LOGGER.info(line) }
        }
    }
}

