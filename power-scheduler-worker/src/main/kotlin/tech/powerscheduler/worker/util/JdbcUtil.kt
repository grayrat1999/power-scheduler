package tech.powerscheduler.worker.util

import tech.powerscheduler.worker.persistence.DataSourceManager

/**
 * 执行sql语句（目前仅用于初始化h2数据库的表结构）
 *
 * @author grayrat
 * @since 2025/5/25
 */
fun executeSql(sql: String) {
    DataSourceManager.getConnection().use { conn ->
        conn.createStatement().use { stmt ->
            stmt.execute(sql)
        }
    }
}