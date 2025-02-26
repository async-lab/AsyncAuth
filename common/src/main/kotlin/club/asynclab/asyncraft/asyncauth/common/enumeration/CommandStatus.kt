package club.asynclab.asyncraft.asyncauth.common.enumeration

enum class CommandStatus(val status: Int) {
    SUCCESS(1),
    FAILED(0);

    companion object {
        fun fromInt(status: Int) = entries.firstOrNull { it.status == status } ?: FAILED
    }
}