package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.builder.EntityExistenceCache
import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.serializer.*
import github.gilbertokpl.total.cache.sql.PlayerDataSQL
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.vip.VipManager
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object PlayerData : ICache {
    override var table: Table = PlayerDataSQL
    override var primaryColumn: Column<String> = PlayerDataSQL.playerTable

    private val ins = TotalEssentials.getCore().getCache()

    val kitsCache = ins.hashMap(this, PlayerDataSQL.kitsTable, KitSerializer())
    val homeCache = ins.hashMap(this, PlayerDataSQL.homeTable, HomeSerializer())
    val vipCache = ins.hashMap(this, PlayerDataSQL.vipTable, VipSerializer())
    val vipItems = ins.list(this, PlayerDataSQL.vipItems, ItemSerializer())
    val nickCache = ins.string(this, PlayerDataSQL.nickTable)
    val gameModeCache = ins.int(this, PlayerDataSQL.gameModeTable)
    val vanishCache = ins.boolean(this, PlayerDataSQL.vanishTable)
    val lightCache = ins.boolean(this, PlayerDataSQL.lightTable)
    val flyCache = ins.boolean(this, PlayerDataSQL.flyTable)
    val backLocation = ins.location(this, PlayerDataSQL.backTable, LocationSerializer())
    val speedCache = ins.int(this, PlayerDataSQL.speedTable)
    val moneyCache = ins.double(this, PlayerDataSQL.moneyTable)
    val discordCache = ins.long(this, PlayerDataSQL.DiscordTable)
    val playTimeCache = ins.long(this, PlayerDataSQL.PlaytimeTable)
    val colorCache = ins.string(this, PlayerDataSQL.colorTable)
    val commandCache = ins.string(this, PlayerDataSQL.CommandTable)
    val limiterItemCache = ins.hashMap(this, PlayerDataSQL.LimiterItemTable, LimiterItemSerializer())
    val limiterLocationCache = ins.hashMap(this, PlayerDataSQL.LimiterLocationTable, LimiterLocationSerializer())
    val inInvSee = ins.simplePlayer()
    val homeLimitCache = ins.simpleInt()
    val inTeleport = ins.simpleBoolean()
    val afk = ins.simpleInt()
    val playtimeLocal = ins.simpleLong()
    val playerInfo = ins.simpleList<String>()

    /**
     * Verifica se o jogador existe no banco de dados.
     * Usa o moneyCache como referência pois sempre tem valor.
     */
    fun checkIfPlayerExists(entity: String): Boolean {
        val key = entity.lowercase()
        // Primeiro verifica no cache de existência centralizado
        if (EntityExistenceCache.exists(table, key)) {
            return true
        }
        // Fallback para o cache local
        return moneyCache[key] != null
    }

    fun checkIfPlayerExists(entity: Player): Boolean {
        return checkIfPlayerExists(entity.name)
    }

    /**
     * Cria dados de economia para um jogador (apenas money).
     * Usado quando o jogador já existe mas não tem money.
     */
    fun createNewPlayerEco(entity: String) {
        moneyCache[entity] = MainConfig.moneyDefault?.toDouble() ?: 0.0
    }

    /**
     * Cria um novo registro COMPLETO no banco de dados para um jogador novo.
     *
     * IMPORTANTE: Este método faz INSERT direto no banco com TODAS as colunas,
     * evitando o problema de INSERT parcial que zera outras colunas.
     *
     * Os builders só fazem UPDATE, nunca INSERT.
     */
    fun createNewPlayerData(entity: String) {
        val key = entity.lowercase()

        // Verifica se já existe para evitar duplicação
        if (checkIfPlayerExists(key)) {
            TotalEssentials.getCore().logger.log("[WARN] Tentativa de criar jogador que já existe: $key")
            return
        }

        val defaultLocation = SpawnData.spawnLocation["spawn"]
            ?: Location(TotalEssentials.getInstance()?.server?.getWorld("world"), 1.0, 1.0, 1.0)

        val defaultMoney = MainConfig.moneyDefault?.toDouble() ?: 0.0

        // Serializers para converter os valores
        val kitSerializer = KitSerializer()
        val homeSerializer = HomeSerializer()
        val vipSerializer = VipSerializer()
        val itemSerializer = ItemSerializer()
        val locationSerializer = LocationSerializer()
        val limiterItemSerializer = LimiterItemSerializer()
        val limiterLocationSerializer = LimiterLocationSerializer()

        try {
            transaction(TotalEssentials.getCore().sql) {
                // INSERT completo com TODAS as colunas
                PlayerDataSQL.insert {
                    it[playerTable] = key
                    it[kitsTable] = kitSerializer.convertToDatabase(hashMapOf())
                    it[homeTable] = homeSerializer.convertToDatabase(hashMapOf())
                    it[vipTable] = vipSerializer.convertToDatabase(hashMapOf())
                    it[vipItems] = itemSerializer.convertToDatabase(arrayListOf())
                    it[nickTable] = ""
                    it[gameModeTable] = 0
                    it[vanishTable] = false
                    it[lightTable] = false
                    it[flyTable] = false
                    it[backTable] = locationSerializer.convertToDatabase(defaultLocation) ?: ""
                    it[speedTable] = 1
                    it[moneyTable] = defaultMoney
                    it[DiscordTable] = 0L
                    it[PlaytimeTable] = 0L
                    it[colorTable] = ""
                    it[CommandTable] = ""
                    it[LimiterItemTable] = limiterItemSerializer.convertToDatabase(hashMapOf())
                    it[LimiterLocationTable] = limiterLocationSerializer.convertToDatabase(hashMapOf())
                }

                // Marca como existente no cache centralizado
                EntityExistenceCache.markAsExisting(table, key)

                // Atualiza os caches em memória
                kitsCache[key] = hashMapOf()
                homeCache[key] = hashMapOf()
                vipCache[key] = hashMapOf()
                vipItems[key] = arrayListOf()
                nickCache[key] = ""
                gameModeCache[key] = 0
                vanishCache[key] = false
                lightCache[key] = false
                flyCache[key] = false
                backLocation[key] = defaultLocation
                speedCache[key] = 1
                moneyCache[key] = defaultMoney
                afk[key] = 1
                playTimeCache[key] = 0
                discordCache[key] = 0
                colorCache[key] = ""
                commandCache[key] = ""
                limiterItemCache[key] = hashMapOf()
                limiterLocationCache[key] = hashMapOf()
            }

            TotalEssentials.getCore().logger.log("[INFO] Novo jogador criado: $key")

        } catch (e: Exception) {
            TotalEssentials.getCore().logger.log("[ERROR] Erro ao criar jogador $key: ${e.message}")
            e.printStackTrace()
        }
    }

    fun applyPlayerSettings(p: Player) {
        afk[p] = 1

        nickCache[p]?.let { nick ->
            if (nick.isNotEmpty() && nick != p.displayName && p.hasPermission("totalessentials.commands.nick")) {
                PlayerUtil.setDisplayName(p, nick)
            }
        }

        gameModeCache[p]?.let { gameModeNumber ->
            val gameModeName = PlayerUtil.getGameModeFromString(gameModeNumber.toString())
            if (p.gameMode != gameModeName && (gameModeName == GameMode.SURVIVAL || p.hasPermission("totalessentials.commands.gamemode"))) {
                p.gameMode = gameModeName
            }
        }

        lightCache[p]?.takeIf { it }?.let {
            p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 1))
        }

        flyCache[p]?.takeIf { it }?.let {
            p.allowFlight = true
            p.isFlying = true
        }

        speedCache[p]?.takeIf { it != 1 }?.let { speed ->
            val speedValue = (speed * 0.1).toFloat()
            p.walkSpeed = speedValue
            p.flySpeed = speedValue
        }

        playTimeCache[p]?.let { playTime ->
            if (playTime > 31_557_600_000) {
                playTimeCache[p] = 0
            }
        }

        commandCache[p]?.takeIf { it.isNotEmpty() }?.let { commands ->
            commands.split(" -").forEach { command ->
                TotalEssentials.getInstance().server.dispatchCommand(
                    TotalEssentials.getInstance().server.consoleSender,
                    command
                )
            }
            commandCache[p] = ""
            VipManager.updateCargo(p.name.lowercase())
        }

        // Otimização: combina as duas iterações em uma só
        val isPlayerVanished = vanishCache[p] ?: false
        val shouldCheckOthersVanish = MainConfig.vanishActivated &&
            !p.hasPermission("totalessentials.commands.vanish") &&
            !p.hasPermission("totalessentials.bypass.vanish")

        if (isPlayerVanished || shouldCheckOthersVanish) {
            val players = TotalEssentials.getCore().getReflection().getPlayers()

            if (isPlayerVanished) {
                p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1))
            }

            players.forEach { otherPlayer ->
                // Se o player está vanished, esconde dele os outros sem permissão
                if (isPlayerVanished) {
                    otherPlayer.player?.takeIf {
                        !it.hasPermission("totalessentials.commands.vanish") &&
                        !it.hasPermission("totalessentials.bypass.vanish")
                    }?.hidePlayer(p)
                }

                // Se o outro player está vanished, esconde dele
                if (shouldCheckOthersVanish) {
                    vanishCache[otherPlayer]?.takeIf { it }?.let {
                        p.hidePlayer(otherPlayer)
                    }
                }
            }
        }
    }
}
