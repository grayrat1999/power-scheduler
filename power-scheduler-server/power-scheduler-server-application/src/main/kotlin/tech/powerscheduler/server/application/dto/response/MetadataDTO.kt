package tech.powerscheduler.server.application.dto.response

/**
 * 元数据DTO
 *
 * @author grayrat
 * @since 2025/4/18
 */
data class MetadataDTO(
    var code: String? = null,
    val label: String? = null,
    val options: List<MetadataDTO> = emptyList(),
)