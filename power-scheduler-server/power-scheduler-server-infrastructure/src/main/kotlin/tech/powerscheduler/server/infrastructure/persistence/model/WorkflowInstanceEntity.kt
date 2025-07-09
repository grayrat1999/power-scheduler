package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*
import tech.powerscheduler.common.enums.WorkflowStatusEnum
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/6/22
 */
@Entity
@Table(name = "workflow_instance")
class WorkflowInstanceEntity : BaseEntity() {

    /**
     * 应用分组
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_group_id", nullable = false)
    var appGroupEntity: AppGroupEntity? = null

    /**
     * 工作流节点实例列表
     */
    @OneToMany(
        mappedBy = "workflowInstanceEntity",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var workflowNodeInstanceEntities: Set<WorkflowNodeInstanceEntity> = emptySet()

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 工作流id
     */
    @Column(name = "workflow_id", nullable = false, updatable = false)
    var workflowId: Long? = null

    /**
     * 工作流名称
     */
    @Column(name = "name", nullable = false, updatable = false)
    var name: String? = null

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: WorkflowStatusEnum? = null

    /**
     * 数据时间
     */
    @Column(name = "data_time", nullable = false)
    var dataTime: LocalDateTime? = null
}