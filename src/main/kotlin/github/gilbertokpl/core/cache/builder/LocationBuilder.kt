package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write
import kotlin.concurrent.read

/**
 * Builder para cache de Locations do Bukkit.
 * Suporta filtragem de mundos desabilitados no load.
 *
 * IMPORTANTE: 
 * - NUNCA faz DELETE da linha (isso apagaria todas as colunas do jogador)
 * - Remove/null apenas limpa o valor para string vazia
 * - INSERT é feito apenas pelo PlayerData.createNewPlayerData()
 */
internal class LocationBuilder(
    table: Table,
    primaryColumn: Column<String>,
    column: Column<String>,
    private val serializer: ICacheSerializer<Location?, String>,
    logger: ICacheLogger,
    private val disabledWorldsProvider: () -> List<String> = { emptyList() },
    private val columnName: String = column.name
) : AbstractCacheBuilder<Location?, String>(table, primaryColumn, column, logger) {

    override fun toDatabase(value: Location?): String =
        if (value == null) "" else serializer.convertToDatabase(value) ?: ""

    override fun fromDatabase(value: String): Location? =
        if (value.isEmpty()) null else serializer.convertToCache(value)

    override fun shouldDelete(value: Location?): Boolean = value == null

    // =========================================================
    // Operações de set
    // =========================================================

    override operator fun set(entity: String, value: Location?) {
        set(entity, value, override = true)
    }

    override operator fun set(entity: String, value: Location?, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            cache[key] = value
            // NÃO marca para deleção - apenas atualiza para string vazia
            markedForDeletion.remove(key)
            markForUpdate(key)
        }
    }

    // =========================================================
    // Persistência - NUNCA DELETA A LINHA!
    // =========================================================

    override fun update() {
        if (pendingUpdates.isEmpty()) return

        rwLock.write {
            val keysToProcess = pendingUpdates.toList()
            if (keysToProcess.isEmpty()) return

            // Busca quais registros existem no banco (com chave original)
            val existingRows = table.selectAll()
                .where { LowerCase(primaryColumn) inList keysToProcess }
                .associate { it[primaryColumn].lowercase() to it[primaryColumn] }

            for (key in keysToProcess) {
                val value = cache[key]
                val originalKey = existingRows[key]
                val dbValue = toDatabase(value) // Converte null para ""

                try {
                    when {
                        // Existe no banco - faz UPDATE (mesmo se null, apenas limpa para "")
                        originalKey != null -> {
                            if (value != null) {
                                logger.log("Atualizando location: $key, coluna: ${column.name}")
                            } else {
                                logger.log("Limpando location: $key, coluna: ${column.name}")
                            }
                            table.update({ primaryColumn eq originalKey }) {
                                it[column] = dbValue
                            }
                        }
                        
                        // Não existe no banco - ignora (INSERT é responsabilidade do PlayerData)
                        else -> {
                            logger.warn("SKIP: Entidade não existe no banco: $key, coluna: ${column.name}")
                        }
                    }
                    pendingUpdates.remove(key)
                } catch (e: Exception) {
                    logger.error("Erro ao persistir location: $key, coluna: ${column.name}", e)
                }
            }
        }
    }

    override fun load() {
        val disabledWorlds = disabledWorldsProvider()

        rwLock.write {
            table.selectAll().forEach { row ->
                val key = normalizeKey(row[primaryColumn])
                val rawValue = row[column]
                val location = fromDatabase(rawValue)

                if (location == null) {
                    cache[key] = null
                    return@forEach
                }

                // Verifica se o mundo está desabilitado (para coluna "Back")
                val worldName = location.world?.name
                val isDisabledWorld = columnName == "Back" && disabledWorlds.any { disabled ->
                    worldName.equals(disabled, ignoreCase = true)
                }

                if (isDisabledWorld) {
                    cache[key] = null
                    markForUpdate(key) // Vai limpar no banco
                    logger.log("Location ignorada (mundo desabilitado): $key, mundo: $worldName")
                } else {
                    cache[key] = location
                }
            }
        }
        logger.log("Carregado ${cache.count { it.value != null }} locations para coluna: ${column.name}")
    }

    // =========================================================
    // Métodos utilitários
    // =========================================================

    /**
     * Retorna todas as locations em um mundo específico.
     */
    fun getByWorld(worldName: String): Map<String, Location> {
        rwLock.read {
            return cache.filterValues { it?.world?.name.equals(worldName, ignoreCase = true) }
                .mapValues { it.value!! }
        }
    }

    /**
     * Retorna locations dentro de um raio de uma location base.
     */
    fun getNearby(center: Location, radius: Double): Map<String, Location> {
        val radiusSquared = radius * radius
        rwLock.read {
            return cache.filterValues { loc ->
                loc != null &&
                loc.world?.name == center.world?.name &&
                loc.distanceSquared(center) <= radiusSquared
            }.mapValues { it.value!! }
        }
    }
}
