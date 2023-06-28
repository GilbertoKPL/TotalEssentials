package github.gilbertokpl.total.cache.local

import github.gilbertokpl.core.external.cache.interfaces.CacheBase
import github.gilbertokpl.total.cache.sql.VipKeysSQL
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

object KeyData : CacheBase {
    override var table: Table = VipKeysSQL
    override var primaryColumn: Column<String> = VipKeysSQL.vipKey

    private val ins = github.gilbertokpl.total.TotalEssentials.basePlugin.getCache()

    val vipName = ins.string(this, VipKeysSQL.vipName)
    val vipTime = ins.long(this, VipKeysSQL.vipTime)

    fun checkIfKeyExist(entity: String): Boolean {
        return vipName[entity] != null
    }

    fun generateRandomString(): String {
        return UUID.randomUUID().toString().replace("-", "").uppercase().substring(0, 10)
    }

    fun genNewVipKey(name: String, time: Long): String {
        var key: String

        do {
            key = generateRandomString()
        } while (checkIfKeyExist(key))

        vipName[key] = name
        vipTime[key] = time

        return key
    }

    fun remove(entity: String) {
        vipName.remove(entity)
        vipTime.remove(entity)
    }
}