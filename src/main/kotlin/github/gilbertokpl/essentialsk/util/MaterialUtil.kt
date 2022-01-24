package github.gilbertokpl.essentialsk.util

import org.bukkit.Material

internal object MaterialUtil {

    private val material = HashMap<String, Material>(10)

    operator fun get(mat: String) = material[mat.lowercase()]

    fun startMaterials() {
        material["glass"] =
            materialHelper(listOf("STAINED_GLASS_PANE", "THIN_GLASS", "YELLOW_STAINED_GLASS"))
        material["clock"] = materialHelper(listOf("CLOCK", "WATCH"))
        material["feather"] = materialHelper(listOf("FEATHER"))
        material["soil"] =
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
