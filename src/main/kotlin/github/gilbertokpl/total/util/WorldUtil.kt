package github.gilbertokpl.total.util

import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.World
import org.bukkit.entity.Item

object WorldUtil {
    fun clearItems() {
        var time = 30000L

        val waitTime = ((time / 3) / 1000)

        TotalEssentials.basePlugin.getTask().async {
            for (i in 0..(time / 10000)) {

                if (time >= 10000) {
                    PlayerUtil.sendAllMessage(
                        LangConfig.ClearitemsMessage.replace(
                            "%time%",
                            TotalEssentials.basePlugin.getTime().convertMillisToString(time, false)
                        )
                    )
                    time -= 10000
                    waitFor(waitTime * 20)
                    continue
                }
                switchContext(SynchronizationContext.SYNC)
                PlayerUtil.sendAllMessage(LangConfig.ClearitemsFinishMessage)

                for (w in MainConfig.ClearitemsWorlds) {
                    val world: World
                    try {
                        world = TotalEssentials.instance.server.getWorld(w)!!
                    } catch (ex: Exception) {
                        continue
                    }
                    val t = world.entities
                    for (e in 0 until t.size) {
                        val i = t[e]
                        if (i is Item && !MainConfig.ClearitemsItemsNotClear.contains(i.itemStack.type.name.lowercase())) {
                            i.remove()
                        }
                    }
                }
            }
        }
    }

}