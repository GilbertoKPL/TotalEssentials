package github.gilbertokpl.total.util

import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster

object WorldUtil {

    private var inUse = false
    fun clearEntities() {
        var time = 30000L

        val waitTime = ((time / 3) / 1000)

        TotalEssentialsJava.basePlugin.getTask().async {
            if (inUse) return@async
            inUse = true
            for (a in 0..(time / 10000)) {

                if (time >= 10000) {
                    PlayerUtil.sendAllMessage(
                        LangConfig.ClearitemsMessage.replace(
                            "%time%",
                            TotalEssentialsJava.basePlugin.getTime().convertMillisToString(time, false)
                        )
                    )
                    time -= 10000
                    waitFor(waitTime * 20)
                    continue
                }
                switchContext(SynchronizationContext.SYNC)
                PlayerUtil.sendAllMessage(LangConfig.ClearitemsFinishMessage)

                for (w in MainConfig.clearentitiesWorlds) {
                    val world: World
                    try {
                        world = TotalEssentialsJava.instance.server.getWorld(w)!!
                    } catch (ex: Exception) {
                        continue
                    }
                    val t = world.entities
                    for (e in 0 until t.size) {
                        val i = t[e]
                        if (i is Item && !MainConfig.clearentitiesItemsNotClear.any { it.equals(i.itemStack.type.name.lowercase(), ignoreCase = true) }) {
                            i.remove()
                        }
                        if (i is LivingEntity && i is Monster) {
                            i.remove()
                        }
                    }
                }
            }
            inUse = false
        }
    }

}