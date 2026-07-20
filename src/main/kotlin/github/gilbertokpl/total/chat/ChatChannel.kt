package github.gilbertokpl.total.chat

data class ChatChannel(
    val name: String,
    val enabled: Boolean,
    val color: String,
    val chatColor: String,
    val mutable: Boolean,
    val filter: Boolean,
    val autoJoin: Boolean,
    val default: Boolean,
    val distance: Int,
    val cooldown: Int,
    val network: Boolean,
    val aliases: List<String>,
    val permission: String,
    val speakPermission: String,
    val prefix: String,
    val format: String
) {
    fun canAccess(permissionChecker: (String) -> Boolean): Boolean =
        permission.isBlank() || permissionChecker(permission)

    fun canSpeak(permissionChecker: (String) -> Boolean): Boolean =
        speakPermission.isBlank() || permissionChecker(speakPermission)
}
