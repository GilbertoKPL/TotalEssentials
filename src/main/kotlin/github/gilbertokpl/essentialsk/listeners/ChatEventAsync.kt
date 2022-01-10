package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.TimeUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEventAsync : Listener {
    @EventHandler
    fun event(e: AsyncPlayerChatEvent) {
        if (MainConfig.kitsActivated) {
            try {
                if (editKitChatEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.addonsColorInChat) {
            try {
                colorChat(e)
            } catch (e: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        DataManager.editKitChat[e.player].also {
            if (it == null) return false
            DataManager.editKitChat.remove(e.player)
            val split = it.split("-")

            val dataInstance = KitDataV2[split[1]] ?: return false

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time = TimeUtil.convertStringToMillis(e.message)
                e.player.sendMessage(GeneralLang.generalSendingInfoToDb)
                e.player.sendMessage(
                    GeneralLang.kitsEditKitTime.replace(
                        "%time%",
                        TimeUtil
                            .convertMillisToString(time, MainConfig.kitsUseShortTime)
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
                    e.player.sendMessage(GeneralLang.kitsNameLength)
                    return true
                }

                e.player.sendMessage(GeneralLang.generalSendingInfoToDb)
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

                e.player.sendMessage(GeneralLang.generalSendingInfoToDb)
                dataInstance.setWeight(integer, e.player)
            }

            return true
        }
    }

    private fun colorChat(e: AsyncPlayerChatEvent) {
        e.message = PermissionUtil.colorPermission(e.player, e.message)
    }
}
