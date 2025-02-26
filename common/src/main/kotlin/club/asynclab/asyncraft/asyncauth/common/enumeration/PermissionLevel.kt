package club.asynclab.asyncraft.asyncauth.common.enumeration

enum class PermissionLevel(val level: Int) {
    PLAYER(0),   // 普通玩家
    JUNIOR(1),   // 初级权限
    MODERATOR(2),// 中级权限（通常是 OP）
    SENIOR(3),   // 高级权限
    ADMIN(4);    // 最高权限（通常是服务器管理员或控制台）

    companion object {
        fun fromInt(lvl: Int) = entries.firstOrNull { it.level == lvl } ?: PLAYER
    }
}