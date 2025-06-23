package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*

/**
 * @author grayrat
 * @since 2025/6/22
 */
@Entity
@Table(name = "workflow_instance")
class WorkflowInstanceEntity : BaseEntity() {
    /**
     * 工作流
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    var workflowEntity: WorkflowEntity? = null

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 工作流名称
     */
    @Column(name = "name", nullable = false, updatable = false)
    var name: String? = null
}