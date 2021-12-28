package github.gilbertokpl.essentialsk.events

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.KitData
import github.gilbertokpl.essentialsk.util.FileLoggerUtil
import github.gilbertokpl.essentialsk.util.PermissionUtil
import github.gilbertokpl.essentialsk.util.TimeUtil
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class PlayerAsyncChatEvent : Listener {
    @EventHandler
    fun event(e: AsyncPlayerChatEvent) {
        if (MainConfig.getInstance().kitsActivated) {
            try {
                if (editKitChatEvent(e)) return
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.getInstance().addonsColorInChat) {
            try {
                colorChat(e)
            } catch (e: Exception) {
                FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        Dao.getInstance().editKitChat[e.player].also {
            if (it == null) return false
            Dao.getInstance().editKitChat.remove(e.player)
            val split = it.split("-")

            val dataInstance = KitData(split[1])

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time = TimeUtil.getInstance().convertStringToMillis(e.message)
                e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
                e.player.sendMessage(
                    GeneralLang.getInstance().kitsEditKitTime.replace(
                        "%time%",
                        TimeUtil.getInstance()
                            .convertMillisToString(time, MainConfig.getInstance().kitsUseShortTime)
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
                    e.player.sendMessage(GeneralLang.getInstance().kitsNameLength)
                    return true
                }

                e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
                dataInstance.setFakeName(e.message.replace("&", "§"), e.player)
            }

            //weight
            if (split[0] == "weight") {
                e.isCancelled = true

                val integer = try { e.message.toInt() } catch (e : Exception) { 0 }

                e.player.sendMessage(GeneralLang.getInstance().generalSendingInfoToDb)
                dataInstance.setWeight(integer, e.player)
            }

            return true
        }
    }

    private fun colorChat(e: AsyncPlayerChatEvent) {
        e.message = PermissionUtil.getInstance().colorPermission(e.player, e.message)
    }
}