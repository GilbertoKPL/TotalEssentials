package github.gilbertokpl.total.cache.sql

import github.gilbertokpl.total.config.files.MainConfig
import org.jetbrains.exposed.sql.Table

object VipDataSQL : Table("VipData" + MainConfig.databaseManager) {
    val vipName = varchar("vipName", 16)
    val vipDiscord = long("vipDiscord").default(0)
    val vipGroup = varchar("vipGroup", 16).default("")
    val vipItems = text("vipItems").default("")
    val vipPrice = integer("vipPrice").default(0)
    val vipQuantity = integer("vipQuantity").default(0)
    override val primaryKey = PrimaryKey(vipName)
}