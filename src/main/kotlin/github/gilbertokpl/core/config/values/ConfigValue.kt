package github.gilbertokpl.core.config.values

import github.gilbertokpl.core.TotalCore
import org.simpleyaml.configuration.file.YamlFile

class ConfigValue(private val totalCore: TotalCore) {
    fun getString(source: YamlFile, path: String, color: Boolean = true): String? {
        val value = source.getString(path)
        return if (color && value != null) {
            totalCore.getColor().rgbHex(null, value).replace("%prefix%", totalCore.serverPrefix)
        } else value
    }

    fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        val list = source.getStringList(path)
        return if (color && list.isNotEmpty()) {
            list.map { totalCore.getColor().rgbHex(null, it).replace("%prefix%", totalCore.serverPrefix) }
        } else list
    }

    fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)
}