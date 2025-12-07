package github.gilbertokpl.core.cache

import github.gilbertokpl.core.TotalCore
import github.gilbertokpl.core.cache.builder.*
import github.gilbertokpl.core.cache.interfaces.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Configuração do CacheManager.
 */
data class CacheConfig(
    val saveIntervalMinutes: Long = 5,
    val initialDelayMinutes: Long = 5,
    val shutdownTimeoutSeconds: Long = 30
)

/**
 * Gerenciador central do sistema de cache.
 *
 * Responsável por:
 * - Criar e registrar builders de cache
 * - Gerenciar ciclo de vida (load/save/unload)
 * - Agendar salvamentos periódicos
 */
class CacheManager private constructor(
    private val databaseProvider: () -> Database,
    private val loggerProvider: () -> ICacheLogger,
    private val errorHandler: ICacheErrorHandler,
    private val config: CacheConfig,
    private val reflectionLoader: ((String) -> Unit)?
) {

    // =========================================================
    // Construtores
    // =========================================================

    /**
     * Construtor para uso com TotalCore (compatibilidade com código existente).
     */
    constructor(totalCore: TotalCore) : this(
        databaseProvider = { totalCore.sql },
        loggerProvider = { TotalCoreLogger(totalCore) },
        errorHandler = ICacheErrorHandler { msg, e ->
            totalCore.logger.log("ERRO CACHE: $msg")
            e.printStackTrace()
        },
        config = CacheConfig(),
        reflectionLoader = { pkg -> totalCore.getReflection().getClasses(pkg) }
    )

    /**
     * Construtor completo para uso standalone.
     */
    constructor(
        database: Database,
        logger: ICacheLogger,
        errorHandler: ICacheErrorHandler = ICacheErrorHandler { msg, e -> logger.error(msg, e) },
        config: CacheConfig = CacheConfig(),
        reflectionLoader: ((String) -> Unit)? = null
    ) : this(
        databaseProvider = { database },
        loggerProvider = { logger },
        errorHandler = errorHandler,
        config = config,
        reflectionLoader = reflectionLoader
    )

    // Propriedades lazy para evitar acesso antes da inicialização
    private val database: Database get() = databaseProvider()
    private val logger: ICacheLogger get() = loggerProvider()

    /**
     * Adapter para usar o FileLogger do TotalCore como ICacheLogger.
     */
    private class TotalCoreLogger(private val totalCore: TotalCore) : ICacheLogger {
        override fun log(message: String) {
            totalCore.logger.log(message)
        }

        override fun warn(message: String) {
            totalCore.logger.log("[WARN] $message")
        }

        override fun error(message: String, throwable: Throwable?) {
            totalCore.logger.log("[ERROR] $message")
            throwable?.printStackTrace()
        }
    }

    // =========================================================
    // Estado interno
    // =========================================================

    private val builders = mutableListOf<ICacheBuilder<*>>()
    private var executor: ScheduledExecutorService? = null
    private val isRunning = AtomicBoolean(false)

    // =========================================================
    // Ciclo de vida
    // =========================================================

    /**
     * Inicia o sistema de cache.
     * @param cachePackage Pacote para carregar classes via reflection (opcional)
     */
    fun start(cachePackage: String? = null) {
        if (isRunning.getAndSet(true)) {
            logger.warn("CacheManager já está rodando")
            return
        }

        // Carrega classes via reflection se fornecido
        cachePackage?.let { pkg ->
            reflectionLoader?.invoke(pkg)
        }

        // Carrega dados do banco
        transaction(database) {
            builders.forEach { builder ->
                try {
                    builder.load()
                } catch (e: Exception) {
                    errorHandler.onError("Erro ao carregar cache: ${builder::class.simpleName}", e)
                }
            }
        }

        // Agenda salvamento periódico
        executor = Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "CacheManager-SaveThread").apply {
                isDaemon = true
            }
        }

        executor?.scheduleWithFixedDelay(
            { save() },
            config.initialDelayMinutes,
            config.saveIntervalMinutes,
            TimeUnit.MINUTES
        )

        logger.log("CacheManager iniciado com ${builders.size} builders")
    }

    /**
     * Para o sistema de cache, salvando todos os dados.
     */
    fun stop() {
        if (!isRunning.getAndSet(false)) {
            logger.warn("CacheManager não está rodando")
            return
        }

        // Para o executor
        executor?.let { exec ->
            exec.shutdown()
            try {
                if (!exec.awaitTermination(config.shutdownTimeoutSeconds, TimeUnit.SECONDS)) {
                    exec.shutdownNow()
                    logger.warn("Executor forçado a parar após timeout")
                }
            } catch (e: InterruptedException) {
                exec.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }
        executor = null

        // Salva e descarrega dados
        transaction(database) {
            builders.forEach { builder ->
                try {
                    builder.unload()
                } catch (e: Exception) {
                    errorHandler.onError("Erro ao descarregar cache: ${builder::class.simpleName}", e)
                }
            }
        }

        logger.log("CacheManager parado")
    }

    /**
     * Salva todas as alterações pendentes no banco.
     */
    fun save() {
        if (!isRunning.get()) return

        try {
            transaction(database) {
                builders.forEach { builder ->
                    try {
                        if (builder.hasPendingUpdates()) {
                            builder.update()
                        }
                    } catch (e: Exception) {
                        errorHandler.onError("Erro ao salvar cache: ${builder::class.simpleName}", e)
                    }
                }
            }
        } catch (e: Exception) {
            errorHandler.onError("Erro crítico durante salvamento", e)
        }
    }

    /**
     * Força salvamento síncrono (útil para comandos admin).
     */
    fun forceSave(): Int {
        var savedCount = 0
        transaction(database) {
            builders.forEach { builder ->
                if (builder.hasPendingUpdates()) {
                    builder.update()
                    savedCount++
                }
            }
        }
        return savedCount
    }

    // =========================================================
    // Builders simples (sem persistência)
    // =========================================================

    fun <T> simple(): SimpleBuilder<T> = SimpleBuilder()

    fun simpleBoolean(): SimpleBuilder<Boolean> = simple()
    fun simpleInt(): SimpleBuilder<Int> = simple()
    fun simpleInteger(): SimpleBuilder<Int> = simple() // Alias para compatibilidade
    fun simpleLong(): SimpleBuilder<Long> = simple()
    fun simpleString(): SimpleBuilder<String> = simple()
    fun simplePlayer(): SimpleBuilder<Player?> = simple()
    fun <T> simpleList(): SimpleBuilder<List<T>> = simple()

    // =========================================================
    // Builders persistentes (tipos primitivos)
    // =========================================================

    fun string(cache: ICache, column: Column<String>): ICacheBuilder<String> =
        createByteBuilder(cache, column)

    fun boolean(cache: ICache, column: Column<Boolean>): ICacheBuilder<Boolean> =
        createByteBuilder(cache, column)

    fun int(cache: ICache, column: Column<Int>): ICacheBuilder<Int> =
        createByteBuilder(cache, column)

    // Alias para compatibilidade com código antigo
    fun integer(cache: ICache, column: Column<Int>): ICacheBuilder<Int> =
        createByteBuilder(cache, column)

    fun double(cache: ICache, column: Column<Double>): ICacheBuilder<Double> =
        createByteBuilder(cache, column)

    fun long(cache: ICache, column: Column<Long>): ICacheBuilder<Long> =
        createByteBuilder(cache, column)

    // =========================================================
    // Builders persistentes (tipos complexos)
    // =========================================================

    /**
     * Versão com disabledWorldsProvider (nova funcionalidade).
     */
    fun location(
        cache: ICache,
        column: Column<String>,
        serializer: ICacheSerializer<Location?, String>,
        disabledWorldsProvider: () -> List<String>
    ): ICacheBuilder<Location?> {
        val builder = LocationBuilder(
            table = cache.table,
            primaryColumn = cache.primaryColumn,
            column = column,
            serializer = serializer,
            logger = logger,
            disabledWorldsProvider = disabledWorldsProvider,
            columnName = column.name
        )
        register(builder)
        return builder
    }

    fun location(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<Location?, String>
    ): ICacheBuilder<Location?> {
        val builder = LocationBuilder(
            table = cacheBase.table,
            primaryColumn = cacheBase.primaryColumn,
            column = column,
            serializer = base,
            logger = logger,
            disabledWorldsProvider = { emptyList() },
            columnName = column.name
        )
        register(builder)
        return builder
    }

    fun <V> list(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<ArrayList<V>, String>
    ): ICacheBuilderExtended<ArrayList<V>, V> {
        val builder = ListBuilder(
            table = cacheBase.table,
            primaryColumn = cacheBase.primaryColumn,
            column = column,
            serializer = base,
            logger = logger
        )
        register(builder)
        return builder
    }

    fun <V, K> hashMap(
        cacheBase: ICache,
        column: Column<String>,
        base: ICacheSerializer<HashMap<V, K>, String>
    ): ICacheBuilderExtended<HashMap<V, K>, V> {
        val builder = HashMapBuilder(
            table = cacheBase.table,
            primaryColumn = cacheBase.primaryColumn,
            column = column,
            serializer = base,
            logger = logger
        )
        register(builder)
        return builder
    }

    fun <K, V> hashMapInt(
        cache: ICache,
        column: Column<Int>,
        serializer: ICacheSerializer<HashMap<K, V>, Int>
    ): HashMapBuilder<K, V, Int> {
        val builder = HashMapBuilder(
            table = cache.table,
            primaryColumn = cache.primaryColumn,
            column = column,
            serializer = serializer,
            logger = logger
        )
        register(builder)
        return builder
    }

    // Alias para compatibilidade com código antigo
    fun <V, K> integerHashMap(
        cacheBase: ICache,
        column: Column<Int>,
        base: ICacheSerializer<HashMap<V, K>, Int>
    ): ICacheBuilderExtended<HashMap<V, K>, V> {
        val builder = HashMapBuilder(
            table = cacheBase.table,
            primaryColumn = cacheBase.primaryColumn,
            column = column,
            serializer = base,
            logger = logger
        )
        register(builder)
        return builder
    }

    // =========================================================
    // Métodos utilitários
    // =========================================================

    private fun <T> createByteBuilder(cache: ICache, column: Column<T>): ByteBuilder<T> {
        val builder = ByteBuilder(
            table = cache.table,
            primaryColumn = cache.primaryColumn,
            column = column,
            logger = logger
        )
        register(builder)
        return builder
    }

    private fun register(builder: ICacheBuilder<*>) {
        builders.add(builder)
    }

    /**
     * Retorna estatísticas do cache.
     */
    fun getStats(): CacheStats {
        return CacheStats(
            builderCount = builders.size,
            totalEntries = builders.sumOf { it.size() },
            pendingUpdates = builders.count { it.hasPendingUpdates() },
            isRunning = isRunning.get()
        )
    }

    /**
     * Retorna lista de builders registrados.
     */
    fun getBuilders(): List<ICacheBuilder<*>> = builders.toList()
}

/**
 * Estatísticas do CacheManager.
 */
data class CacheStats(
    val builderCount: Int,
    val totalEntries: Int,
    val pendingUpdates: Int,
    val isRunning: Boolean
)
