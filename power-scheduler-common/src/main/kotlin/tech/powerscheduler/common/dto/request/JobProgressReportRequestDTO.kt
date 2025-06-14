package tech.powerscheduler.common.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import tech.powerscheduler.common.enums.JobStatusEnum
import java.time.LocalDateTime

/**
 * 任务进度上报请求参数
 *
 * @author grayrat
 * @since 2025/5/20
 */
class JobProgressReportRequestDTO {
    /**
     * 任务实例id
     */
    @NotNull
    var jobInstanceId: Long? = null

    @NotNull
    var taskId: Long? = null

    /**
     * 任务状态
     */
    @NotNull
    var taskStatus: JobStatusEnum? = null

    /**
     * 开始时间
     */
    var startAt: LocalDateTime? = null

    /**
     * 结束时间
     */
    var endAt: LocalDateTime? = null

    /**
     * 任务结果或者异常信息
     */
    var message: String? = null

    @NotBlank
    var appCode: String? = null

    @NotBlank
    var accessToken: String? = null
}
