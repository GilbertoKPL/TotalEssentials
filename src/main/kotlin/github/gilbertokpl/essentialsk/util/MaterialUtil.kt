package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Material

class MaterialUtil {

    fun startMaterials() {
        Dao.getInstance().material["glass"] =
            materialHelper(listOf("STAINED_GLASS_PANE", "THIN_GLASS", "YELLOW_STAINED_GLASS"))
        Dao.getInstance().material["clock"] = materialHelper(listOf("CLOCK", "WATCH"))
        Dao.getInstance().material["feather"] = materialHelper(listOf("FEATHER"))
        Dao.getInstance().material["soil"] =
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

    companion object : IInstance<MaterialUtil> {
        private val instance = createInstance()
        override fun createInstance(): MaterialUtil = MaterialUtil()
        override fun getInstance(): MaterialUtil {
            return instance
        }
    }
}