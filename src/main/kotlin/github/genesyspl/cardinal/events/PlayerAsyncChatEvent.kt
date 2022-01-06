package github.genesyspl.cardinal.events

import github.genesyspl.cardinal.data.DataManager
import github.genesyspl.cardinal.util.FileLoggerUtil
import github.genesyspl.cardinal.util.PermissionUtil
import github.genesyspl.cardinal.util.TimeUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerAsyncChatEvent : Listener {
    @EventHandler
    fun event(e: AsyncPlayerChatEvent) {
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().kitsActivated) {
            try {
                if (editKitChatEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (github.genesyspl.cardinal.configs.MainConfig.getInstance().addonsColorInChat) {
            try {
                colorChat(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        DataManager.getInstance().editKitChat[e.player].also {
            if (it == null) return false
            DataManager.getInstance().editKitChat.remove(e.player)
            val split = it.split("-")

            val dataInstance = DataManager.getInstance().kitCacheV2[split[1]]!!

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time = TimeUtil.getInstance().convertStringToMillis(e.message)
                e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
                e.player.sendMessage(
                    github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsEditKitTime.replace(
                        "%time%",
                        TimeUtil.getInstance()
                            .convertMillisToString(
                                time,
                                github.genesyspl.cardinal.configs.MainConfig.getInstance().kitsUseShortTime
                            )
                    )
                )
                dataInstance.setTime(time, e.player)

                return true
            }

            //name
            if (split[0] == "name") {
                e.isCancelled = true
                //check message length
                if (e.message.replace("&[0-9,a-f]".toRegex(), "").length > 16) {
                    e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().kitsNameLength)
                    return true
                }

                e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
                dataInstance.setFakeName(e.message.replace("&", "§"), e.player)
            }

            //weight
            if (split[0] == "weight") {
                e.isCancelled = true

                val integer = try {
                    e.message.toInt()
                } catch (e: Exception) {
                    0
                }

                e.player.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalSendingInfoToDb)
                dataInstance.setWeight(integer, e.player)
            }

            return true
        }
    }

    private fun colorChat(e: AsyncPlayerChatEvent) {
        e.message = PermissionUtil.getInstance().colorPermission(e.player, e.message)
    }
}