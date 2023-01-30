package github.gilbertokpl.total.cache.sql

import github.gilbertokpl.total.config.files.MainConfig
import org.jetbrains.exposed.sql.Table

object ShopDataSQL : Table("ShopData" + MainConfig.databaseManager) {
    val playerTable = varchar("name", 64)
    val visits = integer("visits").default(0)
    val location = text("location").default("")
    val open = bool("open").default(true)
    override val primaryKey = PrimaryKey(PlayerDataSQL.playerTable)
}