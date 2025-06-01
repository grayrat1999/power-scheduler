package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Table(name = "app_group")
@Entity
class AppGroupEntity : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    @Column(name = "code", unique = true, nullable = false)
    var code: String? = null

    @Column(name = "secret")
    var secret: String? = null

    @Column(name = "name", nullable = false)
    var name: String? = null
}
