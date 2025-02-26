package club.asynclab.asyncraft.asyncauth.common.util

import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

object UtilSql {
    fun <R> DataSource.execute(action: (Connection) -> R): R? {
        this.connection.use {
            try {
                return action(it).apply { it.commit() }
            } catch (e: SQLException) {
                println(e.message)
                connection.rollback()
            }
            return null
        }
    }
}