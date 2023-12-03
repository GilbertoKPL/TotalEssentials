package github.gilbertokpl.total.listeners

import github.gilbertokpl.total.cache.internal.Data
import github.gilbertokpl.total.cache.internal.inventory.Kit
import github.gilbertokpl.total.cache.local.KitsData
import github.gilbertokpl.total.cache.local.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEventAsync : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun event(e: AsyncPlayerChatEvent) {

        if (!LoginData.isPlayerLoggedIn(e.player)) {
            e.isCancelled = true
            return
        }

        if (MainConfig.kitsActivated) {
            try {
                if (editKitChatEvent(e)) return
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun editKitChatEvent(e: AsyncPlayerChatEvent): Boolean {
        val p = e.player

        Data.playerEditKitChat[p].also {
            if (it == null) return false
            Data.playerEditKitChat.remove(p)
            val split = it.split("-")

            //time
            if (split[0] == "time") {
                e.isCancelled = true
                val time =
                    github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getTime().convertStringToMillis(e.message)
                p.sendMessage(
                    LangConfig.kitsEditKitTime.replace(
                        "%time%",
                        github.gilbertokpl.total.TotalEssentialsJava.basePlugin.getTime()
                            .convertMillisToString(
                                time,
                                MainConfig.kitsUseShortTime
                            )
                    )
                )
                KitsData.kitTime[split[1]] = time
                p.sendMessage(
                    LangConfig.kitsEditKitSuccess.replace(
                        "%kit%",
                        split[1]
                    )
                )

                return true
            }

            //name
            if (split[0] == "name") {
                e.isCancelled = true
                //check message length
                if (e.message.replace("&|§([0-9]|[a-f])".toRegex(), "").length > 16) {
                    p.sendMessage(LangConfig.kitsNameLength)
                    return true
                }

                val newName = e.message.replace("&", "§")

                KitsData.kitFakeName[split[1]] = newName
                p.sendMessage(
                    LangConfig.kitsEditKitSuccess.replace(
                        "%kit%",
                        split[1]
                    )
                )

                Kit.setup()
            }

            //weight
            if (split[0] == "weight") {
                e.isCancelled = true

                val integer = try {
                    e.message.toInt()
                } catch (e: Throwable) {
                    0
                }

                KitsData.kitWeight[split[1]] = integer
                p.sendMessage(
                    LangConfig.kitsEditKitSuccess.replace(
                        "%kit%",
                        split[1]
                    )
                )

                Kit.setup()
            }

            return true
        }
    }
}
