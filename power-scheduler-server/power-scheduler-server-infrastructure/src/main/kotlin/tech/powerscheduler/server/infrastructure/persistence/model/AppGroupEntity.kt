package tech.powerscheduler.server.infrastructure.persistence.model

import jakarta.persistence.*

/**
 * @author grayrat
 * @since 2025/4/16
 */
@Entity
@Table(
    name = "app_group",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["namespace_id", "code"]),
    ]
)
class AppGroupEntity : BaseEntity() {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "namespace_id", nullable = false)
    var namespaceEntity: NamespaceEntity? = null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null

    @Column(name = "code", nullable = false, updatable = false)
    var code: String? = null

    @Column(name = "secret", updatable = false)
    var secret: String? = null

    @Column(name = "name", nullable = false)
    var name: String? = null
}
