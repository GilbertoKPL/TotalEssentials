package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.sql.LoginDataSQL
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object LoginData : CacheBase {
    override var table: Table = LoginDataSQL
    override var primaryColumn: Column<String> = LoginDataSQL.player

    private val cache = TotalEssentialsJava.basePlugin.getCache()

    val loginAttempts = cache.simpleInteger()
    val values = cache.simpleInteger()
    val isLoggedIn = cache.simpleBoolean()
    val password = cache.string(this, LoginDataSQL.password)
    val ipAddress = cache.string(this, LoginDataSQL.ip)

    fun isPlayerLoggedIn(playerName: String): Boolean {
        return isLoggedIn[playerName.lowercase()] == true
    }

    fun isPlayerLoggedIn(player: Player): Boolean {
        return isLoggedIn[player] == true
    }

    fun doesPlayerExist(playerName: String): Boolean {
        return password[playerName.lowercase()] != null
    }

    fun doesPlayerExist(player: Player): Boolean {
        return password[player] != null
    }

    fun createNewLoginData(playerName: String, password: String, ipAddress: String) {
        isLoggedIn[playerName] = true
        this.password[playerName] = password
        this.ipAddress[playerName] = ipAddress
    }
}