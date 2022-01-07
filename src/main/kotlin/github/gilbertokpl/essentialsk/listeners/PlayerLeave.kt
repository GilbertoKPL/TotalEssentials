package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.start.PlayerDataLoader
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.DiscordUtil
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PluginUtil
import github.gilbertokpl.essentialsk.util.TaskUtil
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeave : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerQuitEvent) {
        e.quitMessage = null
        if (MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            val playerData = DataManager.getInstance().playerCacheV2[e.player.name.lowercase()] ?: return
            if (!playerData.vanishCache) {
                if (MainConfig.getInstance().messagesLeaveMessage) {
                    PluginUtil.getInstance().serverMessage(
                        GeneralLang.getInstance().messagesLeaveMessage
                            .replace("%player%", e.player.name)
                    )
                }
                if (MainConfig.getInstance().discordbotSendLeaveMessage) {
                    sendLeaveEmbed(e)
                }
            }
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
        try {
            PlayerDataLoader.getInstance().saveCache(e.player)
        } catch (e: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
        }
    }

    private fun setBackLocation(e: PlayerQuitEvent) {
        if (!e.player.hasPermission("essentialsk.commands.back") || MainConfig.getInstance().backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        DataManager.getInstance().playerCacheV2[e.player.name.lowercase()]?.setBack(e.player.location) ?: return
    }

    private fun sendLeaveEmbed(e: PlayerQuitEvent) {
        if (DiscordUtil.getInstance().jda == null) {
            PluginUtil.getInstance().consoleMessage(
                EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoToken + EColor.RESET.color
            )
            return
        }
        if (DataManager.getInstance().discordChat == null) {
            TaskUtil.getInstance().asyncExecutor {
                val newChat =
                    DiscordUtil.getInstance().jda!!.getTextChannelById(MainConfig.getInstance().discordbotIdDiscordChat)
                        ?: run {
                            PluginUtil.getInstance().consoleMessage(
                                EColor.YELLOW.color + GeneralLang.getInstance().discordchatNoChatId + EColor.RESET.color
                            )
                            return@asyncExecutor
                        }
                DataManager.getInstance().discordChat = newChat

                DataManager.getInstance().discordChat!!.sendMessageEmbeds(
                    EmbedBuilder().setDescription(
                        GeneralLang.getInstance().discordchatDiscordSendLeaveMessage.replace("%player%", e.player.name)
                    ).setColor(PluginUtil.getInstance().randomColor()).build()
                ).complete()
            }
            return
        }

        DataManager.getInstance().discordChat!!.sendMessageEmbeds(
            EmbedBuilder().setDescription(
                GeneralLang.getInstance().discordchatDiscordSendLeaveMessage.replace("%player%", e.player.name)
            ).setColor(PluginUtil.getInstance().randomColor()).build()
        ).queue()
    }
}
