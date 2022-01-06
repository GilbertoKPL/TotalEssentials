package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.data.start.PlayerDataLoader
import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.util.DiscordUtil
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PluginUtil
import github.genesyspl.cardinal.util.TaskUtil
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerLeaveEvent : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun event(e: PlayerQuitEvent) {
        e.quitMessage = null
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().backActivated) {
            try {
                setBackLocation(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        try {
            val playerData = DataManager.getInstance().playerCacheV2[e.player.name.lowercase()] ?: return
            if (!playerData.vanishCache) {
                if (github.genesyspl.cardinal.configs.MainConfig.getInstance().messagesLeaveMessage) {
                    PluginUtil.getInstance().serverMessage(
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().messagesLeaveMessage
                            .replace("%player%", e.player.name)
                    )
                }
                if (github.genesyspl.cardinal.configs.MainConfig.getInstance().discordbotSendLeaveMessage) {
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
        if (!e.player.hasPermission("cardinal.commands.back") || github.genesyspl.cardinal.configs.MainConfig.getInstance().backDisabledWorlds.contains(
                e.player.world.name.lowercase()
            )
        ) return
        DataManager.getInstance().playerCacheV2[e.player.name.lowercase()]?.setBack(e.player.location) ?: return
    }

    private fun sendLeaveEmbed(e: PlayerQuitEvent) {
        if (DiscordUtil.getInstance().jda == null) {
            PluginUtil.getInstance().consoleMessage(
                EColor.YELLOW.color + github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatNoToken + EColor.RESET.color
            )
            return
        }
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

                DataManager.getInstance().discordChat!!.sendMessageEmbeds(
                    EmbedBuilder().setDescription(
                        github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatDiscordSendLeaveMessage.replace(
                            "%player%",
                            e.player.name
                        )
                    ).setColor(PluginUtil.getInstance().randomColor()).build()
                ).complete()
            }
            return
        }

        DataManager.getInstance().discordChat!!.sendMessageEmbeds(
            EmbedBuilder().setDescription(
                github.genesyspl.cardinal.configs.GeneralLang.getInstance().discordchatDiscordSendLeaveMessage.replace(
                    "%player%",
                    e.player.name
                )
            ).setColor(PluginUtil.getInstance().randomColor()).build()
        ).queue()
    }
}