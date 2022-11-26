package github.gilbertokpl.total.cache.sql

import github.gilbertokpl.total.config.files.MainConfig
import org.jetbrains.exposed.sql.Table

object VipKeysSQL : Table("VipKeys" + MainConfig.databaseManager) {
    val vipKey = varchar("vipKey", 16)
    val vipName = varchar("vipName", 16)
    val vipTime = long("vipDays").default(30)
    override val primaryKey = PrimaryKey(vipKey)
}