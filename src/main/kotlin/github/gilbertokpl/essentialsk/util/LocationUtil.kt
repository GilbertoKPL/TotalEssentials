package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*


class LocationUtil {
    fun locationSerializer(loc: Location): String {
        return loc.x.toString().replace(".", ",") + ";" + loc.y.toString().replace(".", ",") + ";" + loc.z.toString().replace(".", ",") + ";" + loc.world?.uid.toString().replace("-", "_")
    }

    fun locationSerializer(s: String): Location {
        val parts = s.split(";").toTypedArray()
        val x = parts[0].replace(",", ".").toDouble()
        val y = parts[1].replace(",", ".").toDouble()
        val z = parts[2].replace(",", ".").toDouble()
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