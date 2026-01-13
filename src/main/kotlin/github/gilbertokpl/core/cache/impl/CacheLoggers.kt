package github.gilbertokpl.core.cache.impl

import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Implementação do ICacheLogger usando java.util.logging.
 * Compatível com o sistema de logging do Bukkit.
 */
class BukkitCacheLogger(
    private val logger: Logger,
    private val debugEnabled: Boolean = false
) : ICacheLogger {

    override fun log(message: String) {
        if (debugEnabled) {
            logger.info("[TotalCache] $message")
        }
    }

    override fun warn(message: String) {
        logger.warning("[TotalCache] $message")
    }

    override fun error(message: String, throwable: Throwable?) {
        if (throwable != null) {
            logger.log(Level.SEVERE, "[TotalCache] $message", throwable)
        } else {
            logger.severe("[TotalCache] $message")
        }
    }
}

/**
 * Logger silencioso para testes ou quando não há necessidade de log.
 */
object SilentCacheLogger : ICacheLogger {
    override fun log(message: String) {}
    override fun warn(message: String) {}
    override fun error(message: String, throwable: Throwable?) {}
}

/**
 * Logger que imprime no console (útil para testes).
 */
object ConsoleCacheLogger : ICacheLogger {
    override fun log(message: String) {
        println("[TotalCache:INFO] $message")
    }

    override fun warn(message: String) {
        println("[TotalCache:WARN] $message")
    }

    override fun error(message: String, throwable: Throwable?) {
        System.err.println("[TotalCache:ERROR] $message")
        throwable?.printStackTrace()
    }
}
