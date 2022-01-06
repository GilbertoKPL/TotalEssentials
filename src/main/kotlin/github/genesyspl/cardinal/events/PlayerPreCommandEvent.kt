package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.configs.OtherConfig
import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.util.DiscordUtil
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PluginUtil
import github.genesyspl.cardinal.util.TaskUtil
import net.milkbowl.vault.chat.Chat
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommandEvent : Listener {

    private val chat = try {
        github.genesyspl.cardinal.Cardinal.instance.server.servicesManager.getRegistration(Chat::class.java)?.provider
    } catch (c: NoClassDefFoundError) {
        null
    }

    @EventHandler
    fun event(e: PlayerCommandPreprocessEvent) {
        val split = e.message.split(" ")
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().vanishActivated) {
            try {
                vanishPreCommandEvent(e, split)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            blockCommands(e, split)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotConnectDiscordChat) {
            try {
                discordChatEvent(e, split)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockCommands(e: PlayerCommandPreprocessEvent, split: List<String>) {
        val cmd = split[0]
        for (blockedCmd in github.genesyspl.cardinal.configs.MainConfig.getInstance().antibugsBlockCmds) {
            if ((blockedCmd == cmd || cmd.split(":").toTypedArray().size > 1 && blockedCmd == "/" + cmd.split(":")
                    .toTypedArray()[1]) &&
                !e.player.hasPermission("cardinal.bypass.blockedcmd")
            ) {
                e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalNotPerm)
                e.isCancelled = true
                return
            }
        }
    }

    private fun vanishPreCommandEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (split.isEmpty()) {
            return
        }
        OtherConfig.getInstance().vanishBlockedOtherCmds.also {
            if (it.containsKey(split[0])) {
                val to = it[split[0]]
                if (split.size >= to!!) {
                    val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(split[to - 1]) ?: return
                    if (DataManager.getInstance().playerCacheV2[p.name.lowercase()]!!.vanishCache) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    private fun discordChatEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotCommandChat.contains(split[0].lowercase())) {
            if (DiscordUtil.getInstance().jda == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatNoToken + EColor.RESET.color
                )
                return
            }
            if (chat == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalVaultNotExist + EColor.RESET.color
                )
                return
            }
            val msg = e.message.replace("${split[0]} ", "")

            val patternMessage =
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatMessageToDiscordPattern
                    .replace("%group%", chat.getPlayerPrefix(e.player))
                    .replace("%message%", msg)
                    .replace("%player%", e.player.name).replace("&[0-9,a-z]".toRegex(), "")
                    .replace("@", "")

            if (DataManager.getInstance().discordChat == null) {
                TaskUtil.getInstance().asyncExecutor {
                    val newChat =
                        DiscordUtil.getInstance().jda!!.getTextChannelById(github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotIdDiscordChat)
                            ?: run {
                                PluginUtil.getInstance().consoleMessage(
                                    EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatNoChatId + EColor.RESET.color
                                )
                                return@asyncExecutor
                            }
                    DataManager.getInstance().discordChat = newChat
                    DataManager.getInstance().discordChat!!.sendMessage(patternMessage).queue()
                }
                return
            }
            DataManager.getInstance().discordChat!!.sendMessage(patternMessage).queue()
        }
    }
}