package tech.powerscheduler.server.domain.appgroup

import tech.powerscheduler.server.domain.common.Page

/**
 * 应用分组持久化仓库
 *
 * @author grayrat
 * @since 2025/4/16
 */
interface AppGroupRepository {

    fun pageQuery(query: AppGroupQuery): Page<AppGroup>

    fun existsByCode(code: String): Boolean

    fun findByCode(code: String): AppGroup?

    fun save(model: AppGroup): AppGroup

}