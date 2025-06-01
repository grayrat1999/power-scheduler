package tech.powerscheduler.worker.util

import java.io.InputStream

/**
 * classpath文件读取工具
 *
 * @author grayrat
 * @since 2025/5/25
 */
object ClasspathUtil {

    @JvmStatic
    fun getInputStream(fileName: String): InputStream {
        return ClasspathUtil::class.java.classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("file not in classpath: $fileName")
    }

    @JvmStatic
    fun readTextFrom(fileName: String): String {
        val inputStream = ClasspathUtil::class.java.classLoader.getResourceAsStream(fileName)
            ?: throw IllegalArgumentException("file not in classpath: $fileName")
        return inputStream.bufferedReader().use { it.readText() }
    }

}