package tech.powerscheduler.server.domain.worker

import tech.powerscheduler.server.domain.appgroup.AppGroupKey
import java.time.LocalDateTime

/**
 * 任务注册记录持久化仓库
 *
 * @author grayrat
 * @since 2025/4/29
 */
interface WorkerRegistryRepository {

    fun countByNamespaceCodeAndAppCode(namespaceCode: String, appCode: String): Long

    fun lockById(id: WorkerRegistryId): WorkerRegistry?

    fun findAllByAppGroupKey(groupKey: AppGroupKey): List<WorkerRegistry>

    fun findAllByAppGroupKeys(
        groupKeys: Collection<AppGroupKey>
    ): Map<AppGroupKey, List<WorkerRegistry>>

    fun findAllExpired(expiredAt: LocalDateTime): List<WorkerRegistry>

    fun findByUk(uniqueKey: WorkerRegistryUniqueKey): WorkerRegistry?

    fun findByAccessToken(accessToken: String): WorkerRegistry?

    fun save(workerRegistry: WorkerRegistry): WorkerRegistryId

    fun delete(id: WorkerRegistryId)

    fun deleteAll(ids: Iterable<WorkerRegistryId>)
}