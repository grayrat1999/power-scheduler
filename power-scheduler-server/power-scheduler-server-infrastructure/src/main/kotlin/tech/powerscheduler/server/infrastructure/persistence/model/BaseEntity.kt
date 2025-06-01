package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.LocalDateTime

/**
 * @author grayrat
 * @since 2025/4/16
 */
@MappedSuperclass
open class BaseEntity(
    /**
     * 创建人
     */
    @Column(name = "create_by", nullable = true, updatable = false)
    open var createdBy: String? = null,

    /**
     * 创建时间
     */
    @Column(name = "create_at", nullable = true, updatable = false)
    open var createdAt: LocalDateTime? = null,

    /**
     * 修改人
     */
    @Column(name = "update_by")
    open var updatedBy: String? = null,

    /**
     * 修改时间
     */
    @Column(name = "update_at", nullable = false)
    open var updatedAt: LocalDateTime? = null,
) {
    @PrePersist
    open fun prePersist() {
        this.createdAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    open fun postUpdate() {
        this.updatedAt = LocalDateTime.now()
    }
}