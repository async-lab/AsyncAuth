package club.asynclab.asyncraft.asyncauth.common.enumeration

enum class AuthStatus {
    SUCCESS,
    WRONG_PASSWORD,
    TOO_SHORT,
    EMPTY,
    EXISTS,
    NOT_EXISTS,
    UNKNOWN;
}