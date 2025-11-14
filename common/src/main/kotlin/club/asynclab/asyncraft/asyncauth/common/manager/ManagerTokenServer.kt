package club.asynclab.asyncraft.asyncauth.common.manager

import club.asynclab.asyncraft.asyncauth.common.enumeration.TokenValidationStatus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.UUID

object ManagerTokenServer {
    private data class TokenInfo(val token: String, val expiresAt: Long)

    private val tokens = ConcurrentHashMap<String, TokenInfo>()
    private var expirationMillis: Long = TimeUnit.MINUTES.toMillis(10)

    fun configure(expirationMinutes: Int) {
        expirationMillis = TimeUnit.MINUTES.toMillis(expirationMinutes.toLong().coerceAtLeast(1))
    }

    fun issueToken(username: String): TokenIssueResult {
        val expiresAt = System.currentTimeMillis() + expirationMillis
        val token = UUID.randomUUID().toString()
        tokens[username.lowercase()] = TokenInfo(token, expiresAt)
        return TokenIssueResult(token, expiresAt)
    }

    fun validate(username: String, token: String): TokenValidationStatus {
        val info = tokens[username.lowercase()] ?: return TokenValidationStatus.INVALID
        if (info.token != token) return TokenValidationStatus.INVALID
        if (System.currentTimeMillis() > info.expiresAt) {
            tokens.remove(username.lowercase())
            return TokenValidationStatus.EXPIRED
        }
        return TokenValidationStatus.VALID
    }

    fun revoke(username: String) {
        tokens.remove(username.lowercase())
    }

    data class TokenIssueResult(val token: String, val expiresAt: Long)
}
