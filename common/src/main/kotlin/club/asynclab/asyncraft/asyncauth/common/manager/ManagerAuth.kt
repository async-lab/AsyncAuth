package club.asynclab.asyncraft.asyncauth.common.manager

import club.asynclab.asyncraft.asyncauth.common.Common
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus

open class ManagerAuth(
    private val managerDb: ManagerDb,
    private val minLength: Int,
) {
    fun isExists(username: String): AuthStatus {
        return executeWithExceptionHandling {
            if (managerDb.isExists(username)) AuthStatus.EXISTS else AuthStatus.NOT_EXISTS
        }
    }

    fun auth(username: String, password: String): AuthStatus {
        return executeWithExceptionHandling {
            val success = managerDb.checkPassword(username, password)
            if (success == null) AuthStatus.NOT_EXISTS
            else if (success) AuthStatus.SUCCESS else AuthStatus.WRONG_PASSWORD
        }
    }

    fun register(username: String, password: String): AuthStatus {
        return executeWithExceptionHandling {
            if (username.isEmpty() || password.isEmpty()) AuthStatus.EMPTY
            if (password.length < minLength) AuthStatus.TOO_SHORT
            if (this.managerDb.register(username, password)) AuthStatus.SUCCESS else AuthStatus.EXISTS
        }
    }

    fun changePassword(username: String, password: String): AuthStatus {
        return executeWithExceptionHandling {
            if (this.managerDb.changePassword(username, password)) AuthStatus.SUCCESS else AuthStatus.NOT_EXISTS
        }
    }

    private fun executeWithExceptionHandling(block: () -> AuthStatus): AuthStatus {
        return try {
            block()
        } catch (e: Exception) {
            Common.LOGGER.error(e.message)
            AuthStatus.UNKNOWN
        }
    }
}