package github.gilbertokpl.core.cache.impl

import github.gilbertokpl.core.cache.interfaces.ICacheErrorHandler
import github.gilbertokpl.core.cache.interfaces.ICacheLogger

/**
 * Error handlers pré-configurados para diferentes cenários.
 */
object CacheErrorHandlers {

    /**
     * Handler que apenas loga o erro e continua.
     * Recomendado para a maioria dos casos.
     */
    fun logOnly(logger: ICacheLogger): ICacheErrorHandler = ICacheErrorHandler { message, throwable ->
        logger.error(message, throwable)
    }

    /**
     * Handler que loga e re-lança a exceção.
     * Útil para ambientes de desenvolvimento.
     */
    fun logAndRethrow(logger: ICacheLogger): ICacheErrorHandler = ICacheErrorHandler { message, throwable ->
        logger.error(message, throwable)
        throw RuntimeException(message, throwable)
    }

    /**
     * Handler que executa uma ação customizada.
     * Útil para integração com sistemas de monitoramento.
     */
    fun custom(
        logger: ICacheLogger,
        additionalAction: (String, Throwable) -> Unit
    ): ICacheErrorHandler = ICacheErrorHandler { message, throwable ->
        logger.error(message, throwable)
        additionalAction(message, throwable)
    }

    /**
     * Handler para ambientes críticos que tenta notificar admins.
     */
    fun critical(
        logger: ICacheLogger,
        notifyAction: (String) -> Unit
    ): ICacheErrorHandler = ICacheErrorHandler { message, throwable ->
        logger.error(message, throwable)
        notifyAction("ERRO CRÍTICO DE CACHE: $message - ${throwable.message}")
    }

    /**
     * Handler com contador de erros.
     * Útil para decidir quando escalar o problema.
     */
    class CountingErrorHandler(
        private val logger: ICacheLogger,
        private val maxErrors: Int = 10,
        private val onMaxErrorsReached: () -> Unit = {}
    ) : ICacheErrorHandler {
        private var errorCount = 0

        override fun onError(message: String, throwable: Throwable) {
            logger.error(message, throwable)
            errorCount++
            if (errorCount >= maxErrors) {
                onMaxErrorsReached()
                errorCount = 0 // Reset após ação
            }
        }

        fun getErrorCount(): Int = errorCount
        fun resetCount() { errorCount = 0 }
    }
}
