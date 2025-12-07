package github.gilbertokpl.core.cache.builder

import github.gilbertokpl.core.cache.interfaces.ICacheLogger
import github.gilbertokpl.core.cache.interfaces.ICacheSerializer
import org.bukkit.Location
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.concurrent.write
import kotlin.concurrent.read

/**
 * Builder para cache de Locations do Bukkit.
 * Suporta filtragem de mundos desabilitados no load.
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
        serializer.convertToCache(value)

    override fun shouldDelete(value: Location?): Boolean = value == null

    // =========================================================
    // Operações de set - CORRIGIDO para lidar com null
    // =========================================================

    override operator fun set(entity: String, value: Location?) {
        set(entity, value, override = true)
    }

    override operator fun set(entity: String, value: Location?, override: Boolean) {
        val key = normalizeKey(entity)
        rwLock.write {
            if (value == null) {
                // Marca para deleção
                cache[key] = null
                markedForDeletion.add(key)
            } else {
                // Valor válido
                cache[key] = value
                markedForDeletion.remove(key)
            }
            markForUpdate(key)
        }
    }

    // =========================================================
    // Persistência
    // =========================================================

    override fun update() {
        if (pendingUpdates.isEmpty()) return

        rwLock.write {
            val keysToProcess = pendingUpdates.toList()
            if (keysToProcess.isEmpty()) return

            val existingRows = table.selectAll()
                .where { primaryColumn inList keysToProcess }
                .associate { it[primaryColumn].lowercase() to it[primaryColumn] }

            for (key in keysToProcess) {
                val value = cache[key]
                val originalKey = existingRows[key]
                val shouldRemove = isMarkedForDeletion(key) || value == null

                try {
                    when {
                        // Deletar se null e existe no banco
                        shouldRemove && originalKey != null -> {
                            logger.log("Removendo location: $key, coluna: ${column.name}")
                            table.deleteWhere { primaryColumn eq originalKey }
                            clearDeletionMark(key)
                        }
                        // Inserir se não existe
                        value != null && originalKey == null -> {
                            val dbValue = toDatabase(value)
                            logger.log("Inserindo location: $key, coluna: ${column.name}")
                            table.insert {
                                it[primaryColumn] = key
                                it[column] = dbValue
                            }
                        }
                        // Atualizar se existe
                        value != null && originalKey != null -> {
                            val dbValue = toDatabase(value)
                            logger.log("Atualizando location: $key, coluna: ${column.name}")
                            table.update({ primaryColumn eq originalKey }) {
                                it[column] = dbValue
                            }
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
                val location = fromDatabase(row[column])

                if (location == null) {
                    return@forEach
                }

                // Verifica se o mundo está desabilitado (para coluna "Back")
                val worldName = location.world?.name
                val isDisabledWorld = columnName == "Back" && disabledWorlds.any { disabled ->
                    worldName.equals(disabled, ignoreCase = true)
                }

                if (isDisabledWorld) {
                    cache[key] = null
                    markedForDeletion.add(key)
                    markForUpdate(key)
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
