package tech.powerscheduler.server.domain.worker

import tech.powerscheduler.server.domain.common.AppCode
import java.time.LocalDateTime

/**
 * 任务注册记录持久化仓库
 *
 * @author grayrat
 * @since 2025/4/29
 */
interface WorkerRegistryRepository {

    fun count(): Long

    fun countByAppCode(appCode: String): Long

    fun lockById(id: WorkerRegistryId): WorkerRegistry?

    fun findAllByAppCode(appCode: String): List<WorkerRegistry>

    fun findAllByAppCodes(appCodes: Iterable<String>): Map<AppCode, List<WorkerRegistry>>

    fun findAllExpired(expiredAt: LocalDateTime): List<WorkerRegistry>

    fun findByUk(uniqueKey: WorkerRegistryUniqueKey): WorkerRegistry?

    fun findByAccessToken(accessToken: String): WorkerRegistry?

    fun save(workerRegistry: WorkerRegistry): WorkerRegistryId

    fun delete(id: WorkerRegistryId)

    fun deleteAll(ids: Iterable<WorkerRegistryId>)
}