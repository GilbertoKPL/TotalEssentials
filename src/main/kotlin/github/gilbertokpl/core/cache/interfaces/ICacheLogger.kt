package github.gilbertokpl.core.cache.interfaces

/**
 * Interface para logging do sistema de cache.
 * Permite injeção de dependência e facilita testes.
 */
interface ICacheLogger {
    fun log(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}
