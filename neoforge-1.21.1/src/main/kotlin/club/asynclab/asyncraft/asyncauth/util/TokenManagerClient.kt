package club.asynclab.asyncraft.asyncauth.util

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 客户端令牌管理器
 * 负责在客户端内存中存储和管理JWT令牌
 */
object TokenManagerClient {
    private val tokenStorage = ConcurrentHashMap<String, String>()
    private val tokenExpiryTime = ConcurrentHashMap<String, Long>()
    
    // 定时清理过期令牌
    private val cleanupExecutor = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "AsyncAuth-Client-TokenCleanup").apply { isDaemon = true }
    }
    
    init {
        // 每30分钟清理一次过期令牌
        cleanupExecutor.scheduleAtFixedRate(::clearExpiredTokens, 30, 30, TimeUnit.MINUTES)
    }
    
    /**
     * 存储令牌
     */
    fun storeToken(username: String, token: String, expiryTime: Long) {
        tokenStorage[username] = token
        tokenExpiryTime[username] = expiryTime
        AsyncAuth.LOGGER.info("Stored token for user: $username")
    }
    
    /**
     * 获取令牌
     */
    fun getToken(username: String): String? {
        val token = tokenStorage[username]
        return if (token != null && isTokenValid(username)) {
            token
        } else {
            if (token != null) {
                // 令牌过期，清除
                removeToken(username)
            }
            null
        }
    }
    
    /**
     * 移除令牌
     */
    fun removeToken(username: String) {
        tokenStorage.remove(username)
        tokenExpiryTime.remove(username)
        AsyncAuth.LOGGER.info("Removed token for user: $username")
    }
    
    /**
     * 检查令牌是否有效（未过期）
     */
    fun isTokenValid(username: String): Boolean {
        val expiryTime = tokenExpiryTime[username] ?: return false
        return System.currentTimeMillis() < expiryTime
    }
    
    /**
     * 检查是否存在令牌
     */
    fun hasToken(username: String): Boolean {
        return tokenStorage.containsKey(username) && isTokenValid(username)
    }
    
    /**
     * 清理过期令牌
     */
    fun clearExpiredTokens() {
        val now = System.currentTimeMillis()
        val expiredUsers = mutableListOf<String>()
        
        tokenExpiryTime.forEach { (username, expiryTime) ->
            if (expiryTime <= now) {
                expiredUsers.add(username)
            }
        }
        
        expiredUsers.forEach { username ->
            tokenStorage.remove(username)
            tokenExpiryTime.remove(username)
        }
        
        if (expiredUsers.isNotEmpty()) {
            AsyncAuth.LOGGER.info("Cleared ${expiredUsers.size} expired tokens")
        }
    }
    
    /**
     * 清除所有令牌
     */
    fun clearAllTokens() {
        tokenStorage.clear()
        tokenExpiryTime.clear()
        AsyncAuth.LOGGER.info("Cleared all stored tokens")
    }
    
    /**
     * 获取存储的令牌数量
     */
    fun getTokenCount(): Int = tokenStorage.size
    
    /**
     * 关闭令牌管理器
     */
    fun shutdown() {
        cleanupExecutor.shutdown()
        try {
            if (!cleanupExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            cleanupExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}