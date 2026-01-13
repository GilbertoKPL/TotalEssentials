package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.*
import github.gilbertokpl.total.cache.serializer.*
import github.gilbertokpl.total.cache.sql.*
import org.jetbrains.exposed.v1.core.LowerCase
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
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
     * Executa todos os checkers de integridade de cache
     */
    fun syncAllCaches() {
        val logs = mutableListOf<String>()
        logs.add("=".repeat(70))
        logs.add("SINCRONIZAÇÃO COMPLETA DE TODOS OS CACHES - ${dateFormat.format(Date())}")
        logs.add("=".repeat(70))
        logs.add("")

        TotalEssentials.getCore().logger.log("Iniciando sincronização de todos os caches...")

        syncPlayerDataToDatabase()
        syncShopDataToDatabase()
        syncKeyDataToDatabase()
        syncKitsDataToDatabase()
        syncSpawnDataToDatabase()
        syncVipDataToDatabase()
        syncWarpDataToDatabase()

        logs.add("✅ SINCRONIZAÇÃO COMPLETA DE TODOS OS CACHES FINALIZADA!")
        saveLog(logs, "sync-all")
        logs.forEach { TotalEssentials.getCore().logger.log(it) }
    }

    /**
     * Verifica diferenças e SINCRONIZA o banco com o cache - PLAYER DATA.
     * Qualquer valor diferente no cache será gravado no banco.
     * Loga todas as diferenças encontradas.
     */
    fun syncPlayerDataToDatabase() {
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
                            val limiterLocationStr =
                                limiterLocationSerializer.convertToDatabase(HashMap(limiterLocation))

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
            }
            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO: ${e.message}")
                e.printStackTrace()
            }
            // Salva log e imprime
            saveLog(logs, "sync-playerdata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
    }

        /**
         * Sincroniza ShopData
         */
        fun syncShopDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO SHOPDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val locationSerializer = LocationSerializer()
                    val cacheKeys = ShopData.shopVisits.getMap().keys.mapTo(HashSet()) { it.lowercase() }

                    val dbRecords = ShopDataSQL.selectAll()
                        .associate { row ->
                            row[ShopDataSQL.playerTable].lowercase() to ShopDbRecord(
                                visits = row[ShopDataSQL.visits],
                                location = row[ShopDataSQL.location],
                                open = row[ShopDataSQL.open]
                            )
                        }

                    for (playerKey in cacheKeys) {
                        try {
                            val visits = ShopData.shopVisits[playerKey] ?: continue
                            val location = ShopData.shopLocation[playerKey]
                            val open = ShopData.shopOpen[playerKey] ?: false

                            val locationStr = locationSerializer.convertToDatabase(location) ?: ""
                            val dbRecord = dbRecords[playerKey]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT SHOP: $playerKey")
                                ShopDataSQL.insert {
                                    it[playerTable] = playerKey
                                    it[this.visits] = visits
                                    it[this.location] = locationStr
                                    it[this.open] = open
                                }
                                inserted++
                            } else {
                                val diffs = mutableListOf<String>()
                                if (dbRecord.visits != visits) diffs.add("   Visits: ${dbRecord.visits} -> $visits")
                                if (dbRecord.location != locationStr) diffs.add("   Location: DIFERENTE")
                                if (dbRecord.open != open) diffs.add("   Open: ${dbRecord.open} -> $open")

                                if (diffs.isNotEmpty()) {
                                    logs.add("🔄 UPDATE SHOP: $playerKey")
                                    logs.addAll(diffs)
                                    ShopDataSQL.update({ LowerCase(ShopDataSQL.playerTable) eq playerKey }) {
                                        it[this.visits] = visits
                                        it[this.location] = locationStr
                                        it[this.open] = open
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

                logs.add("RESUMO SHOP: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ SHOP SYNC OK!" else "⚠️ SHOP SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO SHOP: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-shopdata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }

        /**
         * Sincroniza KeyData
         */
        fun syncKeyDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO KEYDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val cacheKeys = KeyData.vipName.getMap().keys.toSet()

                    val dbRecords = VipKeysSQL.selectAll()
                        .associate { row ->
                            row[VipKeysSQL.vipKey] to KeyDbRecord(
                                vipName = row[VipKeysSQL.vipName],
                                vipTime = row[VipKeysSQL.vipTime]
                            )
                        }

                    for (key in cacheKeys) {
                        try {
                            val vipName = KeyData.vipName[key] ?: continue
                            val vipTime = KeyData.vipTime[key] ?: 30L
                            val dbRecord = dbRecords[key]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT KEY: $key")
                                VipKeysSQL.insert {
                                    it[vipKey] = key
                                    it[this.vipName] = vipName
                                    it[this.vipTime] = vipTime
                                }
                                inserted++
                            } else {
                                val diffs = mutableListOf<String>()
                                if (dbRecord.vipName != vipName) diffs.add("   Name: ${dbRecord.vipName} -> $vipName")
                                if (dbRecord.vipTime != vipTime) diffs.add("   Time: ${dbRecord.vipTime} -> $vipTime")

                                if (diffs.isNotEmpty()) {
                                    logs.add("🔄 UPDATE KEY: $key")
                                    logs.addAll(diffs)
                                    VipKeysSQL.update({ VipKeysSQL.vipKey eq key }) {
                                        it[this.vipName] = vipName
                                        it[this.vipTime] = vipTime
                                    }
                                    updated++
                                } else {
                                    unchanged++
                                }
                            }
                        } catch (e: Exception) {
                            logs.add("❌ ERRO em $key: ${e.message}")
                            errors++
                        }
                    }
                }

                logs.add("RESUMO KEYS: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ KEYS SYNC OK!" else "⚠️ KEYS SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO KEYS: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-keydata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }

        /**
         * Sincroniza KitsData
         */
        fun syncKitsDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO KITSDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val itemSerializer = ItemSerializer()
                    val cacheKeys = KitsData.kitTime.getMap().keys.mapTo(HashSet()) { it.lowercase() }

                    val dbRecords = KitsDataSQL.selectAll()
                        .associate { row ->
                            row[KitsDataSQL.kitNameTable].lowercase() to KitDbRecord(
                                fakeName = row[KitsDataSQL.kitFakeNameTable],
                                time = row[KitsDataSQL.kitTimeTable],
                                items = row[KitsDataSQL.kitItemsTable],
                                weight = row[KitsDataSQL.kitWeightTable]
                            )
                        }

                    for (kitName in cacheKeys) {
                        try {
                            val fakeName = KitsData.kitFakeName[kitName] ?: ""
                            val time = KitsData.kitTime[kitName] ?: continue
                            val items = KitsData.kitItems[kitName] ?: arrayListOf()
                            val weight = KitsData.kitWeight[kitName] ?: 0

                            val itemsStr = itemSerializer.convertToDatabase(ArrayList(items))
                            val dbRecord = dbRecords[kitName]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT KIT: $kitName")
                                KitsDataSQL.insert {
                                    it[kitNameTable] = kitName
                                    it[kitFakeNameTable] = fakeName
                                    it[kitTimeTable] = time
                                    it[kitItemsTable] = itemsStr
                                    it[kitWeightTable] = weight
                                }
                                inserted++
                            } else {
                                val diffs = mutableListOf<String>()
                                if (dbRecord.fakeName != fakeName) diffs.add("   FakeName: DIFERENTE")
                                if (dbRecord.time != time) diffs.add("   Time: ${dbRecord.time} -> $time")
                                if (dbRecord.items != itemsStr) diffs.add("   Items: DIFERENTE")
                                if (dbRecord.weight != weight) diffs.add("   Weight: ${dbRecord.weight} -> $weight")

                                if (diffs.isNotEmpty()) {
                                    logs.add("🔄 UPDATE KIT: $kitName")
                                    logs.addAll(diffs)
                                    KitsDataSQL.update({ LowerCase(KitsDataSQL.kitNameTable) eq kitName }) {
                                        it[kitFakeNameTable] = fakeName
                                        it[kitTimeTable] = time
                                        it[kitItemsTable] = itemsStr
                                        it[kitWeightTable] = weight
                                    }
                                    updated++
                                } else {
                                    unchanged++
                                }
                            }
                        } catch (e: Exception) {
                            logs.add("❌ ERRO em $kitName: ${e.message}")
                            errors++
                        }
                    }
                }

                logs.add("RESUMO KITS: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ KITS SYNC OK!" else "⚠️ KITS SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO KITS: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-kitsdata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }

        /**
         * Sincroniza SpawnData
         */
        fun syncSpawnDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO SPAWNDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val locationSerializer = LocationSerializer()
                    val cacheKeys = SpawnData.spawnLocation.getMap().keys.toSet()

                    val dbRecords = SpawnDataSQL.selectAll()
                        .associate { row ->
                            row[SpawnDataSQL.spawnNameTable] to SpawnDbRecord(
                                location = row[SpawnDataSQL.spawnLocationTable]
                            )
                        }

                    for (spawnName in cacheKeys) {
                        try {
                            val location = SpawnData.spawnLocation[spawnName]
                            val locationStr = locationSerializer.convertToDatabase(location) ?: ""
                            val dbRecord = dbRecords[spawnName]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT SPAWN: $spawnName")
                                SpawnDataSQL.insert {
                                    it[spawnNameTable] = spawnName
                                    it[spawnLocationTable] = locationStr
                                }
                                inserted++
                            } else {
                                if (dbRecord.location != locationStr) {
                                    logs.add("🔄 UPDATE SPAWN: $spawnName - Location: DIFERENTE")
                                    SpawnDataSQL.update({ SpawnDataSQL.spawnNameTable eq spawnName }) {
                                        it[spawnLocationTable] = locationStr
                                    }
                                    updated++
                                } else {
                                    unchanged++
                                }
                            }
                        } catch (e: Exception) {
                            logs.add("❌ ERRO em $spawnName: ${e.message}")
                            errors++
                        }
                    }
                }

                logs.add("RESUMO SPAWN: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ SPAWN SYNC OK!" else "⚠️ SPAWN SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO SPAWN: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-spawndata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }

        /**
         * Sincroniza VipData
         */
        fun syncVipDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO VIPDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val itemSerializer = ItemSerializer()
                    val commandsSerializer = CommandsSerializer()
                    val cacheKeys = VipData.vipPrice.getMap().keys.mapTo(HashSet()) { it.lowercase() }

                    val dbRecords = VipDataSQL.selectAll()
                        .associate { row ->
                            row[VipDataSQL.vipName].lowercase() to VipDbRecord(
                                discord = row[VipDataSQL.vipDiscord],
                                group = row[VipDataSQL.vipGroup],
                                items = row[VipDataSQL.vipItems],
                                commands = row[VipDataSQL.vipCommands],
                                price = row[VipDataSQL.vipPrice],
                                quantity = row[VipDataSQL.vipQuantity]
                            )
                        }

                    for (vipName in cacheKeys) {
                        try {
                            val price = VipData.vipPrice[vipName] ?: continue
                            val quantity = VipData.vipQuantity[vipName] ?: 0
                            val group = VipData.vipGroup[vipName] ?: ""
                            val discord = VipData.vipDiscord[vipName] ?: 0L
                            val items = VipData.vipItems[vipName] ?: arrayListOf()
                            val commands = VipData.vipCommands[vipName] ?: arrayListOf()

                            val itemsStr = itemSerializer.convertToDatabase(ArrayList(items))
                            val commandsStr = commandsSerializer.convertToDatabase(ArrayList(commands))
                            val dbRecord = dbRecords[vipName]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT VIP: $vipName")
                                VipDataSQL.insert {
                                    it[this.vipName] = vipName
                                    it[vipDiscord] = discord
                                    it[vipGroup] = group
                                    it[this.vipItems] = itemsStr
                                    it[vipCommands] = commandsStr
                                    it[vipPrice] = price
                                    it[vipQuantity] = quantity
                                }
                                inserted++
                            } else {
                                val diffs = mutableListOf<String>()
                                if (dbRecord.discord != discord) diffs.add("   Discord: ${dbRecord.discord} -> $discord")
                                if (dbRecord.group != group) diffs.add("   Group: ${dbRecord.group} -> $group")
                                if (dbRecord.items != itemsStr) diffs.add("   Items: DIFERENTE")
                                if (dbRecord.commands != commandsStr) diffs.add("   Commands: DIFERENTE")
                                if (dbRecord.price != price) diffs.add("   Price: ${dbRecord.price} -> $price")
                                if (dbRecord.quantity != quantity) diffs.add("   Quantity: ${dbRecord.quantity} -> $quantity")

                                if (diffs.isNotEmpty()) {
                                    logs.add("🔄 UPDATE VIP: $vipName")
                                    logs.addAll(diffs)
                                    VipDataSQL.update({ LowerCase(VipDataSQL.vipName) eq vipName }) {
                                        it[vipDiscord] = discord
                                        it[vipGroup] = group
                                        it[vipItems] = itemsStr
                                        it[vipCommands] = commandsStr
                                        it[vipPrice] = price
                                        it[vipQuantity] = quantity
                                    }
                                    updated++
                                } else {
                                    unchanged++
                                }
                            }
                        } catch (e: Exception) {
                            logs.add("❌ ERRO em $vipName: ${e.message}")
                            errors++
                        }
                    }
                }

                logs.add("RESUMO VIP: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ VIP SYNC OK!" else "⚠️ VIP SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO VIP: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-vipdata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }

        /**
         * Sincroniza WarpData
         */
        fun syncWarpDataToDatabase() {
            val logs = mutableListOf<String>()
            logs.add("=".repeat(70))
            logs.add("SINCRONIZAÇÃO WARPDATA - ${dateFormat.format(Date())}")
            logs.add("=".repeat(70))

            var inserted = 0
            var updated = 0
            var unchanged = 0
            var errors = 0

            try {
                transaction(TotalEssentials.getCore().sql) {
                    val locationSerializer = LocationSerializer()
                    val cacheKeys = WarpData.warpLocation.getMap().keys.toSet()

                    val dbRecords = WarpsDataSQL.selectAll()
                        .associate { row ->
                            row[WarpsDataSQL.warpNameTable] to WarpDbRecord(
                                location = row[WarpsDataSQL.warpLocationTable]
                            )
                        }

                    for (warpName in cacheKeys) {
                        try {
                            val location = WarpData.warpLocation[warpName]
                            val locationStr = locationSerializer.convertToDatabase(location) ?: ""
                            val dbRecord = dbRecords[warpName]

                            if (dbRecord == null) {
                                logs.add("➕ INSERT WARP: $warpName")
                                WarpsDataSQL.insert {
                                    it[warpNameTable] = warpName
                                    it[warpLocationTable] = locationStr
                                }
                                inserted++
                            } else {
                                if (dbRecord.location != locationStr) {
                                    logs.add("🔄 UPDATE WARP: $warpName - Location: DIFERENTE")
                                    WarpsDataSQL.update({ WarpsDataSQL.warpNameTable eq warpName }) {
                                        it[warpLocationTable] = locationStr
                                    }
                                    updated++
                                } else {
                                    unchanged++
                                }
                            }
                        } catch (e: Exception) {
                            logs.add("❌ ERRO em $warpName: ${e.message}")
                            errors++
                        }
                    }
                }

                logs.add("RESUMO WARP: ➕$inserted 🔄$updated ✓$unchanged ❌$errors")
                logs.add(if (errors == 0) "✅ WARP SYNC OK!" else "⚠️ WARP SYNC COM ERROS!")

            } catch (e: Exception) {
                logs.add("❌ ERRO CRÍTICO WARP: ${e.message}")
                e.printStackTrace()
            }

            saveLog(logs, "sync-warpdata")
            logs.forEach { TotalEssentials.getCore().logger.log(it) }
        }
        fun saveLog(lines: List<String>, prefix: String) {
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

    private data class ShopDbRecord(
        val visits: Int,
        val location: String,
        val open: Boolean
    )

    private data class KeyDbRecord(
        val vipName: String,
        val vipTime: Long
    )

    private data class KitDbRecord(
        val fakeName: String,
        val time: Long,
        val items: String,
        val weight: Int
    )

    private data class SpawnDbRecord(
        val location: String
    )

    private data class VipDbRecord(
        val discord: Long,
        val group: String,
        val items: String,
        val commands: String,
        val price: Int,
        val quantity: Int
    )

    private data class WarpDbRecord(
        val location: String
    )