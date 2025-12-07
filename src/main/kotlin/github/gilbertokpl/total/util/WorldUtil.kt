package github.gilbertokpl.total.util

import github.gilbertokpl.core.task.TaskManager
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.LangConfig.soundClearentities
import github.gilbertokpl.total.config.files.MainConfig
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.plugin.Plugin

object WorldUtil {

    private const val COUNTDOWN_DURATION_MS = 30000L
    private const val COUNTDOWN_INTERVAL_MS = 10000L

    @Volatile
    private var clearInProgress = false

    // Cache para RegionScheduler (Folia/Paper moderno)
    private val regionSchedulerSupport by lazy {
        RegionSchedulerSupport()
    }

    fun clearEntities() {
        if (clearInProgress) return

        val task = TotalEssentials.getCore().getTask()

        task.async {
            clearInProgress = true

            try {
                runCountdown(task)
            } finally {
                clearInProgress = false
            }
        }
    }

    private suspend fun runCountdown(task: TaskManager) {
        val timeUtil = TotalEssentials.getCore().getTime()
        var remainingTime = COUNTDOWN_DURATION_MS

        while (remainingTime > 0) {

            // Sempre main thread:
            task.sync {
                val formattedTime = timeUtil.convertMillisToString(remainingTime, false)
                PlayerUtil.sendAllAction(
                    LangConfig.ClearitemsMessage.replace("%time%", formattedTime),
                )
                PlayerUtil.sendAllSound(soundClearentities)
            }

            delay(COUNTDOWN_INTERVAL_MS)
            remainingTime -= COUNTDOWN_INTERVAL_MS
        }

        // Finalização também na main thread:
        task.sync {
            PlayerUtil.sendAllAction(LangConfig.ClearitemsFinishMessage,)
            PlayerUtil.sendAllSound(soundClearentities)
            clearWorldEntities()
        }
    }

    private fun clearWorldEntities() {
        val server = TotalEssentials.getInstance().server

        for (worldName in MainConfig.clearentitiesWorlds) {
            val world = server.getWorld(worldName) ?: continue
            clearEntitiesInWorld(world)
        }
    }

    private fun clearEntitiesInWorld(world: World) {
        val entities = world.entities.toList()

        for (entity in entities) {
            if (regionSchedulerSupport.isAvailable) {
                scheduleEntityRemoval(entity)
            } else {
                removeEntityIfNeeded(entity)
            }
        }
    }

    private fun scheduleEntityRemoval(entity: org.bukkit.entity.Entity) {
        regionSchedulerSupport.execute(entity.location) {
            removeEntityIfNeeded(entity)
        }
    }

    private fun removeEntityIfNeeded(entity: org.bukkit.entity.Entity) {
        when (entity) {
            is Item -> {
                if (shouldRemoveItem(entity)) {
                    entity.remove()
                }
            }
            is Monster -> {
                entity.remove()
            }
        }
    }

    private fun shouldRemoveItem(item: Item): Boolean {
        val itemTypeName = item.itemStack.type.name.lowercase()

        return !MainConfig.clearentitiesItemsNotClear.any {
            it.equals(itemTypeName, ignoreCase = true)
        }
    }

    // Classe interna para gerenciar RegionScheduler (Folia/Paper)
    private class RegionSchedulerSupport {
        val isAvailable: Boolean
        private val regionScheduler: Any?
        private val executeMethod: java.lang.reflect.Method?

        init {
            var available = false
            var scheduler: Any? = null
            var method: java.lang.reflect.Method? = null

            try {
                val getSchedulerMethod = Bukkit::class.java.getMethod("getRegionScheduler")
                scheduler = getSchedulerMethod.invoke(null)

                method = scheduler?.javaClass?.getMethod(
                    "execute",
                    Plugin::class.java,
                    org.bukkit.Location::class.java,
                    Runnable::class.java
                )

                available = scheduler != null && method != null
            } catch (e: Exception) {
                // RegionScheduler não disponível (versões antigas)
            }

            isAvailable = available
            regionScheduler = scheduler
            executeMethod = method
        }

        fun execute(location: org.bukkit.Location, task: Runnable) {
            if (!isAvailable || regionScheduler == null || executeMethod == null) return

            try {
                executeMethod.invoke(
                    regionScheduler,
                    TotalEssentials.getCore().plugin,
                    location,
                    task
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}