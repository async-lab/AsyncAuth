package club.asynclab.asyncraft.asyncauth.common.manager

import club.asynclab.asyncraft.asyncauth.common.Common
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus

open class ManagerAuth(
    private val managerDb: ManagerDb,
) {
    fun isExists(username: String): AuthStatus {
        try {
            return if (managerDb.isExists(username)) AuthStatus.EXISTS else AuthStatus.NOT_EXISTS
        } catch (e: Exception) {
            Common.LOGGER.error(e.message)
            return AuthStatus.UNKNOWN
        }
    }

    fun auth(username: String, password: String): AuthStatus {
        try {
            val success = managerDb.checkPassword(username, password) ?: return AuthStatus.NOT_EXISTS
            return if (success) AuthStatus.SUCCESS else AuthStatus.WRONG_PASSWORD
        } catch (e: Exception) {
            Common.LOGGER.error(e.message)
            return AuthStatus.UNKNOWN
        }
    }

    fun register(username: String, password: String): AuthStatus {
        if (username.isEmpty() || password.isEmpty()) return AuthStatus.EMPTY

        try {
            return if (this.managerDb.register(username, password)) AuthStatus.SUCCESS else AuthStatus.EXISTS
        } catch (e: Exception) {
            Common.LOGGER.error(e.message)
            return AuthStatus.UNKNOWN
        }
    }

    fun changePassword(username: String, password: String): AuthStatus {
        try {
            return if (this.managerDb.changePassword(username, password)) AuthStatus.SUCCESS else AuthStatus.NOT_EXISTS
        } catch (e: Exception) {
            Common.LOGGER.error(e.message)
            return AuthStatus.UNKNOWN
        }
    }
}