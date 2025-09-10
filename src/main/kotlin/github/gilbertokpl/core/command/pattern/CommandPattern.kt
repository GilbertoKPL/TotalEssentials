package github.gilbertokpl.core.command.pattern

import github.gilbertokpl.core.command.type.CommandTargetType

data class CommandPattern(
    val aliases: List<String>,
    val target: CommandTargetType,
    val active: Boolean,
    val permission: String,
    val usage: List<String>,
    val countdown: Long,
    val minimumSize: Int,
    val maximumSize: Int?
)
