package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializerBase
import org.bukkit.Location

class LocationSerializer : SerializerBase<Location?, String> {
    override fun convertToDatabase(hash: Location?): String {
        hash ?: return ""
        return hash.x.toString() + ";" +
                hash.y.toString() + ";" +
                hash.z.toString() + ";" +
                hash.world?.name + ";" +
                hash.pitch + ";" +
                hash.yaw
    }

    override fun convertToCache(value: String): Location? {

        if (value == "") return null

        return try {
            val parts = value.split(";").toTypedArray()
            val x = if (parts[0].isEmpty()) 0.0 else parts[0].toDouble()
            val y = if (parts[1].isEmpty()) 0.0 else parts[1].toDouble()
            val z = if (parts[2].isEmpty()) 0.0 else parts[2].toDouble()
            val w = try {
                github.gilbertokpl.total.TotalEssentialsJava.instance.server.getWorld(parts[3])
            } catch (e: Throwable) {
                e.printStackTrace()
                github.gilbertokpl.total.TotalEssentialsJava.instance.server.getWorld("world")
            }
            Location(
                w, x, y, z, try {
                    parts[5].toFloat()
                } catch (e: Throwable) {
                    0F
                }, try {
                    parts[4].toFloat()
                } catch (e: Throwable) {
                    0F
                }
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}