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
     * 工作流编码
     */
    @Column(name = "code", nullable = false, updatable = false)
    var code: String? = null

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

    /**
     * 有向无环图的UI数据
     */
    @Column(name = "graph_data", nullable = false)
    var graphData: String? = null

    /**
     * 开始时间
     */
    @Column(name = "start_at", insertable = false)
    var startAt: LocalDateTime? = null

    /**
     * 结束时间
     */
    @Column(name = "end_at", insertable = false)
    var endAt: LocalDateTime? = null
}