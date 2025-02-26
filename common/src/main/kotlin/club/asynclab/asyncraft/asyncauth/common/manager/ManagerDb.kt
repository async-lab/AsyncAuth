package club.asynclab.asyncraft.asyncauth.common.manager

import club.asynclab.asyncraft.asyncauth.common.util.UtilPassword
import club.asynclab.asyncraft.asyncauth.common.util.UtilSql.execute
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.ResultSet

class ManagerDb(
    address: String,
    port: Int,
    db: String,
    private val table: String,
    useSSL: Boolean,
    username: String,
    password: String,
) {

    private val dataSource: HikariDataSource = HikariDataSource(HikariConfig().also {
        it.driverClassName = "com.mysql.cj.jdbc.Driver"
        it.jdbcUrl = "jdbc:mysql://$address:$port/$db?characterEncoding=utf8&useSSL=$useSSL"
        it.username = username
        it.password = password
        it.isAutoCommit = false
        it.transactionIsolation = "TRANSACTION_SERIALIZABLE"
    })

    init {
        val sql = """
            CREATE TABLE IF NOT EXISTS ${this.table}
            (
                `id`         MEDIUMINT(8) UNSIGNED NOT NULL AUTO_INCREMENT,
                `username`   VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
                `realname`   VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
                `password`   VARCHAR(255) NOT NULL COLLATE 'ascii_bin',
                `ip`         VARCHAR(40) NULL DEFAULT NULL COLLATE 'ascii_bin',
                `lastlogin`  BIGINT(20) NULL DEFAULT NULL,
                `x` DOUBLE NOT NULL DEFAULT '0',
                `y` DOUBLE NOT NULL DEFAULT '0',
                `z` DOUBLE NOT NULL DEFAULT '0',
                `world`      VARCHAR(255) NOT NULL DEFAULT 'world' COLLATE 'utf8mb3_general_ci',
                `regdate`    BIGINT(20) NOT NULL DEFAULT '0',
                `regip`      VARCHAR(40) NULL DEFAULT NULL COLLATE 'ascii_bin',
                `yaw`        FLOAT NULL DEFAULT NULL,
                `pitch`      FLOAT NULL DEFAULT NULL,
                `email`      VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
                `isLogged`   SMALLINT(6) NOT NULL DEFAULT '0',
                `hasSession` SMALLINT(6) NOT NULL DEFAULT '0',
                `totp`       VARCHAR(32) NULL DEFAULT NULL COLLATE 'utf8mb3_general_ci',
                PRIMARY KEY (`id`) USING BTREE,
                UNIQUE INDEX `username` (`username`) USING BTREE
            ) COLLATE='utf8mb3_general_ci'
            ENGINE=InnoDB
            AUTO_INCREMENT=311
            ;
        """.trimIndent()
        this.dataSource.execute { connection ->
            connection.createStatement().execute(sql)
        }
    }

    private fun getHashedPassword(username: String): String? {
        val sql = "SELECT password FROM ${this.table} WHERE username = ? LIMIT 1;"
        return this.dataSource.execute { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, username.lowercase())
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getString("password") else null
                }
            }
        }
    }

    fun isExists(username: String): Boolean {
        val sql = "SELECT 1 FROM ${this.table} WHERE username = ? LIMIT 1;"
        return this.dataSource.execute { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, username.lowercase())
                stmt.executeQuery().use(ResultSet::next)
            }
        } ?: false
    }

    fun checkPassword(username: String, password: String): Boolean {
        val rawPassword = getHashedPassword(username) ?: return false
        return UtilPassword.check(password, rawPassword)
    }

    fun register(username: String, password: String): Boolean {
        val sql = "INSERT IGNORE INTO ${this.table} (username, realname, password) VALUES (?, ?, ?);"
        return this.dataSource.execute { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, username.lowercase())
                stmt.setString(2, username)
                stmt.setString(3, UtilPassword.hashPassword(password))
                stmt.executeUpdate() > 0
            }
        } ?: false
    }

    fun changePassword(username: String, password: String): Boolean {
        val sql = "UPDATE ${this.table} SET password = ? WHERE username = ?;"
        return this.dataSource.execute { connection ->
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, UtilPassword.hashPassword(password))
                stmt.setString(2, username.lowercase())
                stmt.executeUpdate() > 0
            }
        } ?: false
    }
}