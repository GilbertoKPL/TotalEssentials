package github.gilbertokpl.total.util

import gilbertokpl.mcpctotal.addons.ActionBarAdaptor
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.PlayerData
import github.gilbertokpl.total.cache.data.ShopData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Field
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.Locale.getDefault

object PlayerUtil {

    private const val MAX_PLAYTIME_MS = 94608000000L // 3 anos em milissegundos
    private const val LOCALHOST_IP = "127.0.0.1"

    var title: Boolean = true

    private val reflectionCache = ReflectionCache()

    /**
     * Teleporta um jogador de forma segura, usando teleportAsync se disponível
     */
    fun Player.teleportSafe(location: Location) {
        reflectionCache.teleportPlayer(this, location)
    }

    /**
     * Mandar TITLE todas versões
     */
    fun Player.title(t: String, subtitle: String) {
        if (!title) return
        try {
            this.sendTitle(t, subtitle)
        } catch (e: Exception) {
            title = false
        }

    }

    /**
     * Mandar SOUND todas versões
     */
    fun Player.sound(sound: String) {
        try {
            this.playSound(this.location, sound, 1f, 1f)
        } catch (e: Exception) {
            try {
                this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
            }
            catch (e: Exception) {}
        }
    }

    /**
     * Envia mensagem para todos os jogadores online
     */
    fun sendAllSound(sound: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach { player ->
            player.sound(sound)
        }
    }

    /**
     * Envia mensagem para todos os jogadores online
     */
    fun sendAllMessage(message: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach { player ->
            player.sendMessage(message)
        }
    }

    /**
     * Envia mensagem para todos os jogadores online
     */
    fun sendAllAction(message: String) {
        TotalEssentials.getCore().getReflection().getPlayers().forEach { player ->
            try {
                player.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent(message)
                )
            } catch (e: NoClassDefFoundError) {
                try {
                    ActionBarAdaptor.smallTitle(player, message, 20, 100, 20)
                }catch (e: NoClassDefFoundError) {}
            }
        }
    }

    /**
     * Envia mensagem para um jogador específico pelo nome
     */
    fun sendMessage(playerName: String, message: String) {
        val player = TotalEssentials.getInstance().server.getPlayerExact(playerName.lowercase())
        player?.sendMessage(message)
    }

    /**
     * Retorna a quantidade de jogadores online
     * @param includeVanished se deve incluir jogadores em vanish na contagem
     */
    fun getOnlinePlayersCount(includeVanished: Boolean = true): Int {
        val players = TotalEssentials.getCore().getReflection().getPlayers()

        if (includeVanished) {
            return players.size
        }

        return players.count { player ->
            PlayerData.vanishCache[player] != true
        }
    }

    /**
     * Salva o playtime de todos os jogadores online
     */
    fun savePlaytime() {
        if (!MainConfig.playtimeActivated) return

        val currentTime = System.currentTimeMillis()

        TotalEssentials.getCore().getReflection().getPlayers().forEach { player ->
            updatePlayerPlaytime(player, currentTime)
        }
    }

    private fun updatePlayerPlaytime(player: Player, currentTime: Long) {
        val previousPlaytime = PlayerData.playTimeCache[player] ?: 0L
        val sessionStart = PlayerData.playtimeLocal[player] ?: currentTime

        val sessionDuration = currentTime - sessionStart
        var totalPlaytime = previousPlaytime + sessionDuration

        // Limita o playtime máximo
        if (totalPlaytime > MAX_PLAYTIME_MS) {
            totalPlaytime = MAX_PLAYTIME_MS
        }

        PlayerData.playTimeCache[player] = totalPlaytime
        PlayerData.playtimeLocal[player] = currentTime
    }

    /**
     * Converte GameMode para número (compatibilidade com versões antigas)
     */
    fun getGameModeAsNumber(gameMode: GameMode): Int {
        return when (gameMode) {
            GameMode.SURVIVAL -> 0
            GameMode.CREATIVE -> 1
            GameMode.ADVENTURE -> 2
            GameMode.SPECTATOR -> 3
            else -> 0 // Fallback para versões antigas
        }
    }

    /**
     * Converte número/string para GameMode
     */
    fun getGameModeFromString(input: String): GameMode {
        return when (input.lowercase()) {
            "0", "survival" -> GameMode.SURVIVAL
            "1", "creative" -> GameMode.CREATIVE
            "2", "adventure" -> safeGameMode(GameMode.ADVENTURE)
            "3", "spectator", "spectactor" -> safeGameMode(GameMode.SPECTATOR)
            else -> GameMode.SURVIVAL
        }
    }

    private fun safeGameMode(gameMode: GameMode): GameMode {
        return try {
            gameMode
        } catch (e: NoSuchFieldError) {
            GameMode.SURVIVAL
        }
    }

    /**
     * Verifica informações de IP usando ip-api.com
     * @return Lista com [País, Estado, Cidade, isVPN]
     */
    fun checkPlayerIP(ipAddress: String): List<String> {
        if (ipAddress.contains(LOCALHOST_IP)) {
            return listOf("País: Local", "Estado: Local", "Cidade: Local", "false")
        }

        return try {
            fetchIPInfo(ipAddress)
        } catch (e: Exception) {
            listOf("Erro API", "Erro API", "Erro API", "false")
        }
    }

    private fun fetchIPInfo(ipAddress: String): List<String> {
        val apiUrl = URL("http://ip-api.com/json/$ipAddress?fields=status,country,regionName,city,hosting")

        apiUrl.openStream().use { inputStream ->
            val jsonContent = BufferedReader(
                InputStreamReader(inputStream, StandardCharsets.UTF_8)
            ).use { it.readText() }

            return parseIPApiResponse(jsonContent)
        }
    }

    private fun parseIPApiResponse(json: String): List<String> {
        if (json.contains("\"status\":\"fail\"")) {
            return listOf("Erro API", "Erro API", "Erro API", "false")
        }

        val country = extractJsonValue(json, "country")
        val region = extractJsonValue(json, "regionName")
        val city = extractJsonValue(json, "city")
        val hosting = extractJsonBooleanValue(json, "hosting")

        return listOf(
            "País: $country",
            "Estado: $region",
            "Cidade: $city",
            hosting
        )
    }

    private fun extractJsonBooleanValue(json: String, key: String): String {
        val pattern = "\"$key\":"
        val startIndex = json.indexOf(pattern)
        if (startIndex == -1) return "false"
        val valueStart = startIndex + pattern.length
        val remaining = json.substring(valueStart).trim()
        return if (remaining.startsWith("true")) "true" else "false"
    }

    private fun extractJsonValue(json: String, key: String): String {
        val startMarker = "\"$key\":\""
        val startIndex = json.indexOf(startMarker) + startMarker.length
        val endIndex = json.indexOf("\"", startIndex)

        return if (startIndex > startMarker.length && endIndex > startIndex) {
            json.substring(startIndex, endIndex)
        } else {
            "Unknown"
        }
    }

    /**
     * Teleporta jogador para uma loja
     */
    fun shopTeleport(player: Player, shopOwner: String) {
        val location = ShopData.shopLocation[shopOwner] ?: return

        teleportWithDelay(
            player = player,
            location = location,
            delaySeconds = MainConfig.homesTimeToTeleport,
            message = LangConfig.shopTeleport.replace("%player%", shopOwner),
            locationName = "shop"
        )
    }

    /**
     * Retorna URL do avatar do jogador
     */
    fun getMojangSkinURL(player: Player): String {
        return "https://minotar.net/avatar/${player.name.lowercase()}"
    }

    /**
     * Teleporta jogador com delay configurável
     */
    fun teleportWithDelay(
        player: Player,
        location: Location,
        delaySeconds: Int,
        message: String? = null,
        locationName: String
    ) {
        // Bypass para jogadores com permissão ou delay 0
        if (player.hasPermission("totalessentials.bypass.teleport") || delaySeconds == 0) {
            player.teleportSafe(location)
            message?.let { player.sendMessage(it) }
            return
        }

        // Verifica se já está em teleporte
        if (PlayerData.inTeleport[player] == true) {
            player.sendMessage(LangConfig.generalInTeleport)
            return
        }

        scheduleDelayedTeleport(player, location, delaySeconds, message, locationName)
    }

    private fun scheduleDelayedTeleport(
        player: Player,
        location: Location,
        delaySeconds: Int,
        message: String?,
        locationName: String
    ) {
        PlayerData.inTeleport[player] = true

        val task = TotalEssentials.getCore().getTask()

        task.supplyLater(delaySeconds.toLong()) {
            PlayerData.inTeleport[player] = false

            task.sync {
                player.teleportSafe(location)
                message?.let { player.sendMessage(it) }
            }
        }

        player.sendMessage(
            LangConfig.generalTimeToTeleport
                .replace("%local%", locationName)
                .replace("%time%", delaySeconds.toString())
        )
    }

    /**
     * Define o display name de um jogador (compatível com versões antigas)
     */
    fun setDisplayName(player: Player, displayName: String?) {
        reflectionCache.setPlayerDisplayName(player, displayName)
    }

    /**
     * Define o item na mão principal (compatível com versões antigas)
     */
    fun setItemInMainHand(player: Player, item: ItemStack?) {
        reflectionCache.setPlayerItemInHand(player, item)
    }

    /**
     * Cache de métodos de reflection para melhor performance
     */
    private class ReflectionCache {
        private val teleportAsyncMethod by lazy { findTeleportAsyncMethod() }
        private var useLegacyDisplayName = false
        private var useLegacyItemInHand = false

        private val displayNameField by lazy {
            findField(Player::class.java, "displayName")
        }

        private val itemInHandField by lazy {
            findField(Player::class.java, "itemInHand")
        }

        fun teleportPlayer(player: Player, location: Location) {
            teleportAsyncMethod?.let { method ->
                try {
                    method.invoke(player, location)
                    return
                } catch (e: Exception) {
                    // Fallback para teleport normal
                }
            }

            player.teleport(location)
        }

        fun setPlayerDisplayName(player: Player, displayName: String?) {
            if (!useLegacyDisplayName) {
                try {
                    player.setDisplayName(displayName)
                    return
                } catch (e: NoSuchMethodError) {
                    useLegacyDisplayName = true
                }
            }

            displayNameField?.set(player, displayName)
        }

        fun setPlayerItemInHand(player: Player, item: ItemStack?) {
            if (!useLegacyItemInHand) {
                try {
                    @Suppress("DEPRECATION")
                    player.setItemInHand(item)
                    return
                } catch (e: NoSuchMethodError) {
                    useLegacyItemInHand = true
                }
            }

            itemInHandField?.set(player, item)
        }

        private fun findTeleportAsyncMethod() = try {
            Player::class.java.getMethod("teleportAsync", Location::class.java)
        } catch (e: NoSuchMethodException) {
            null
        }

        private fun findField(clazz: Class<*>, fieldName: String): Field? {
            return try {
                clazz.getDeclaredField(fieldName).apply { isAccessible = true }
            } catch (e: NoSuchFieldException) {
                null
            }
        }
    }
}