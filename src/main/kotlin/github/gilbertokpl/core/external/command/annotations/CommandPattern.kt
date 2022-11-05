package github.gilbertokpl.core.external.command.annotations

import github.gilbertokpl.core.external.command.CommandTarget

data class CommandPattern(
    val aliases: List<String>,
    val target: CommandTarget,
    val active: Boolean,
    val permission: String,
    val usage: List<String>,
    val countdown: Long,
    val minimumSize: Int,
    val maximumSize: Int?
)
