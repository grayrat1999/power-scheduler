package tech.powerscheduler.worker.util

/**
 * 用户根目录
 *
 * @author grayrat
 * @since 2025/6/4
 */
val USER_HOME: String? = System.getProperty("user.home")

/**
 * 工作目录
 *
 * @author grayrat
 * @since 2025/6/4
 */
val WORKSPACE = "${USER_HOME}/PowerSchedulerWorker"

/**
 * H2数据库文件的存放目录
 *
 * @author grayrat
 * @since 2025/6/4
 */
val H2_DIR = "${WORKSPACE}/h2"

/**
 * 脚本任务的代码存储目录
 *
 * @author grayrat
 * @since 2025/6/4
 */
val SCRIPT_DIR = "${WORKSPACE}/scripts"
