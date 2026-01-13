package github.gilbertokpl.core.cache.builder

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.concurrent.ConcurrentHashMap

/**
 * Gerenciador centralizado para verificar existência de registros no banco.
 * 
 * PROBLEMA RESOLVIDO:
 * Antes, cada builder (ByteBuilder, HashMapBuilder, etc.) fazia sua própria
 * verificação de existência e INSERT independente. Isso causava:
 * 1. INSERT parcial com apenas uma coluna
 * 2. Valores DEFAULT para todas as outras colunas (zerando dados)
 * 
 * SOLUÇÃO:
 * Este manager mantém um cache centralizado de quais entidades existem
 * em cada tabela. Os builders consultam este cache antes de fazer INSERT.
 */
object EntityExistenceCache {
    
    // Cache de entidades existentes por tabela
    // Chave: nome da tabela
    // Valor: Set de chaves (normalizadas para lowercase) que existem
    private val existingEntities = ConcurrentHashMap<String, MutableSet<String>>()
    
    /**
     * Verifica se uma entidade existe em uma tabela.
     */
    fun exists(table: Table, key: String): Boolean {
        val tableName = table.tableName
        val normalizedKey = key.lowercase()
        return existingEntities[tableName]?.contains(normalizedKey) ?: false
    }
    
    /**
     * Marca uma entidade como existente.
     */
    fun markAsExisting(table: Table, key: String) {
        val tableName = table.tableName
        val normalizedKey = key.lowercase()
        existingEntities.computeIfAbsent(tableName) { 
            ConcurrentHashMap.newKeySet() 
        }.add(normalizedKey)
    }
    
    /**
     * Marca uma entidade como deletada.
     */
    fun markAsDeleted(table: Table, key: String) {
        val tableName = table.tableName
        val normalizedKey = key.lowercase()
        existingEntities[tableName]?.remove(normalizedKey)
    }
    
    /**
     * Carrega entidades existentes do banco de dados.
     * Deve ser chamado uma vez durante o load inicial.
     */
    fun loadFromDatabase(table: Table, primaryColumn: Column<String>) {
        val tableName = table.tableName
        val keys = ConcurrentHashMap.newKeySet<String>()
        
        table.selectAll().forEach { row ->
            keys.add(row[primaryColumn].lowercase())
        }
        
        existingEntities[tableName] = keys
    }
    
    /**
     * Verifica múltiplas chaves de uma vez e retorna quais existem.
     * Mais eficiente que verificar uma por uma.
     */
    fun filterExisting(table: Table, keys: List<String>): Set<String> {
        val tableName = table.tableName
        val existingSet = existingEntities[tableName] ?: return emptySet()
        return keys.filter { existingSet.contains(it.lowercase()) }.toSet()
    }
    
    /**
     * Retorna o mapa original de chaves (chave normalizada -> chave original no banco).
     * Usado para fazer UPDATE com a chave original.
     */
    fun fetchOriginalKeys(
        table: Table, 
        primaryColumn: Column<String>, 
        keys: List<String>
    ): Map<String, String> {
        if (keys.isEmpty()) return emptyMap()
        
        return table.selectAll()
            .where { LowerCase(primaryColumn) inList keys }
            .associate { it[primaryColumn].lowercase() to it[primaryColumn] }
    }
    
    /**
     * Cria um novo registro completo no banco de dados.
     * Retorna true se criou, false se já existia.
     */
    fun createIfNotExists(
        table: Table,
        primaryColumn: Column<String>,
        key: String,
        initializer: (org.jetbrains.exposed.v1.core.statements.InsertStatement<Number>) -> Unit
    ): Boolean {
        val normalizedKey = key.lowercase()
        
        // Verifica se já existe (double-check para evitar race condition)
        if (exists(table, normalizedKey)) {
            return false
        }
        
        // Sincroniza para evitar INSERT duplicado
        synchronized(this) {
            // Double-check dentro do synchronized
            if (exists(table, normalizedKey)) {
                return false
            }
            
            // Faz o INSERT
            table.insert {
                it[primaryColumn] = normalizedKey
                initializer(it)
            }
            
            // Marca como existente
            markAsExisting(table, normalizedKey)
            return true
        }
    }
    
    /**
     * Limpa o cache de uma tabela específica.
     */
    fun clearTable(table: Table) {
        existingEntities.remove(table.tableName)
    }
    
    /**
     * Limpa todo o cache.
     */
    fun clearAll() {
        existingEntities.clear()
    }
}
