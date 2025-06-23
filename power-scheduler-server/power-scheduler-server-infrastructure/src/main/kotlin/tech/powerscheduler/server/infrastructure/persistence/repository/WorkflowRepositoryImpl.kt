package tech.powerscheduler.server.infrastructure.persistence.repository

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import tech.powerscheduler.server.domain.workflow.Workflow
import tech.powerscheduler.server.domain.workflow.WorkflowId
import tech.powerscheduler.server.domain.workflow.WorkflowRepository
import tech.powerscheduler.server.infrastructure.persistence.repository.impl.WorkflowJpaRepository
import tech.powerscheduler.server.infrastructure.utils.toDomainModel
import tech.powerscheduler.server.infrastructure.utils.toEntity

/**
 * @author grayrat
 * @since 2025/6/22
 */
@Repository
class WorkflowRepositoryImpl(
    private val workflowJpaRepository: WorkflowJpaRepository
) : WorkflowRepository {

    override fun findById(workflowId: WorkflowId): Workflow? {
        val entity = workflowJpaRepository.findByIdOrNull(workflowId.value)
        return entity?.toDomainModel()
    }

    override fun save(workflow: Workflow): WorkflowId {
        val entity = workflow.toEntity()
        workflowJpaRepository.save(entity)
        return WorkflowId(entity.id!!)
    }

    override fun deleteById(workflowId: WorkflowId) {
        workflowJpaRepository.deleteById(workflowId.value)
    }

}