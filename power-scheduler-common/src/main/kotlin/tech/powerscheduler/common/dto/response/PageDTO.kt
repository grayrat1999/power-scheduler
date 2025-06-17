package tech.powerscheduler.common.dto.response

/**
 * @author grayrat
 * @since 2025/4/16
 */
data class PageDTO<T>(
    var number: Int = 1,
    var size: Int = 20,
    var totalPages: Int = 1,
    var totalElements: Long = 0L,
    var content: List<T> = emptyList()
) {

    companion object {
        inline fun <reified T> empty(
            number: Int = 1,
            size: Int = 20,
        ): PageDTO<T> = PageDTO(
            number = number,
            size = size,
        )
    }

    fun <R> map(transform: (T) -> R): PageDTO<R> {
        return PageDTO(
            number = number,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages,
            content = content.map(transform)
        )
    }
}