package github.gilbertokpl.total.util

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.World
import org.bukkit.entity.Item

object WorldUtil {
    fun clearItems() {
        for (w in MainConfig.ClearitemsWorlds) {
            val world: World
            try {
                world = TotalEssentials.instance.server.getWorld(w)!!
            } catch (ex: Exception) {
                continue
            }
            val t = world.entities
            for (e in 0..t.size) {
                val i = t[e]
                println(i.type.key.key)
                if (i is Item && !MainConfig.ClearitemsItemsNotClear.contains(i.type.key.key.lowercase())) {
                    i.remove()
                }
            }
        }
    }

}