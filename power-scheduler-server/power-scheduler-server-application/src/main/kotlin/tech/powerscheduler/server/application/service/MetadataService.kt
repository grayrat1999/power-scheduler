package tech.powerscheduler.server.application.service

import org.springframework.stereotype.Service
import tech.powerscheduler.common.annotation.Metadata
import tech.powerscheduler.common.enums.*
import tech.powerscheduler.server.application.dto.response.MetadataDTO
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * @author grayrat
 * @since 2025/5/18
 */
@Service
class MetadataService {

    private val metadataEnumMap: Map<String, KClass<*>> = listOf(
        JobTypeEnum::class,
        JobStatusEnum::class,
        ScheduleTypeEnum::class,
        ExecuteModeEnum::class,
        ScriptTypeEnum::class,
        RetentionPolicyEnum::class,
    ).associateBy { it.simpleName!! }

    fun listMetadata(metadataCodes: List<String>): List<MetadataDTO> {
        return metadataCodes.mapNotNull { buildMetadata(it) }
    }

    fun buildMetadata(metadataCode: String): MetadataDTO? {
        val metadataClass = metadataEnumMap[metadataCode] ?: return null
        val metadataAnnotation = metadataClass.findAnnotation<Metadata>()!!
        val options = metadataClass.java.enumConstants.mapNotNull { buildOption(it as BaseEnum) }
        return MetadataDTO(
            code = metadataCode,
            label = metadataAnnotation.label,
            options = options
        )
    }

    private fun buildOption(option: BaseEnum): MetadataDTO? {
        // 未完成的功能暂时不展示给前端
        if (option is ExecuteModeEnum) {
            if (option in setOf(ExecuteModeEnum.BROADCAST, ExecuteModeEnum.MAP_REDUCE)) {
                return null
            }
        }
        return MetadataDTO(
            code = option.code,
            label = option.label,
        )
    }
}