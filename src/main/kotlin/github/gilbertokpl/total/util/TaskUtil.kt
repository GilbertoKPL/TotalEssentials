package github.gilbertokpl.total.util

import github.gilbertokpl.core.external.task.SynchronizationContext
import org.bukkit.Bukkit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

internal object TaskUtil {

    private val scheduler = Bukkit.getScheduler()

    private val poolExecutor = Executors.newCachedThreadPool()

    private val poolExecutorTeleport = Executors.newCachedThreadPool()

    private var announceExecutor = Executors.newSingleThreadScheduledExecutor()

    private var discordExecutor = Executors.newSingleThreadScheduledExecutor()

    private var dataExecutor = Executors.newSingleThreadScheduledExecutor()

    private var moneyExecutor = Executors.newSingleThreadScheduledExecutor()

    fun disable() {
        poolExecutor.shutdown()
        poolExecutorTeleport.shutdown()
        announceExecutor.shutdown()
        discordExecutor.shutdown()
        dataExecutor.shutdown()
        moneyExecutor.shutdown()
    }

    fun getMoneyExecutor(): ScheduledExecutorService {
        return moneyExecutor
    }

    fun getDiscordExecutor(): ScheduledExecutorService {
        return discordExecutor
    }

    fun restartDiscordExecutor() {
        discordExecutor.shutdownNow()
        discordExecutor = Executors.newSingleThreadScheduledExecutor()
    }

    fun getDataExecutor(): ScheduledExecutorService {
        return dataExecutor
    }

    fun restartDataExecutor() {
        dataExecutor.shutdownNow()
        dataExecutor = Executors.newSingleThreadScheduledExecutor()
    }

    fun getAnnounceExecutor(): ScheduledExecutorService {
        return announceExecutor
    }

    fun restartAnnounceExecutor() {
        announceExecutor.shutdownNow()
        announceExecutor = Executors.newSingleThreadScheduledExecutor()
    }

    fun getTeleportExecutor(): ExecutorService {
        return poolExecutorTeleport
    }

    fun getExecutor(): ExecutorService {
        return poolExecutor
    }

    fun teleportExecutor(time: Int): (() -> Unit) -> Unit {
        return {
            github.gilbertokpl.total.TotalEssentials.basePlugin.getTask().async {
                waitFor(time.toLong() * 20)
                try {
                    switchContext(SynchronizationContext.SYNC)
                    it()
                } catch (ex: Throwable) {

                }
            }
        }
    }
}
