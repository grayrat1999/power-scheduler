package tech.powerscheduler.worker.processor

import org.slf4j.LoggerFactory
import tech.powerscheduler.worker.job.JobContext
import tech.powerscheduler.worker.job.ScriptJobContext
import tech.powerscheduler.worker.processor.ProcessResult.Companion.failure
import tech.powerscheduler.worker.processor.ProcessResult.Companion.success
import tech.powerscheduler.worker.util.SCRIPT_DIR
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

/**
 * 脚本任务处理器
 *
 * @author grayrat
 * @since 2025/5/15
 */
class ScriptProcessor : Processor {

    companion object {
        const val PROCESSOR_PATH = "ScriptProcessor"
    }

    private val log = LoggerFactory.getLogger(ScriptProcessor::class.java)

    override fun process(context: JobContext): ProcessResult {
        return doProcess((context as ScriptJobContext))
    }

    fun doProcess(context: ScriptJobContext): ProcessResult {
        val dir = File(SCRIPT_DIR)
        if (!dir.exists()) {
            dir.mkdir()
        }

        val scriptType = context.scriptType
        val scriptFilePath = SCRIPT_DIR + File.separator + context.jobInstanceId + scriptType!!.suffix
        val file = File(scriptFilePath)
        try {
            file.writeText(context.scriptCode.orEmpty(), Charsets.UTF_8)
        } catch (e: Exception) {
            return failure("Failed to write script file: " + e.message)
        }

        try {
            val process = ProcessBuilder(scriptType.executor, scriptFilePath).start()
            val exitCode = process.waitFor()
            process.outputStream.use { }
            val message = readAndCloseInputStream(process.inputStream)
            val errorMessage = readAndCloseInputStream(process.errorStream)
            if (exitCode == 0 && errorMessage.isEmpty()) {
                file.delete()
                log.info("Success to process scriptJob [{}]: message={}", context.jobInstanceId, message)
                return success()
            } else {
                log.warn("Failed to process scriptJob [{}]: errorMessage={}", context.jobInstanceId, errorMessage)
                return failure(errorMessage)
            }
        } catch (e: Exception) {
            return failure("Script execution failed: " + e.message)
        }
    }

    private fun readAndCloseInputStream(inputStream: InputStream): String {
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            reader.readText().trim()
        }
    }

    override val path: String?
        get() = PROCESSOR_PATH
}
