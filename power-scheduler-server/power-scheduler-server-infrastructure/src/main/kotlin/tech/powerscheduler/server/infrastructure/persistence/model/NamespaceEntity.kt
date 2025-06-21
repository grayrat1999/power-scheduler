package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*

/**
 * @author grayrat
 * @since 2025/6/21
 */
@Entity
@Table(name = "namespace")
class NamespaceEntity : BaseEntity() {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    /**
     * 命名空间编码
     */
    @Column(name = "code", unique = true, nullable = false, updatable = false)
    var code: String? = null

    /**
     * 命名空间名称
     */
    @Column(name = "name", nullable = false)
    var name: String? = null

    /**
     * 命名空间描述
     */
    @Column(name = "description")
    var description: String? = null
}