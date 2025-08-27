package github.gilbertokpl.core.external.task

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method
import java.util.function.Consumer
import kotlin.coroutines.CoroutineContext

class Task(private val plugin: Plugin) : CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Default

    private val foliaGlobalScheduler: Any?
    private val foliaGlobalRun: Method?
    private val foliaAsyncScheduler: Any?
    private val foliaAsyncRunNow: Method?

    init {
        val server = plugin.server
        val serverClass = server::class.java

        var gScheduler: Any? = null
        var gRun: Method? = null
        var aScheduler: Any? = null
        var aRunNow: Method? = null

        try {
            val gInstance = serverClass.getMethod("getGlobalRegionScheduler").invoke(server)
            gScheduler = gInstance
            gRun = gInstance.javaClass.getMethod("run", Plugin::class.java, Consumer::class.java)
        } catch (_: Exception) {
        }

        try {
            val aInstance = serverClass.getMethod("getAsyncScheduler").invoke(server)
            aScheduler = aInstance
            aRunNow = aInstance.javaClass.getMethod("runNow", Plugin::class.java, Consumer::class.java)
        } catch (_: Exception) {
        }

        foliaGlobalScheduler = gScheduler
        foliaGlobalRun = gRun
        foliaAsyncScheduler = aScheduler
        foliaAsyncRunNow = aRunNow
    }

    /** Executa no main/region thread seguro */
    fun sync(block: suspend CoroutineScope.() -> Unit) {
        if (foliaGlobalScheduler != null && foliaGlobalRun != null) {
            // Folia: roda direto na mesma thread do scheduler
            foliaGlobalRun.invoke(foliaGlobalScheduler, plugin, Consumer<Any?> {
                runBlocking { block() } // não muda de thread
            })
        } else {
            // Paper/Spigot: main thread
            Bukkit.getScheduler().runTask(plugin, Runnable {
                runBlocking { block() }
            })
        }
    }

    /** Executa async seguro */
    fun async(block: suspend CoroutineScope.() -> Unit) {
        if (foliaAsyncScheduler != null && foliaAsyncRunNow != null) {
            // Folia async scheduler
            foliaAsyncRunNow.invoke(foliaAsyncScheduler, plugin, Consumer<Any?> {
                launch { block() } // pode usar coroutine aqui
            })
        } else {
            // Paper/Spigot async
            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                launch { block() }
            })
        }
    }

    /** Delay em ticks (50ms por tick) */
    suspend fun waitTicks(ticks: Long) {
        delay(ticks * 50L)
    }

    /** Delay em segundos */
    suspend fun waitSeconds(seconds: Long) = waitTicks(seconds * 20)

    /** Cancela todas as coroutines da Task */
    fun cancelAll() {
        job.cancel()
    }
}
