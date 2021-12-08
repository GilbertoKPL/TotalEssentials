package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*


class LocationUtil {
    fun locationSerializer(loc: Location): String {
        return loc.x.toString().split(".").first() + ";" + loc.y.toString().split(".").first() + ";" + loc.z.toString()
            .split(".").first() + ";" + loc.world?.uid.toString().replace("-", "_")
    }

    fun locationSerializer(s: String): Location {
        val parts = s.split(";").toTypedArray()
        val x = parts[0].toDouble()
        val y = parts[1].toDouble()
        val z = parts[2].toDouble()
        val u = UUID.fromString(parts[3].replace("_", "-"))
        val w = Bukkit.getServer().getWorld(u)
        return Location(w, x, y, z)
    }

    companion object : IInstance<LocationUtil> {
        private val instance = createInstance()
        override fun createInstance(): LocationUtil = LocationUtil()
        override fun getInstance(): LocationUtil {
            return instance
        }
    }
}