package github.gilbertokpl.total.cache.serializer

import github.gilbertokpl.core.external.cache.convert.SerializatorBase
import org.bukkit.Location

class LocationSerializer : SerializatorBase<Location?, String> {
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
        return try {
            val parts = value.split(";").toTypedArray()
            val x = parts[0].toDouble()
            val y = parts[1].toDouble()
            val z = parts[2].toDouble()
            val w = try {
                github.gilbertokpl.total.TotalEssentials.instance.server.getWorld(parts[3])
            } catch (e: Throwable) {
                null
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
            null
        }
    }
}