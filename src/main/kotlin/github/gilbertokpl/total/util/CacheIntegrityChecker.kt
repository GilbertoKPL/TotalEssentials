package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.serializer.*
import github.gilbertokpl.total.cache.sql.PlayerDataSQL
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilitário para verificar e sincronizar cache com banco de dados.
 * Se houver diferenças, força a atualização do banco com valores do cache.
 */
object CacheIntegrityChecker {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * Verifica diferenças e SINCRONIZA o banco com o cache.
     * Qualquer valor diferente no cache será gravado no banco.
     * Loga todas as diferenças encontradas.
     */
    fun syncCacheToDatabase() {
        val logs = mutableListOf<String>()
        val timestamp = dateFormat.format(Date())

        logs.add("=".repeat(70))
        logs.add("SINCRONIZAÇÃO CACHE -> BANCO - $timestamp")
        logs.add("=".repeat(70))
        logs.add("")

        var inserted = 0
        var updated = 0
        var unchanged = 0
        var errors = 0

        try {
            transaction(TotalEssentials.getCore().sql) {
                // Serializers
                val kitSerializer = KitSerializer()
                val homeSerializer = HomeSerializer()
                val vipSerializer = VipSerializer()
                val itemSerializer = ItemSerializer()
                val locationSerializer = LocationSerializer()
                val limiterItemSerializer = LimiterItemSerializer()
                val limiterLocationSerializer = LimiterLocationSerializer()

                // Pega todas as chaves do cache (otimizado - evita lista intermediária)
                val cacheKeys = PlayerData.moneyCache.getMap().keys.mapTo(HashSet()) { it.lowercase() }

                // Processa em batches de 1000 para evitar carregar tudo na memória
                val batchSize = 1000
                val keyBatches = cacheKeys.chunked(batchSize)

                for (batch in keyBatches) {
                    // Busca apenas os registros deste batch do banco
                    val dbRecords = PlayerDataSQL.selectAll()
                        .where { LowerCase(PlayerDataSQL.playerTable) inList batch }
                        .associate { row ->
                            row[PlayerDataSQL.playerTable].lowercase() to DbRecord(
                                originalKey = row[PlayerDataSQL.playerTable],
                                money = row[PlayerDataSQL.moneyTable],
                                nick = row[PlayerDataSQL.nickTable],
                                gamemode = row[PlayerDataSQL.gameModeTable],
                                vanish = row[PlayerDataSQL.vanishTable],
                                light = row[PlayerDataSQL.lightTable],
                                fly = row[PlayerDataSQL.flyTable],
                                speed = row[PlayerDataSQL.speedTable],
                                playtime = row[PlayerDataSQL.PlaytimeTable],
                                discord = row[PlayerDataSQL.DiscordTable],
                                color = row[PlayerDataSQL.colorTable],
                                command = row[PlayerDataSQL.CommandTable],
                                kits = row[PlayerDataSQL.kitsTable],
                                homes = row[PlayerDataSQL.homeTable],
                                vip = row[PlayerDataSQL.vipTable],
                                vipItems = row[PlayerDataSQL.vipItems],
                                back = row[PlayerDataSQL.backTable],
                                limiterItem = row[PlayerDataSQL.LimiterItemTable],
                                limiterLocation = row[PlayerDataSQL.LimiterLocationTable]
                            )
                        }

                    for (playerKey in batch) {
                        try {
                            val dbRecord = dbRecords[playerKey]

                            // Pega valores do cache
                            val money = PlayerData.moneyCache[playerKey] ?: continue
                        val nick = PlayerData.nickCache[playerKey] ?: ""
                        val gamemode = PlayerData.gameModeCache[playerKey] ?: 0
                        val vanish = PlayerData.vanishCache[playerKey] ?: false
                        val light = PlayerData.lightCache[playerKey] ?: false
                        val fly = PlayerData.flyCache[playerKey] ?: false
                        val speed = PlayerData.speedCache[playerKey] ?: 1
                        val playtime = PlayerData.playTimeCache[playerKey] ?: 0L
                        val discord = PlayerData.discordCache[playerKey] ?: 0L
                        val color = PlayerData.colorCache[playerKey] ?: ""
                        val command = PlayerData.commandCache[playerKey] ?: ""
                        val kits = PlayerData.kitsCache[playerKey] ?: hashMapOf()
                        val homes = PlayerData.homeCache[playerKey] ?: hashMapOf()
                        val vip = PlayerData.vipCache[playerKey] ?: hashMapOf()
                        val vipItemsList = PlayerData.vipItems[playerKey] ?: arrayListOf()
                        val back = PlayerData.backLocation[playerKey]
                        val limiterItem = PlayerData.limiterItemCache[playerKey] ?: hashMapOf()
                        val limiterLocation = PlayerData.limiterLocationCache[playerKey] ?: hashMapOf()

                        // Serializa para comparação
                        val kitsStr = kitSerializer.convertToDatabase(HashMap(kits))
                        val homesStr = homeSerializer.convertToDatabase(HashMap(homes))
                        val vipStr = vipSerializer.convertToDatabase(HashMap(vip))
                        val vipItemsStr = itemSerializer.convertToDatabase(ArrayList(vipItemsList))
                        val backStr = locationSerializer.convertToDatabase(back) ?: ""
                        val limiterItemStr = limiterItemSerializer.convertToDatabase(HashMap(limiterItem))
                        val limiterLocationStr = limiterLocationSerializer.convertToDatabase(HashMap(limiterLocation))

                        if (dbRecord == null) {
                            // INSERT - jogador não existe no banco
                            logs.add("➕ INSERT: $playerKey")
                            logs.add("   Money: $money")
                            logs.add("   Homes: ${homes.size} casas")
                            logs.add("   VIP: ${vip.size} vips")
                            logs.add("")

                            PlayerDataSQL.insert {
                                it[playerTable] = playerKey
                                it[moneyTable] = money
                                it[nickTable] = nick
                                it[gameModeTable] = gamemode
                                it[vanishTable] = vanish
                                it[lightTable] = light
                                it[flyTable] = fly
                                it[speedTable] = speed
                                it[PlaytimeTable] = playtime
                                it[DiscordTable] = discord
                                it[colorTable] = color
                                it[CommandTable] = command
                                it[kitsTable] = kitsStr
                                it[homeTable] = homesStr
                                it[vipTable] = vipStr
                                it[this.vipItems] = vipItemsStr
                                it[backTable] = backStr
                                it[LimiterItemTable] = limiterItemStr
                                it[LimiterLocationTable] = limiterLocationStr
                            }
                            inserted++
                        } else {
                            // Verifica diferenças
                            val diffs = mutableListOf<String>()

                            if (dbRecord.money != money)
                                diffs.add("   Money: ${dbRecord.money} -> $money")
                            if (dbRecord.nick != nick)
                                diffs.add("   Nick: '${dbRecord.nick}' -> '$nick'")
                            if (dbRecord.gamemode != gamemode)
                                diffs.add("   Gamemode: ${dbRecord.gamemode} -> $gamemode")
                            if (dbRecord.vanish != vanish)
                                diffs.add("   Vanish: ${dbRecord.vanish} -> $vanish")
                            if (dbRecord.light != light)
                                diffs.add("   Light: ${dbRecord.light} -> $light")
                            if (dbRecord.fly != fly)
                                diffs.add("   Fly: ${dbRecord.fly} -> $fly")
                            if (dbRecord.speed != speed)
                                diffs.add("   Speed: ${dbRecord.speed} -> $speed")
                            if (dbRecord.playtime != playtime)
                                diffs.add("   Playtime: ${dbRecord.playtime} -> $playtime")
                            if (dbRecord.discord != discord)
                                diffs.add("   Discord: ${dbRecord.discord} -> $discord")
                            if (dbRecord.color != color)
                                diffs.add("   Color: '${dbRecord.color}' -> '$color'")
                            if (dbRecord.command != command)
                                diffs.add("   Command: '${dbRecord.command}' -> '$command'")
                            if (dbRecord.kits != kitsStr)
                                diffs.add("   Kits: DIFERENTE")
                            if (dbRecord.homes != homesStr)
                                diffs.add("   Homes: DIFERENTE (cache: ${homes.size} casas)")
                            if (dbRecord.vip != vipStr)
                                diffs.add("   Vip: DIFERENTE (cache: ${vip.size} vips)")
                            if (dbRecord.vipItems != vipItemsStr)
                                diffs.add("   VipItems: DIFERENTE (cache: ${vipItemsList.size} itens)")
                            if (dbRecord.back != backStr)
                                diffs.add("   Back: DIFERENTE")
                            if (dbRecord.limiterItem != limiterItemStr)
                                diffs.add("   LimiterItem: DIFERENTE")
                            if (dbRecord.limiterLocation != limiterLocationStr)
                                diffs.add("   LimiterLocation: DIFERENTE")

                            if (diffs.isNotEmpty()) {
                                logs.add("🔄 UPDATE: $playerKey")
                                logs.addAll(diffs)
                                logs.add("")

                                // UPDATE - força atualização de TODAS as colunas
                                PlayerDataSQL.update({ LowerCase(PlayerDataSQL.playerTable) eq playerKey }) {
                                    it[moneyTable] = money
                                    it[nickTable] = nick
                                    it[gameModeTable] = gamemode
                                    it[vanishTable] = vanish
                                    it[lightTable] = light
                                    it[flyTable] = fly
                                    it[speedTable] = speed
                                    it[PlaytimeTable] = playtime
                                    it[DiscordTable] = discord
                                    it[colorTable] = color
                                    it[CommandTable] = command
                                    it[kitsTable] = kitsStr
                                    it[homeTable] = homesStr
                                    it[vipTable] = vipStr
                                    it[this.vipItems] = vipItemsStr
                                    it[backTable] = backStr
                                    it[LimiterItemTable] = limiterItemStr
                                    it[LimiterLocationTable] = limiterLocationStr
                                }
                                updated++
                            } else {
                                unchanged++
                            }
                        }
                    } catch (e: Exception) {
                        logs.add("❌ ERRO em $playerKey: ${e.message}")
                        errors++
                    }
                }
            }

            logs.add("-".repeat(70))
            logs.add("RESUMO:")
            logs.add("  ➕ Inseridos: $inserted")
            logs.add("  🔄 Atualizados (com diferenças): $updated")
            logs.add("  ✓ Sem alterações: $unchanged")
            logs.add("  ❌ Erros: $errors")
            logs.add("  Total processados: ${inserted + updated + unchanged + errors}")
            logs.add("")
            logs.add(if (errors == 0) "✅ SINCRONIZAÇÃO COMPLETA!" else "⚠️ SINCRONIZAÇÃO COM ERROS!")

        } catch (e: Exception) {
            logs.add("❌ ERRO CRÍTICO: ${e.message}")
            e.printStackTrace()
        }

        // Salva log e imprime
        saveLog(logs, "sync")
        logs.forEach { TotalEssentials.getCore().logger.log(it) }
    }

    private fun saveLog(lines: List<String>, prefix: String) {
        try {
            val pluginFolder = TotalEssentials.getInstance().dataFolder
            val logsFolder = File(pluginFolder, "log")
            logsFolder.mkdirs()

            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
            val logFile = File(logsFolder, "$prefix-$timestamp.txt")
            logFile.writeText(lines.joinToString("\n"))

            TotalEssentials.getCore().logger.log("Log salvo: ${logFile.absolutePath}")
        } catch (e: Exception) {
            TotalEssentials.getCore().logger.log("Erro ao salvar log: ${e.message}")
        }
    }

    private data class DbRecord(
        val originalKey: String,
        val money: Double,
        val nick: String,
        val gamemode: Int,
        val vanish: Boolean,
        val light: Boolean,
        val fly: Boolean,
        val speed: Int,
        val playtime: Long,
        val discord: Long,
        val color: String,
        val command: String,
        val kits: String,
        val homes: String,
        val vip: String,
        val vipItems: String,
        val back: String,
        val limiterItem: String,
        val limiterLocation: String
    )
}