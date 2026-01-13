package github.gilbertokpl.core.cache.interfaces

/**
 * Interface funcional para tratamento de erros do cache.
 */
fun interface ICacheErrorHandler {
    fun onError(message: String, throwable: Throwable)
}
