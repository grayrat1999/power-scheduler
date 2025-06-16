package tech.powerscheduler.worker.task

import tech.powerscheduler.common.enums.ScriptTypeEnum

/**
 * 脚本任务上下文
 *
 * @author grayrat
 * @since 2025/5/19
 */
class ScriptTaskContext : TaskContext() {
    /**
     * 脚本类型
     */
    var scriptType: ScriptTypeEnum? = null

    /**
     * 脚本代码内容
     */
    var scriptCode: String? = null
}
