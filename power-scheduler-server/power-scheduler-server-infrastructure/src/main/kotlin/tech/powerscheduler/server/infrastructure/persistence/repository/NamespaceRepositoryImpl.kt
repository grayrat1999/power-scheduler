package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.domain.namespace.Namespace
import tech.powerscheduler.server.domain.namespace.NamespaceId
import tech.powerscheduler.server.domain.namespace.NamespaceQuery
import tech.powerscheduler.server.domain.namespace.NamespaceRepository
import tech.powerscheduler.server.infrastructure.persistence.model.NamespaceEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.NamespaceJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toDomainPage
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/6/21
 */
@Repository
class NamespaceRepositoryImpl(
    private val namespaceJpaRepository: NamespaceJpaRepository
) : NamespaceRepository {

    override fun pageQuery(query: NamespaceQuery): Page<Namespace> {
        val pageable = PageRequest.of(
            query.pageNo - 1,
            query.pageSize,
            Sort.by(NamespaceEntity::id.name).descending()
        )
        val specification = Specification<NamespaceEntity> { root, _, criteriaBuilder ->
            val nameLike = query.name.takeUnless { it.isNullOrBlank() }?.let {
                criteriaBuilder.like(root.get(NamespaceEntity::name.name), "%$it%")
            }
            val predicates = listOfNotNull(nameLike)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val page = namespaceJpaRepository.findAll(specification, pageable)
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun findById(namespaceId: NamespaceId): Namespace? {
        val entity = namespaceJpaRepository.findByIdOrNull(namespaceId.value)
        return entity?.toDomainModel()
    }

    override fun findByCode(code: String): Namespace? {
        val entity = namespaceJpaRepository.findByCode(code)
        return entity?.toDomainModel()
    }

    override fun save(namespace: Namespace): NamespaceId {
        val entity = namespace.toEntity()
         namespaceJpaRepository.save(entity)
        return NamespaceId(entity.id!!)
    }

}