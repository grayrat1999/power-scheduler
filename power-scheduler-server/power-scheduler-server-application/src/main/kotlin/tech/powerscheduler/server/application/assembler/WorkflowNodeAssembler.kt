package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowNodeDTO
import tech.powerscheduler.server.domain.workflow.Workflow
import tech.powerscheduler.server.domain.workflow.WorkflowNode

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Component
class WorkflowNodeAssembler {

    fun toDomainModel4AddRequest(
        workflow: Workflow,
        nodes: List<WorkflowNodeDTO>,
    ): List<WorkflowNode> {
        val uuid2workflowNode = nodes.associate {
            Pair(
                it.uuid,
                toDomainModel4Add(
                    workflow = workflow,
                    currentNode = it,
                )
            )
        }
        nodes.forEach {
            val workflowNode = uuid2workflowNode[it.uuid]!!
            val children = it.childrenUuids.mapNotNull { childUuid -> uuid2workflowNode[childUuid] }
            workflowNode.children = children.toSet()
        }
        return uuid2workflowNode.values.toList()
    }

    fun toDomainModel4EditRequest(
        workflow: Workflow,
        nodes: List<WorkflowNodeDTO>,
        existNodes: List<WorkflowNode>,
    ): List<WorkflowNode> {
        val uuid2existNode = existNodes.associateBy { it.uuid!! }
        val uuid2workflowNode = nodes.associate {
            Pair(
                it.uuid,
                if (uuid2existNode.containsKey(it.uuid)) {
                    toDomainModel4Edit(
                        workflow = workflow,
                        currentNode = it,
                        existNode = uuid2existNode[it.uuid]!!,
                    )
                } else {
                    toDomainModel4Add(
                        workflow = workflow,
                        currentNode = it,
                    )
                }
            )
        }

        nodes.forEach {
            val workflowNode = uuid2workflowNode[it.uuid]!!
            val children = it.childrenUuids.mapNotNull { childUuid -> uuid2workflowNode[childUuid] }
            workflowNode.children = children.toSet()
        }
        return uuid2workflowNode.values.toList()
    }

    fun toDomainModel4Add(
        workflow: Workflow,
        currentNode: WorkflowNodeDTO,
    ): WorkflowNode {
        return WorkflowNode().apply {
            this.workflow = workflow
            this.uuid = currentNode.uuid
            this.name = currentNode.name
            this.description = currentNode.description
            this.jobType = currentNode.jobType
            this.processor = currentNode.processor
            this.executeMode = currentNode.executeMode
            this.executeParams = currentNode.executeParams
            this.scriptType = currentNode.scriptType
            this.scriptCode = currentNode.scriptCode
            this.maxAttemptCnt = currentNode.maxAttemptCnt
            this.attemptInterval = currentNode.attemptInterval
            this.taskMaxAttemptCnt = currentNode.taskMaxAttemptCnt
            this.taskAttemptInterval = currentNode.taskAttemptInterval
            this.priority = currentNode.priority
        }
    }

    fun toDomainModel4Edit(
        workflow: Workflow,
        currentNode: WorkflowNodeDTO,
        existNode: WorkflowNode,
    ): WorkflowNode {
        return WorkflowNode().apply {
            this.workflow = workflow
            this.id = existNode.id
            this.uuid = currentNode.uuid
            this.name = currentNode.name
            this.description = currentNode.description
            this.jobType = currentNode.jobType
            this.processor = currentNode.processor
            this.executeMode = currentNode.executeMode
            this.executeParams = currentNode.executeParams
            this.scriptType = currentNode.scriptType
            this.scriptCode = currentNode.scriptCode
            this.maxAttemptCnt = currentNode.maxAttemptCnt
            this.attemptInterval = currentNode.attemptInterval
            this.taskMaxAttemptCnt = currentNode.taskMaxAttemptCnt
            this.taskAttemptInterval = currentNode.taskAttemptInterval
            this.priority = currentNode.priority
        }
    }

}