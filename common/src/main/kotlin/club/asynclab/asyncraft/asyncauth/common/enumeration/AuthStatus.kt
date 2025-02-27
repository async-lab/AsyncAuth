package club.asynclab.asyncraft.asyncauth.common.enumeration

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon

enum class AuthStatus {
    SUCCESS,
    WRONG_PASSWORD,
    TOO_SHORT,
    EMPTY,
    EXISTS,
    NOT_EXISTS,
    UNKNOWN;

    fun msgPath(): String {
        return "msg.${BuiltConstantsCommon.MOD_ID}.auth_status.${this.name.lowercase()}"
    }
}