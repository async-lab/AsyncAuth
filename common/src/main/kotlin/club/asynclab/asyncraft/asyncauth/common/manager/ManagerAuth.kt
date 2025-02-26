package club.asynclab.asyncraft.asyncauth.common.manager

open class ManagerAuth(
    private val managerDb: ManagerDb,
) {
    fun isExists(username: String) = this.managerDb.isExists(username)

    fun login(username: String, password: String): Boolean {
        return managerDb.checkPassword(username, password)
    }

    // TODO(dsx137): 很明显还差很多功能，注册完应该要重新登录才对
    fun register(username: String, password: String): Boolean {
        if (password.isEmpty()) return false
        return this.managerDb.register(username, password)
    }

    fun changePassword(username: String, password: String): Boolean {
        return this.managerDb.changePassword(username, password)
    }
}