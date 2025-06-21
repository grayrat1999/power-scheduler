package tech.powerscheduler.server.application.utils

/**
 * @author grayrat
 * @since 2025/5/19
 */
fun randomString(length: Int): String {
    val allowChars = sequenceOf(
        '0'..'9',
        'a'..'z',
        'A'..'Z',
    ).flatten().shuffled().toList()
    return generateSequence { allowChars.random() }
        .take(length)
        .joinToString("")
}