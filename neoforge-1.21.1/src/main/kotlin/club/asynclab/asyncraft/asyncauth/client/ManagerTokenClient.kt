package club.asynclab.asyncraft.asyncauth.client

import club.asynclab.asyncraft.asyncauth.AsyncAuth
import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import net.neoforged.fml.loading.FMLPaths
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap

object ManagerTokenClient {
    data class StoredToken(val username: String, val token: String, val expiresAt: Long) {
        fun isExpired(now: Long = System.currentTimeMillis()): Boolean = now >= expiresAt
    }

    private val tokens = ConcurrentHashMap<String, StoredToken>()
    private val storageLock = Any()
    private val storageDir: Path by lazy {
        val dir = FMLPaths.CONFIGDIR.get().resolve(BuiltConstantsCommon.MOD_ID)
        try {
            Files.createDirectories(dir)
        } catch (ex: IOException) {
            AsyncAuth.LOGGER.warn("Failed to create token storage directory at {}", dir.toAbsolutePath(), ex)
        }
        dir
    }
    private val storageFile: Path get() = storageDir.resolve("client_tokens.properties")

    init {
        loadFromDisk()
    }

    fun storeToken(serverKey: String, username: String, token: String, expiresAt: Long) {
        tokens[serverKey] = StoredToken(username, token, expiresAt)
        persist()
        AsyncAuth.LOGGER.debug("Stored login token for {}", serverKey)
    }

    fun getValidToken(serverKey: String): StoredToken? {
        val stored = tokens[serverKey] ?: return null
        return if (stored.isExpired()) {
            removeToken(serverKey)
            null
        } else stored
    }

    fun removeToken(serverKey: String) {
        val removed = tokens.remove(serverKey)
        if (removed != null) {
            persist()
            AsyncAuth.LOGGER.debug("Removed stored token for {}", serverKey)
        }
    }

    fun clearExpiredTokens() {
        val now = System.currentTimeMillis()
        val expiredServers = tokens.filterValues { it.isExpired(now) }.keys
        expiredServers.forEach { tokens.remove(it) }
        if (expiredServers.isNotEmpty()) {
            persist()
            AsyncAuth.LOGGER.debug("Cleared {} expired token(s)", expiredServers.size)
        }
    }

    fun clearAllTokens() {
        if (tokens.isEmpty()) return
        tokens.clear()
        persist()
    }

    private fun loadFromDisk() {
        val path = storageFile
        if (!Files.exists(path)) return
        runCatching {
            Files.newInputStream(path).use { input ->
                val props = Properties().apply { load(input) }
                props.stringPropertyNames().forEach { serverKey ->
                    val raw = props.getProperty(serverKey) ?: return@forEach
                    val parts = raw.split("|", limit = 3)
                    if (parts.size == 3) {
                        val expiresAt = parts[2].toLongOrNull() ?: return@forEach
                        tokens[serverKey] = StoredToken(parts[0], parts[1], expiresAt)
                    }
                }
            }
        }.onFailure {
            AsyncAuth.LOGGER.warn("Failed to load stored tokens from {}", path.toAbsolutePath(), it)
            tokens.clear()
        }
    }

    private fun persist() {
        synchronized(storageLock) {
            runCatching {
                Files.newOutputStream(storageFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                ).use { output ->
                    val props = Properties()
                    tokens.forEach { (serverKey, entry) ->
                        props[serverKey] = listOf(entry.username, entry.token, entry.expiresAt).joinToString("|")
                    }
                    props.store(output, "AsyncAuth client token cache")
                }
            }.onFailure {
                AsyncAuth.LOGGER.warn("Failed to persist token cache to {}", storageFile.toAbsolutePath(), it)
            }
        }
    }
}