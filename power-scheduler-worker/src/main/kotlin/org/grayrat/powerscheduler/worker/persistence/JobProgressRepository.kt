package org.grayrat.powerscheduler.worker.persistence

import org.grayrat.powerscheduler.common.enums.JobStatusEnum
import org.grayrat.powerscheduler.worker.util.ClasspathUtil.readTextFrom
import java.time.LocalDateTime

/**
 * 任务进度持仓库
 *
 * @author grayrat
 * @since 2025/5/25
 */
object JobProgressRepository {

    private val insertSql = readTextFrom("sql/job_progress/insert.sql")
    private val deleteByIdSql = readTextFrom("sql/job_progress/delete_by_id.sql")
    private val listByJobInstanceIdSql = readTextFrom("sql/job_progress/list_by_job_instance_id.sql")
    private val listDistinctJobInstanceIdSql = readTextFrom("sql/job_progress/list_distinct_job_instance_id.sql")

    fun save(entity: JobProgressEntity) {
        DataSourceManager.getConnection().use { conn ->
            conn.prepareStatement(insertSql).use { stmt ->
                stmt.setObject(1, entity.jobId)
                stmt.setObject(2, entity.jobInstanceId)
                stmt.setString(3, entity.status?.name)
                stmt.setObject(4, entity.startAt)
                stmt.setObject(5, entity.endAt)
                stmt.setString(6, entity.message)
                stmt.executeUpdate()
            }
        }
    }

    fun listDistinctJobInstanceIds(): Set<Long> {
        return DataSourceManager.getConnection().use { conn ->
            return@use conn.prepareStatement(listDistinctJobInstanceIdSql).use { stmt ->
                return@use stmt.executeQuery().use { rs ->
                    return@use generateSequence {
                        rs.takeIf { it.next() }?.getLong("job_instance_id")
                    }.toSet()
                }
            }
        }
    }

    fun listByJobInstanceId(jobInstanceId: Long): List<JobProgressEntity> {
        return DataSourceManager.getConnection().use { conn ->
            conn.prepareStatement(listByJobInstanceIdSql).use { stmt ->
                stmt.setLong(1, jobInstanceId)
                stmt.executeQuery().use { rs ->
                    generateSequence {
                        if (rs.next()) {
                            JobProgressEntity().apply {
                                this.id = rs.getLong("id")
                                this.jobId = rs.getLong("job_id")
                                this.jobInstanceId = rs.getLong("job_instance_id")
                                this.status = rs.getString("status")?.let { JobStatusEnum.valueOf(it) }
                                this.startAt = rs.getObject("start_at", LocalDateTime::class.java)
                                this.endAt = rs.getObject("end_at", LocalDateTime::class.java)
                                this.message = rs.getString("message")
                            }
                        } else {
                            null
                        }
                    }.toList()
                }
            }
        }
    }

    fun deleteByIds(ids: Iterable<Long>) {
        DataSourceManager.getConnection().use { conn ->
            conn.prepareStatement(deleteByIdSql).use { stmt ->
                for (id in ids) {
                    stmt.setLong(1, id)
                    stmt.addBatch()
                }
                stmt.executeBatch()
            }
        }
    }

}