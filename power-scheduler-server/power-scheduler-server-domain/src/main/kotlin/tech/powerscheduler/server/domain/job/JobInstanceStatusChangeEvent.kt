package tech.powerscheduler.server.domain.job

data class JobInstanceStatusChangeEvent(
    val jobInstanceId: Long,
) {
    companion object {
       fun create(jobInstanceId: JobInstanceId):JobInstanceStatusChangeEvent {
           return JobInstanceStatusChangeEvent(jobInstanceId.value)
       }
    }
}
