package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.DiscordUtil
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import net.milkbowl.vault.chat.Chat
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommandEvent : Listener {

    private val chat = try { EssentialsK.instance.server.servicesManager.getRegistration(Chat::class.java)?.provider } catch (c : NoClassDefFoundError) { null }

    @EventHandler
    fun event(e: PlayerCommandPreprocessEvent) {
        val split = e.message.split(" ")
        if (MainConfig.getInstance().vanishActivated) {
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
        if (MainConfig.getInstance().discordbotConnectDiscordChat) {
            try {
                discordChatEvent(e, split)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockCommands(e: PlayerCommandPreprocessEvent, split: List<String>) {
        val cmd = split[0]
        for (blockedCmd in MainConfig.getInstance().antibugsBlockCmds) {
            if ((blockedCmd == cmd || cmd.split(":").toTypedArray().size > 1 && blockedCmd == "/" + cmd.split(":")
                    .toTypedArray()[1]) &&
                !e.player.hasPermission("essentialsk.bypass.blockedcmd")
            ) {
                e.player.sendMessage(GeneralLang.getInstance().generalNotPerm)
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
                    val p = EssentialsK.instance.server.getPlayer(split[to - 1]) ?: return
                    if (PlayerData(p.name).checkVanish()) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    private fun discordChatEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (MainConfig.getInstance().discordbotCommandChat.contains(split[0].lowercase())) {
            if (DiscordUtil.getInstance().jda == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoToken + EColor.RESET.color
                )
                return
            }
            if (chat == null) {
                PluginUtil.getInstance().consoleMessage(
                    EColor.YELLOW.color + GeneralLang.getInstance().generalVaultNotExist + EColor.RESET.color
                )
                return
            }
            val msg = e.message.replace("${split[0]} ", "")

            val patternMessage = GeneralLang.getInstance().discordchatMessageToDiscordPattern
                .replace("%group%", chat.getPlayerPrefix(e.player))
                .replace("%message%", msg)
                .replace("%player%", e.player.name).replace("&[0-9,a-f]".toRegex(), "")
                .replace("@", "")

            if (Dao.getInstance().discordChat == null) {
                TaskUtil.getInstance().asyncExecutor {
                    val newChat =
                        DiscordUtil.getInstance().jda!!.getTextChannelById(MainConfig.getInstance().discordbotIdDiscordChat)
                            ?: run {
                                PluginUtil.getInstance().consoleMessage(
                                    EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoChatId + EColor.RESET.color
                                )
                                return@asyncExecutor
                            }
                    Dao.getInstance().discordChat = newChat
                    Dao.getInstance().discordChat!!.sendMessage(patternMessage).queue()
                }
                return
            }
            Dao.getInstance().discordChat!!.sendMessage(patternMessage).queue()
        }
    }
}