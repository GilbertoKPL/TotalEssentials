package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.manager.IInstance
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.json.JSONObject
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption


class VersionUtil {
    fun check(): Boolean {
        val checkJson = JSONObject(IOUtils.toString(URL("https://pastebin.com/raw/GbxhP7qM"), "UTF-8"))

        PluginUtil.getInstance()
            .consoleMessage(StartLang.getInstance().startVerification.replace("%to%", "versão do plugin"))
        val versionJson = checkJson.get("version") as String
        val versionPlugin = ManifestUtil.getInstance().getManifestValue("Plugin-Version") ?: return false

        try {
            if (versionJson.toDouble() > versionPlugin.toDouble()) {
                PluginUtil.getInstance()
                    .consoleMessage(StartLang.getInstance().updatePlugin.replace("%version%", versionJson))

                val newJar =
                    PluginUtil.getInstance().fileDownloader(checkJson.get("download") as String) ?: return false

                val pluginPath = File(PluginUtil.getInstance().pluginPath)

                var pluginRealPath = File(
                    pluginPath.path.replace(
                        pluginPath.name,
                        "${ManifestUtil.getInstance().getManifestValue("Plugin-Name")}-$versionJson.jar"
                    )
                )

                if (pluginPath.name == pluginRealPath.name) {
                    pluginRealPath = File(pluginPath.path.replace(".jar", "-debug.jar"))
                }

                Files.copy(newJar, pluginRealPath.toPath(), StandardCopyOption.REPLACE_EXISTING)

                PluginUtil.getInstance().consoleMessage(StartLang.getInstance().updatePluginMessage)

                try {
                    PluginUtil.getInstance().unloadPlugin(EssentialsK.instance)
                } finally {
                    pluginPath.delete()
                    EssentialsK.instance.server.dispatchCommand(EssentialsK.instance.server.consoleSender, "restart")
                }
                return true
            }
            PluginUtil.getInstance().consoleMessage(StartLang.getInstance().completeVerification)
            return false
        } catch (ex: Exception) {
            FileLoggerUtil.getInstance().logError(ExceptionUtils.getStackTrace(ex))
        }
        return false
    }

    companion object : IInstance<VersionUtil> {
        private val instance = createInstance()
        override fun createInstance(): VersionUtil = VersionUtil()
        override fun getInstance(): VersionUtil {
            return instance
        }
    }
}