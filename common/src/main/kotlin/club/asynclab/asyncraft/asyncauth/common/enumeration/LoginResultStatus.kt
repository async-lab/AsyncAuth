package club.asynclab.asyncraft.asyncauth.common.enumeration

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon

enum class LoginResultStatus {
    SUCCESS,
    WRONG_PASSWORD,
    TOO_SHORT,
    UNKNOWN;

    fun msgPath(): String {
        return "msg.${BuiltConstantsCommon.MOD_ID}.status.${this.name.lowercase()}"
    }
}