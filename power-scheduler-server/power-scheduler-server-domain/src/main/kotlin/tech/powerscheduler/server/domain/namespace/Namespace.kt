package tech.powerscheduler.server.domain.namespace

import java.time.LocalDateTime

/**
 * 命名空间
 *
 * @author grayrat
 * @since 2025/6/21
 */
class Namespace {
    /**
     * 主键
     */
    var id: NamespaceId? = null

    /**
     * 命名空间编码
     */
    var code: String? = null

    /**
     * 命名空间名称
     */
    var name: String? = null

    /**
     * 命名空间描述
     */
    var description: String? = null

    /**
     * 创建人
     */
    var createdBy: String? = null

    /**
     * 创建时间
     */
    var createdAt: LocalDateTime? = null
}