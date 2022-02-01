package github.gilbertokpl.essentialsk.manager

data class CommandData(
    val active: Boolean,
    val timeCoolDown: Long?,
    val commandName: String,
    val permission: String?,
    val consoleCanUse: Boolean,
    val commandUsage: List<String>,
    val minimumSize: Int?,
    val maximumSize: Int?
)
