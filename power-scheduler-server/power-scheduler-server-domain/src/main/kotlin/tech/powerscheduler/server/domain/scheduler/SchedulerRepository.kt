package tech.powerscheduler.server.domain.scheduler

/**
 * @author grayrat
 * @since 2025/7/11
 */
interface SchedulerRepository {

    fun findByAddress(address: String): Scheduler?

    fun save(scheduler: Scheduler): SchedulerId

}