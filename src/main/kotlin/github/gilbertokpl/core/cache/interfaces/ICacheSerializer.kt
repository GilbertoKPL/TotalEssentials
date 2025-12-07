package github.gilbertokpl.core.cache.interfaces

/**
 * Interface para serialização/deserialização de dados do cache.
 *
 * @param T Tipo em memória (ex: Location, ArrayList)
 * @param V Tipo no banco de dados (ex: String, Int)
 */
interface ICacheSerializer<T, V> {

    /**
     * Converte do tipo em memória para o tipo do banco de dados.
     */
    fun convertToDatabase(value: T): V

    /**
     * Converte do tipo do banco de dados para o tipo em memória.
     * @return null se a conversão falhar ou o valor for inválido
     */
    fun convertToCache(value: V): T?
}
