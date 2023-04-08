package github.gilbertokpl.total.listeners

import br.com.devpaulo.legendchat.mutes.Mute
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.ColorUtil
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class PlayerPreCommand : Listener {

    private val chat = try {
        github.gilbertokpl.total.TotalEssentials.instance.server.servicesManager.getRegistration(Chat::class.java)?.provider
    } catch (c: NoClassDefFoundError) {
        null
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: PlayerCommandPreprocessEvent) {
        val split = e.message.split(" ")

        if (!LoginData.isPlayerLoggedIn(e.player) && split[0] != "/login" && split[0] != "/logar" && split[0] != "/register" && split[0] != "/registrar") {
            e.isCancelled = true
            return
        }

        if (MainConfig.vanishActivated) {
            try {
                vanishPreCommandEvent(e, split)
            } catch (e: Throwable) {

            }
        }
        try {
            blockCommands(e, split)
        } catch (e: Throwable) {

        }
        if (MainConfig.discordbotConnectDiscordChat) {
            try {
                discordChatEvent(e, split)
            } catch (e: Throwable) {

            }
        }
    }

    private fun blockCommands(e: PlayerCommandPreprocessEvent, split: List<String>) {
        val cmd = split[0]
        for (blockedCmd in MainConfig.antibugsBlockCmds) {
            if ((blockedCmd == cmd || cmd.split(":").toTypedArray().size > 1 && blockedCmd == "/" + cmd.split(":")
                    .toTypedArray()[1]) &&
                !e.player.hasPermission("totalessentials.bypass.blockedcmd")
            ) {
                e.player.sendMessage(LangConfig.generalNotPerm)
                e.isCancelled = true
                return
            }
        }
    }

    private fun vanishPreCommandEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (split.isEmpty()) {
            return
        }
    }

    private fun discordChatEvent(e: PlayerCommandPreprocessEvent, split: List<String>) {
        if (MainConfig.discordbotCommandChat.contains(split[0].lowercase())) {

            val bol = try {
                br.com.devpaulo.legendchat.mutes.MuteManager().isPlayerMuted(e.player.name)
            } catch (e: NoClassDefFoundError) {
                false
            }

            if (bol) return

            if (chat == null) {
                Bukkit.getConsoleSender().sendMessage(
                    ColorUtil.YELLOW.color + LangConfig.generalVaultNotExist + ColorUtil.RESET.color
                )
                return
            }
            val msg = e.message.replace("${split[0]} ", "")
                .replace("@", "")
                .replace("*", "")
                .replace("#", "")
                .replace("`", "")

            if (msg == "/g" || msg == "") return

            val patternMessage =
                LangConfig.discordchatMessageToDiscordPattern
                    .replace("%group%", chat.getPlayerPrefix(e.player))
                    .replace("%message%", msg)
                    .replace("%player%", e.player.name).replace("&[0-9,a-z]".toRegex(), "")

            Discord.sendDiscordMessage(patternMessage, false)
        }
    }
}
