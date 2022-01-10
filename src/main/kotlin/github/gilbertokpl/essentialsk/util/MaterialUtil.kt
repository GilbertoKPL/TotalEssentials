package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.data.DataManager
import org.bukkit.Material

object MaterialUtil {

    fun startMaterials() {
        DataManager.material["glass"] =
            materialHelper(listOf("STAINED_GLASS_PANE", "THIN_GLASS", "YELLOW_STAINED_GLASS"))
        DataManager.material["clock"] = materialHelper(listOf("CLOCK", "WATCH"))
        DataManager.material["feather"] = materialHelper(listOf("FEATHER"))
        DataManager.material["soil"] =
            materialHelper(listOf("SOIL", "SOUL_SOIL"))
    }

    private fun materialHelper(material: List<String>): Material {
        var mat = Material.AIR
        for (i in material) {
            val toPut = Material.getMaterial(i)
            if (toPut != null) {
                mat = toPut
                break
            }
        }
        return mat
    }
}
