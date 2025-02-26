package club.asynclab.asyncraft.asyncauth.common.util

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

object UtilPassword {
    private val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
    private val random: SecureRandom = SecureRandom()
    private fun hash(str: String): String {
        digest.reset()
        digest.update(str.toByteArray())
        val hashcodeBytes = digest.digest()
        return "%0${hashcodeBytes.size * 2}x".format(BigInteger(1, hashcodeBytes))
    }

    private fun generateSalt(): String {
        return ByteArray(8).also { random.nextBytes(it) }.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String = generateSalt()): String {
        return "\$SHA$$salt$${hash(hash(password) + salt)}"
    }

    fun check(candidate: String, hashed: String): Boolean {
        return hashPassword(candidate, hashed.substring(5, 21)) == hashed
    }
}
