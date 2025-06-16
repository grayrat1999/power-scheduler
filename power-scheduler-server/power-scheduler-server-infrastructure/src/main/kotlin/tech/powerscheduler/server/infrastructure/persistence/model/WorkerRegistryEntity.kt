package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/4/29
 */
@Entity
@Table(
    name = "worker_registry",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["app_code", "addr", "port"]),
    ]
)
class WorkerRegistryEntity : BaseEntity() {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 应用编码
     */
    @Column(name = "app_code")
    var appCode: String? = null

    /**
     * ip地址
     */
    @Column(name = "addr", nullable = false)
    var host: String? = null

    /**
     * 端口
     */
    @Column(name = "port", nullable = false)
    var port: Int? = null

    /**
     * 访问凭证
     */
    @Column(name = "access_token", unique = true, nullable = false, updatable = false)
    var accessToken: String? = null

    /**
     * 最后心跳时间
     */
    @Column(name = "last_heartbeat_at")
    var lastHeartbeatAt: LocalDateTime? = null

    /**
     * 乐观锁
     */
    @Version
    @Column(name = "version")
    var version: Long? = null

    /**
     * CPU利用率
     */
    @Column(name = "cpu_usage_percent", insertable = false)
    var cpuUsagePercent: Double? = null

    /**
     * 内存利用率
     */
    @Column(name = "memory_usage_percent", insertable = false)
    var memoryUsagePercent: Double? = null
}