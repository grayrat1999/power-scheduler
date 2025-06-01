package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.domain.appgroup.AppGroup
import tech.powerscheduler.server.domain.appgroup.AppGroupQuery
import tech.powerscheduler.server.domain.appgroup.AppGroupRepository
import tech.powerscheduler.server.domain.common.Page
import tech.powerscheduler.server.infrastructure.persistence.model.AppGroupEntity
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.AppGroupJpaRepository
import tech.powerscheduler.server.infrastructure.utils.randomString
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toDomainPage

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Repository
class AppGroupRepositoryImpl(
    private val appGroupJpaRepository: AppGroupJpaRepository
) : AppGroupRepository {

    override fun pageQuery(query: AppGroupQuery): Page<AppGroup> {
        val pageable = PageRequest.of(
            query.pageNo - 1, query.pageSize, Sort.by(AppGroupEntity::id.name).descending()
        )
        val specification = Specification<AppGroupEntity> { root, _, criteriaBuilder ->
            val codeLike = query.code.takeUnless { it.isNullOrBlank() }?.let {
                criteriaBuilder.like(root.get(AppGroupEntity::code.name), "%$it%")
            }
            val nameLike = query.name.takeUnless { it.isNullOrBlank() }?.let {
                criteriaBuilder.like(root.get(AppGroupEntity::name.name), "%$it%")
            }
            val predicates = listOfNotNull(codeLike, nameLike)
            criteriaBuilder.and(*predicates.toTypedArray())
        }
        val page = appGroupJpaRepository.findAll(specification, pageable)
        return page.map { it.toDomainModel() }.toDomainPage()
    }

    override fun existsByCode(code: String): Boolean {
        return appGroupJpaRepository.existsByCode(code)
    }

    override fun findByCode(code: String): AppGroup? {
        val entity = appGroupJpaRepository.findByCode(code)
        return entity?.toDomainModel()
    }

    override fun save(model: AppGroup): AppGroup {
        val entity = AppGroupEntity().apply {
            this.id = model.id?.value
            this.name = model.name
            this.code = model.code
            this.secret = randomString(32)
        }
        appGroupJpaRepository.save(entity)
        return entity.toDomainModel()
    }
}