package github.gilbertokpl.total.cache.data

import github.gilbertokpl.core.cache.interfaces.CacheBase
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.sql.LoginDataSQL
import org.bukkit.entity.Player
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

object LoginData : CacheBase {
    override var table: Table = LoginDataSQL
    override var primaryColumn: Column<String> = LoginDataSQL.player

    private val cache = TotalEssentials.getCore().getCache()

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