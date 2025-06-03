package tech.powerscheduler.common.enums

import tech.powerscheduler.common.annotation.Metadata

/**
 * 脚本类型枚举
 *
 * @author grayrat
 * @since 2025/5/18
 */
@Metadata(label = "脚本类型", code = "ScriptTypeEnum")
enum class ScriptTypeEnum(
    override val label: String,
    val executor: String,
    val suffix: String,
) : BaseEnum {
    BASH(
        "Bash",
        "bash",
        ".sh"
    ),
    CMD(
        "Cmd",
        "cmd",
        ".bat"
    ),
    PYTHON(
        "Python",
        "python",
        ".py"
    ),
    POWER_SHELL(
        "PowerShell",
        "pwsh",
        ".ps1"
    ),
    ;

    override val code = this.name

    companion object {
        fun findByName(name: String?): ScriptTypeEnum? {
            return entries.firstOrNull { it.name == name }
        }
    }
}