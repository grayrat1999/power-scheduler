package tech.powerscheduler.worker.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.File
import java.sql.Connection

/**
 * 数据源管理器
 *
 * @author grayrat
 * @since 2025/5/26
 */
object DataSourceManager {

    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:h2:file:~/PowerSchedulerWorkerData/h2_data;DB_CLOSE_ON_EXIT=FALSE"
        username = "sa"
        password = ""
        driverClassName = "org.h2.Driver"
        maximumPoolSize = 10
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
        val homeDir = System.getProperty("user.home")
        val dbFilePath = "$homeDir/PowerSchedulerWorkerData/h2_data.mv.db"
        val dbFile = File(dbFilePath)
        dbFile.delete()
    }

}
