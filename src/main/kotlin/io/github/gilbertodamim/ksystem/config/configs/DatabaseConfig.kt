package io.github.gilbertodamim.ksystem.config.configs

import io.github.gilbertodamim.ksystem.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object DatabaseConfig {
    lateinit var sqlType: String
    lateinit var sqlIp: String
    lateinit var sqlPort: String
    lateinit var sqlUsername: String
    lateinit var sqlDatabase: String
    lateinit var sqlPassword: String
    lateinit var langName: String

    fun reload(source1: YamlConfiguration) {
        langName = ConfigMain.getString(source1, "Lang", false)
        sqlType = ConfigMain.getString(source1, "Database.type", false)
        sqlIp = ConfigMain.getString(source1, "Database.connect.ip", false)
        sqlPort = ConfigMain.getString(source1, "Database.connect.port", false)
        sqlUsername = ConfigMain.getString(source1, "Database.connect.username", false)
        sqlDatabase = ConfigMain.getString(source1, "Database.connect.database", false)
        sqlPassword = ConfigMain.getString(source1, "Database.connect.pass", false)
    }
}