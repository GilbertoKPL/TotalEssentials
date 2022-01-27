package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.okkero.skedule.SynchronizationContext
import github.okkero.skedule.schedule
import org.apache.commons.lang3.exception.ExceptionUtils
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

    fun disable() {
        poolExecutor.shutdown()
        poolExecutorTeleport.shutdown()
        announceExecutor.shutdown()
        discordExecutor.shutdown()
    }

    fun getDiscordExecutor(): ScheduledExecutorService {
        return discordExecutor
    }

    fun restartDiscordExecutor() {
        discordExecutor.shutdownNow()
        discordExecutor = Executors.newSingleThreadScheduledExecutor()
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
            scheduler.schedule(EssentialsK.instance, SynchronizationContext.ASYNC) {
                waitFor(time.toLong() * 20)
                try {
                    switchContext(SynchronizationContext.SYNC)
                    it()
                } catch (ex: Throwable) {
                    FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
                }
            }
        }
    }
}
