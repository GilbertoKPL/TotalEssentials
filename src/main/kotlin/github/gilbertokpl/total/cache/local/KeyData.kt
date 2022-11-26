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

    fun checkIfKeyExist(entity: String) : Boolean {
        return vipName[entity] != null
    }

    private fun getRandomString() : String {
        return Random().ints(97, 122 + 1)
            .limit(10)
            .collect({ StringBuilder() }, java.lang.StringBuilder::appendCodePoint, java.lang.StringBuilder::append)
            .toString().uppercase()
    }

    fun genNewVipKey(name: String, time: Long) : String {
        var key = getRandomString()

        if (checkIfKeyExist(key)) {
            key = getRandomString()
        }

        vipName[key] = name
        vipTime[key] = time

        return key
    }

    fun delete(entity: String) {
        vipName.delete(entity)
        vipTime.delete(entity)
    }
}