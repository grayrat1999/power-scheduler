package tech.powerscheduler.worker.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import tech.powerscheduler.worker.util.H2_DIR
import java.io.File
import java.sql.Connection

/**
 * 数据源管理器
 *
 * @author grayrat
 * @since 2025/5/26
 */
object DataSourceManager {

    private val DB_FILE = System.getenv("DB_FILE") ?: "data"

    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:h2:file:${H2_DIR}/${DB_FILE};DB_CLOSE_ON_EXIT=FALSE"
        username = "sa"
        password = ""
        driverClassName = "org.h2.Driver"
        maximumPoolSize = 20
        minimumIdle = 2
        idleTimeout = 60000
        connectionTimeout = 2000
        maxLifetime = 1800000
    }

    private val dataSource = HikariDataSource(hikariConfig)

    fun getConnection(): Connection {
        return dataSource.connection
    }

    fun closeDataSource() {
        dataSource.close()
        val dbFilePath = "${H2_DIR}/${DB_FILE}.mv.db"
        val dbFile = File(dbFilePath)
        dbFile.delete()
    }

}
