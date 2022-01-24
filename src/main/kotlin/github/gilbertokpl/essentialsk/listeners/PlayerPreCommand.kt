package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.api.DiscordAPI
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.data.dao.PlayerDataDAO
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.MainUtil
import net.milkbowl.vault.chat.Chat
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommand : Listener {

    private val chat = try {
        EssentialsK.instance.server.servicesManager.getRegistration(Chat::class.java)?.provider
    } catch (c: NoClassDefFoundError) {
        null
    }

    @EventHandler
    fun event(e: PlayerCommandPreprocessEvent) {
        val split = e.message.split(" ")
        if (MainConfig.vanishActivated) {
            try {
                vanishPreCommandEvent(e, split)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            blockCommands(e, split)
        } catch (e: Throwable) {
            FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
        }
        if (MainConfig.discordbotConnectDiscordChat) {
            try {
                discordChatEvent(e, split)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun blockCommands(e: PlayerCommandPreprocessEvent, split: List<String>) {
        val cmd = split[0]
        for (blockedCmd in MainConfig.antibugsBlockCmds) {
            if ((blockedCmd == cmd || cmd.split(":").toTypedArray().size > 1 && blockedCmd == "/" + cmd.split(":")
                    .toTypedArray()[1]) &&
                !e.player.hasPermission("essentialsk.bypass.blockedcmd")
            ) {
                e.player.sendMessage(GeneralLang.generalNotPerm)
                e.isCancelled = true
                return
            }
        }
    }

    private fun vanishPreCommandEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (split.isEmpty()) {
            return
        }
        OtherConfig.vanishBlockedOtherCmds.also {
            if (it.containsKey(split[0])) {
                val to = it[split[0]]
                if (split.size >= to!!) {
                    val p = EssentialsK.instance.server.getPlayer(split[to - 1]) ?: return
                    if (PlayerDataDAO[p]?.vanishCache ?: return) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    private fun discordChatEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (MainConfig.discordbotCommandChat.contains(split[0].lowercase())) {
            if (chat == null) {
                MainUtil.consoleMessage(
                    EColor.YELLOW.color + GeneralLang.generalVaultNotExist + EColor.RESET.color
                )
                return
            }
            val msg = e.message.replace("${split[0]} ", "")
                .replace("@", "")
                .replace("*", "")
                .replace("#", "")
                .replace("`", "")

            if (msg == "/g" || msg == "") return

            val patternMessage = GeneralLang.discordchatMessageToDiscordPattern
                .replace("%group%", chat.getPlayerPrefix(e.player))
                .replace("%message%", msg)
                .replace("%player%", e.player.name).replace("&[0-9,a-z]".toRegex(), "")

            DiscordAPI.sendMessageDiscord(patternMessage, false)
        }
    }
}
