package github.gilbertokpl.essentialsk.config.get

import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.util.PermissionUtil
import org.simpleyaml.configuration.file.YamlFile
import java.util.stream.Collectors

object GetValues {

    private val lowercaseValues = listOf(
        "nicks.blocked-nicks",
        "homes.block-worlds",
        "vanish.blocked-other-cmds",
        "back.disabled-worlds",
        "fly.disabled-worlds",
        "containers.block-shift",
        "containers.block-open",
        "discordbot.command-chat"
    )

    fun getString(source: YamlFile, path: String, color: Boolean = true): String? {
        return if (color) {
            PermissionUtil.colorPermission(null, source.getString(path))
                .replace("%prefix%", OtherConfig.serverPrefix)
        } else source.getString(path)
    }

    fun getStringList(source: YamlFile, path: String, color: Boolean = true): List<String> {
        if (lowercaseValues.contains(path)) {
            return source.getStringList(path).stream().map { to -> to.lowercase() }.collect(Collectors.toList())
        }
        return if (color) {
            source.getStringList(path).stream()
                .map { to -> PermissionUtil.colorPermission(null, to).replace("%prefix%", OtherConfig.serverPrefix) }
                .collect(Collectors.toList())
        } else source.getStringList(path)
    }

    fun getInt(source: YamlFile, path: String): Int = source.getInt(path)

    fun getBoolean(source: YamlFile, path: String): Boolean = source.getBoolean(path)
}