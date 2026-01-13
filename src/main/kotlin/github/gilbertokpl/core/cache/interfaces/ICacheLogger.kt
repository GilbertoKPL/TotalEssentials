package github.gilbertokpl.core.cache.interfaces

/**
 * Interface para logging do sistema de cache.
 */
interface ICacheLogger {
    /**
     * Log de informação.
     */
    fun log(message: String)

    /**
     * Log de informação com nível.
     * Nível 1 = essencial, Nível 2 = detalhado
     */
    fun log(message: String, level: Int) {
        // Default: ignora nível, sempre loga
        log(message)
    }

    /**
     * Log de aviso.
     */
    fun warn(message: String)

    /**
     * Log de erro com exceção opcional.
     */
    fun error(message: String, throwable: Throwable? = null)
}
