package github.gilbertokpl.core.cache.interfaces

/**
 * Interface para tratamento de erros do sistema de cache.
 * Permite comportamento configurável em caso de falhas.
 */
fun interface ICacheErrorHandler {
    fun onError(message: String, throwable: Throwable)
}
