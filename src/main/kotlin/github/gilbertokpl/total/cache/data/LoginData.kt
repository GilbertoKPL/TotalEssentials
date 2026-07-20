package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.ICache
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.sql.LoginDataSQL
import github.gilbertokpl.total.login.VelocityAuthBridge
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object LoginData : ICache {
    override var table: Table = LoginDataSQL
    override var primaryColumn: Column<String> = LoginDataSQL.player

    private val cache = TotalEssentials.getCore().getCache()

    val loginAttempts = cache.simpleInt()
    val values = cache.simpleInt()
    val isLoggedIn = cache.simpleBoolean()
    val password = cache.string(this, LoginDataSQL.password)
    val ipAddress = cache.string(this, LoginDataSQL.ip)

    fun isPlayerLoggedIn(playerName: String): Boolean {
        return isLoggedIn[playerName.lowercase()] == true
    }

    fun isPlayerLoggedIn(player: Player): Boolean {
        return isLoggedIn[player] == true
    }

    fun markLoggedIn(player: Player, notifyVelocity: Boolean = true) {
        isLoggedIn[player] = true
        if (notifyVelocity) VelocityAuthBridge.notifyAuthenticated(player)
    }

    fun doesPlayerExist(playerName: String): Boolean {
        return password[playerName.lowercase()] != null
    }

    fun doesPlayerExist(player: Player): Boolean {
        return password[player] != null
    }

    fun createNewLoginData(playerName: String, password: String, ipAddress: String) {
        val key = playerName.lowercase()

        try {
            transaction(TotalEssentials.getCore().sql) {
                LoginDataSQL.insert {
                    it[player] = key
                    it[ip] = ipAddress
                    it[LoginDataSQL.password] = password
                }
            }
        } catch (e: Exception) {
            TotalEssentials.getCore().logger.log("[ERROR] Erro ao criar login para $key: ${e.message}")
            e.printStackTrace()
        }

        isLoggedIn[key] = true
        this.password[key] = password
        this.ipAddress[key] = ipAddress
    }
}
