package github.gilbertokpl.essentialsk.listeners

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.data.dao.KitData
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
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
        if (MainConfig.addonsColorInChat) {
            try {
                colorChat(e)
            } catch (e: Throwable) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(e))
            }
        }
    }

    private fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        val p = e.player

        DataManager.editKitChat[p].also {
            if (it == null) return false
            DataManager.editKitChat.remove(p)
            val split = it.split("-")

            val dataInstance = KitData[split[1]] ?: return false

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time = TimeUtil.convertStringToMillis(e.message)
                p.sendMessage(
                    GeneralLang.kitsEditKitTime.replace(
                        "%time%",
                        TimeUtil
                            .convertMillisToString(time, MainConfig.kitsUseShortTime)
                    )
                )
                dataInstance.setTime(time)
                p.sendMessage(GeneralLang.kitsEditKitSuccess.replace("%kit%", dataInstance.kitNameCache))

                return true
            }

            //name
            if (split[0] == "name") {
                e.isCancelled = true
                //check message length
                if (e.message.replace("&|§([0-9]|[a-f])".toRegex(), "").length > 16) {
                    p.sendMessage(GeneralLang.kitsNameLength)
                    return true
                }

                val newName = e.message.replace("&", "§")

                dataInstance.setFakeName(newName)
                p.sendMessage(GeneralLang.kitsEditKitSuccess.replace("%kit%", dataInstance.kitNameCache))
            }

            //weight
            if (split[0] == "weight") {
                e.isCancelled = true

                val integer = try {
                    e.message.toInt()
                } catch (e: Throwable) {
                    0
                }

                dataInstance.setWeight(integer)
                p.sendMessage(GeneralLang.kitsEditKitSuccess.replace("%kit%", dataInstance.kitNameCache))
            }

            return true
        }
    }

    private fun colorChat(e: AsyncPlayerChatEvent) {
        e.message = PermissionUtil.colorPermission(e.player, e.message)
    }
}
