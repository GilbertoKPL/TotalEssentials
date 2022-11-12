package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.sql.KitsDataSQL
import github.gilbertokpl.total.cache.sql.LoginDataSQL
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object LoginData : CacheBase {
    override var table: Table = LoginDataSQL
    override var primaryColumn: Column<String> = LoginDataSQL.player

    private val ins = TotalEssentials.basePlugin.getCache()

    val attempts = ins.simpleInteger()
    val values = ins.simpleInteger()
    val loggedIn = ins.simpleBoolean()
    val password = ins.string(this, LoginDataSQL.password)
    val ip = ins.string(this, LoginDataSQL.ip)

    fun checkIfPlayerIsLoggedIn(entity: String) : Boolean {
        return loggedIn[entity.lowercase()] == true
    }
    fun checkIfPlayerIsLoggedIn(p: Player) : Boolean {
        return loggedIn[p] == true
    }

    fun checkIfPlayerExist(entity: String) : Boolean {
        return password[entity.lowercase()] != null
    }

    fun checkIfPlayerExist(entity: Player) : Boolean {
        return password[entity] != null
    }

    fun createNewLoginData(entity: String, pass : String, i: String) {
        loggedIn[entity] = true
        password[entity] = pass
        ip[entity] = i
    }
}