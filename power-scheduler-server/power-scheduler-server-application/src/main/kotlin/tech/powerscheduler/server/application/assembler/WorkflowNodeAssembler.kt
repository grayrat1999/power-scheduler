package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowNodeAddRequestDTO
import tech.powerscheduler.server.application.dto.request.WorkflowNodeEditRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowNodeQueryResponseDTO
import tech.powerscheduler.server.domain.workflow.Workflow
import tech.powerscheduler.server.domain.workflow.WorkflowNode

/**
 * @author grayrat
 * @since 2025/6/23
 */
@Component
class WorkflowNodeAssembler {

    fun toWorkflowNodeQueryResponseDTO(workflowNode: WorkflowNode): WorkflowNodeQueryResponseDTO {
        return WorkflowNodeQueryResponseDTO().apply {

        }
    }

    fun toDomainModel4AddRequest(workflow: Workflow, param: WorkflowNodeAddRequestDTO): WorkflowNode {
        return WorkflowNode().apply {
            this.workflow = workflow
            this.parents = emptySet()
            this.children = emptySet()
            this.jobName = param.jobName
            this.jobDesc = param.jobDesc
            this.jobType = param.jobType
            this.processor = param.processor
            this.executeMode = param.executeMode
            this.executeParams = param.executeParams
            this.scriptType = param.scriptType
            this.scriptCode = param.scriptCode
            this.maxAttemptCnt = param.maxAttemptCnt
            this.attemptInterval = param.attemptInterval
            this.taskMaxAttemptCnt = param.taskMaxAttemptCnt
            this.taskAttemptInterval = param.taskAttemptInterval
            this.priority = param.priority
        }
    }

    fun toDomainModel4EditRequest(workflowNode: WorkflowNode, param: WorkflowNodeEditRequestDTO): WorkflowNode {
        return WorkflowNode().apply {
            this.workflow = workflowNode.workflow
            this.parents = workflowNode.parents
            this.children = workflowNode.children
            this.id = workflowNode.id
            this.jobName = param.jobName
            this.jobDesc = param.jobDesc
            this.jobType = param.jobType
            this.processor = param.processor
            this.executeMode = param.executeMode
            this.executeParams = param.executeParams
            this.scriptType = param.scriptType
            this.scriptCode = param.scriptCode
            this.maxAttemptCnt = param.maxAttemptCnt
            this.attemptInterval = param.attemptInterval
            this.taskMaxAttemptCnt = param.taskMaxAttemptCnt
            this.taskAttemptInterval = param.taskAttemptInterval
            this.priority = param.priority
        }
    }
}