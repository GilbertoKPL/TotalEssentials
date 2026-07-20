package github.gilbertokpl.total.listeners

import github.gilbertokpl.core.utils.ConsoleColorUtil
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.chat.ChatManager
import github.gilbertokpl.total.config.files.ChatConfig
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.DiscordManager
import github.gilbertokpl.total.util.PlayerUtil.getMojangSkinURL
import github.gilbertokpl.total.util.PermissionUtil
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommand : Listener {

    private val chat = try {
        TotalEssentials.getInstance().server.servicesManager.getRegistration(Chat::class.java)?.provider
    } catch (e: NoClassDefFoundError) {
        null
    }

    companion object {
        private val LOGIN_COMMANDS = setOf("/login", "/logar", "/register", "/registrar")
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val args = event.message.split(" ")
        val command = args.firstOrNull() ?: return

        // Bloqueia comandos se o jogador não estiver logado
        if (!LoginData.isPlayerLoggedIn(event.player) && command !in LOGIN_COMMANDS) {
            event.isCancelled = true
            return
        }

        blockCommands(event, command)
        if (event.isCancelled || handlePrivateMessageCommand(event, args)) return

        if (MainConfig.discordbotConnectDiscordChat) {
            TotalEssentials.getCore().getTask().async {
                discordChatEvent(event, args)
            }
        }
    }

    private fun handlePrivateMessageCommand(event: PlayerCommandPreprocessEvent, args: List<String>): Boolean {
        if (!ChatConfig.tellActivated) return false
        val label = args.firstOrNull()
            ?.removePrefix("/")
            ?.substringAfter(':')
            ?.lowercase()
            ?: return false
        val tellLabels = ChatConfig.tellAliases.map { it.lowercase() }.toSet() + "tell"
        val replyLabels = ChatConfig.tellReplyAliases.map { it.lowercase() }.toSet() + "reply"

        when (label) {
            in tellLabels -> {
                event.isCancelled = true
                if (!hasConfiguredPermission(event.player, ChatConfig.tellPermission)) {
                    event.player.sendMessage(LangConfig.generalNotPerm)
                } else if (args.size < 3) {
                    event.player.sendMessage(LangConfig.tellUsage)
                } else {
                    ChatManager.sendPrivateMessage(
                        event.player,
                        args[1],
                        args.drop(2).joinToString(" "),
                        false
                    )
                }
                return true
            }
            in replyLabels -> {
                event.isCancelled = true
                if (!hasConfiguredPermission(event.player, ChatConfig.tellReplyPermission)) {
                    event.player.sendMessage(LangConfig.generalNotPerm)
                } else if (args.size < 2) {
                    event.player.sendMessage(LangConfig.tellReplyUsage)
                } else {
                    ChatManager.sendPrivateMessage(
                        event.player,
                        null,
                        args.drop(1).joinToString(" "),
                        true
                    )
                }
                return true
            }
        }
        return false
    }

    private fun hasConfiguredPermission(player: org.bukkit.entity.Player, permission: String): Boolean {
        if (permission.isBlank() || permission.equals("none", true)) return true
        return PermissionUtil.hasPermission(player, permission)
    }

    private fun blockCommands(event: PlayerCommandPreprocessEvent, command: String) {
        if (event.player.hasPermission("totalessentials.bypass.blockedcmd")) return

        val commandToCheck = extractCommand(command)

        if (MainConfig.antibugsBlockCmds.contains(commandToCheck)) {
            event.player.sendMessage(LangConfig.generalNotPerm)
            event.isCancelled = true
        }
    }

    private fun extractCommand(command: String): String {
        // Extrai o comando real se houver plugin prefix (ex: "essentials:give" -> "/give")
        val parts = command.split(":")
        return if (parts.size > 1) "/${parts[1]}" else command
    }

    private fun discordChatEvent(event: PlayerCommandPreprocessEvent, args: List<String>) {
        val command = args.firstOrNull()?.lowercase() ?: return

        if (command !in MainConfig.discordbotCommandChat) return
        if (chat == null) {
            logVaultNotFound()
            return
        }

        val message = extractMessage(event.message, command)
        if (message.isBlank()) return

        sendToDiscord(event.player.name, message)
    }

    private fun logVaultNotFound() {
        Bukkit.getConsoleSender().sendMessage(
            "${ConsoleColorUtil.YELLOW.color}${LangConfig.generalVaultNotExist}${ConsoleColorUtil.RESET.color}"
        )
    }

    private fun extractMessage(fullMessage: String, command: String): String {
        return fullMessage
            .removePrefix("$command ")
            .replace(Regex("[@*#`]"), "")
            .trim()
    }

    private fun sendToDiscord(playerName: String, message: String) {
        val player = Bukkit.getPlayerExact(playerName) ?: return

        val formattedName = LangConfig.discordchatMessageToDiscordNamePattern
            .replace("%group%", chat?.getPlayerPrefix(player) ?: "")
            .replace("%player%", playerName)
            .replace(Regex("&[0-9a-z]"), "")

        val formattedMessage = LangConfig.discordchatMessageToDiscordNewPattern
            .replace("%message%", message)

        DiscordManager.sendPlayerWebhook(formattedName, getMojangSkinURL(player), formattedMessage)
    }
}
