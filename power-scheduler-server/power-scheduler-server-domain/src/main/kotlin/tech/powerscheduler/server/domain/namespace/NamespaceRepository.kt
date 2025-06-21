package tech.powerscheduler.server.domain.namespace

import tech.powerscheduler.server.domain.common.Page

/**
 * @author grayrat
 * @since 2025/6/21
 */
interface NamespaceRepository {

    fun pageQuery(query: NamespaceQuery): Page<Namespace>

    fun findById(namespaceId: NamespaceId): Namespace?

    fun findByCode(code: String): Namespace?

    fun save(namespace: Namespace): NamespaceId

}