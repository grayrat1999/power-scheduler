package tech.powerscheduler.server.application.assembler

import org.springframework.stereotype.Component
import tech.powerscheduler.server.application.dto.request.WorkflowInstanceQueryRequestDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceDetailResponseDTO
import tech.powerscheduler.server.application.dto.response.WorkflowInstanceQueryResponseDTO
import tech.powerscheduler.server.application.utils.JSON
import tech.powerscheduler.server.domain.workflow.WorkflowInstance
import tech.powerscheduler.server.domain.workflow.WorkflowInstanceQuery

/**
 * @author grayrat
 * @since 2025/7/9
 */
@Component
class WorkflowInstanceAssembler {

    fun toDomainQuery(param: WorkflowInstanceQueryRequestDTO): WorkflowInstanceQuery {
        return WorkflowInstanceQuery().apply {
            this.pageNo = param.pageNo
            this.pageSize = param.pageSize
            this.status = param.status
        }
    }

    fun toWorkflowInstanceQueryResponseDTO(workflowInstance: WorkflowInstance): WorkflowInstanceQueryResponseDTO {
        return WorkflowInstanceQueryResponseDTO().apply {
            this.appName = workflowInstance.appGroup!!.name
            this.id = workflowInstance.id!!.value
            this.workflowId = workflowInstance.workflowId!!.value
            this.name = workflowInstance.name
            this.code = workflowInstance.code
            this.status = workflowInstance.status
            this.dataTime = workflowInstance.dataTime
        }
    }

    fun toWorkflowInstanceDetailResponseDTO(workflowInstance: WorkflowInstance): WorkflowInstanceDetailResponseDTO {
        return WorkflowInstanceDetailResponseDTO().apply {
            this.appName = workflowInstance.appGroup!!.name
            this.id = workflowInstance.id!!.value
            this.workflowId = workflowInstance.workflowId!!.value
            this.name = workflowInstance.name
            this.code = workflowInstance.code
            this.status = workflowInstance.status
            this.dataTime = workflowInstance.dataTime
            this.graphData = JSON.writeValueAsString(workflowInstance.graphData!!)
        }
    }

}