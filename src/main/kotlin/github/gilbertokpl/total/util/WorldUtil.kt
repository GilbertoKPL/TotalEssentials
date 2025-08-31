package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.plugin.Plugin

object WorldUtil {

    private var inUse = false

    fun clearEntities() {
        var time = 30000L
        val waitTime = ((time / 3) / 1000)

        val task = TotalEssentials.getCore().getTask()

        task.async {
            if (inUse) return@async
            inUse = true

            for (a in 0..(time / 10000)) {
                if (time >= 10000) {
                    PlayerUtil.sendAllMessage(
                        LangConfig.ClearitemsMessage.replace(
                            "%time%",
                            TotalEssentials.getCore().getTime().convertMillisToString(time, false)
                        )
                    )
                    time -= 10000
                    task.waitSeconds(waitTime)
                    continue
                }

                task.sync {
                    PlayerUtil.sendAllMessage(LangConfig.ClearitemsFinishMessage)
                    clearWorldEntities(TotalEssentials.getCore().plugin)
                }
            }
            inUse = false
        }
    }

    private fun clearWorldEntities(plugin: Plugin) {
        val server = TotalEssentials.getInstance().server

        val regionSchedulerMethod = try {
            Bukkit::class.java.getMethod("getRegionScheduler")
        } catch (_: NoSuchMethodException) {
            null
        }

        for (w in MainConfig.clearentitiesWorlds) {
            val world: World = server.getWorld(w) ?: continue
            val entities = world.entities.toList()

            for (entity in entities) {
                if (regionSchedulerMethod != null) {
                    val regionScheduler = regionSchedulerMethod.invoke(null)
                    val execute = regionScheduler.javaClass.getMethod(
                        "execute",
                        Plugin::class.java,
                        org.bukkit.Location::class.java,
                        Runnable::class.java
                    )

                    execute.invoke(regionScheduler, plugin, entity.location, Runnable {
                        if (entity is Item && !MainConfig.clearentitiesItemsNotClear.any {
                                it.equals(entity.itemStack.type.name.lowercase(), ignoreCase = true)
                            }) {
                            entity.remove()
                        }
                        if (entity is LivingEntity && entity is Monster) {
                            entity.remove()
                        }
                    })
                } else {
                    if (entity is Item && !MainConfig.clearentitiesItemsNotClear.any {
                            it.equals(entity.itemStack.type.name.lowercase(), ignoreCase = true)
                        }) {
                        entity.remove()
                    }
                    if (entity is LivingEntity && entity is Monster) {
                        entity.remove()
                    }
                }
            }
        }
    }

}
