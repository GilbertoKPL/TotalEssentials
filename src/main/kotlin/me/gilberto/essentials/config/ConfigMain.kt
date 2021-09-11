package me.gilberto.essentials.config

import me.gilberto.essentials.EssentialsMain.instance
import me.gilberto.essentials.EssentialsMain.pluginName
import me.gilberto.essentials.config.configs.lang.langname
import me.gilberto.essentials.management.Manager.consoleMessage
import me.gilberto.essentials.management.Manager.disableplugin
import me.gilberto.essentials.management.Manager.pluginlangdir
import me.gilberto.essentials.management.Manager.pluginpastedir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.nio.file.*

object ConfigMain {
    var configs = HashSet<YamlConfiguration>()
    var langs = HashSet<YamlConfiguration>()
    lateinit var econf: YamlConfiguration
    lateinit var lang: YamlConfiguration
    fun startconfig() {
        consoleMessage("$pluginName §eIniciando verificação da config...")
        econf = copyconfig("GD_EssentialsConfig", false) ?: return
        ConfigReload()
        consoleMessage("$pluginName §eVerificação completa!")
        consoleMessage("$pluginName §eIniciando verificação da lang...")
        startlang()
        val padlang = YamlConfiguration.loadConfiguration(File(pluginlangdir(), "pt_BR.yml"))
        try {
            val a = File(pluginlangdir(), "$langname.yml")
            if (a.exists()) {
                lang = YamlConfiguration.loadConfiguration(a)
                consoleMessage("$pluginName §eLang selecionada -> ${langname}!")
            }
            else {
                lang = padlang
                consoleMessage("$pluginName §eUtilizando lang padrão por error!")
            }
        } catch (ex: Exception) {
            lang = padlang
            consoleMessage("$pluginName §eUtilizando lang padrão por error!")
        }
        consoleMessage("$pluginName §eVerificação completa!")
    }
    fun addtoconfig (source: YamlConfiguration, path: String, value: Any) {
        source.addDefault(path, value)
        reloadconfig()
    }
    fun reloadconfig() {
        fun test(source: YamlConfiguration) {
            val check: YamlConfiguration
            try {
                check = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "${source.name}.yml"))
            } catch (Ex: Exception) {
                Ex.printStackTrace()
                consoleMessage("§cProblema na config $source, retornando a antiga!")
                source.save(source.currentPath)
                return
            }
            check.save(source.currentPath)
        }
        for (i in configs) {
            test(i)
        }
        ConfigReload()
    }

    private fun startlang() {
        val directoryStream: DirectoryStream<Path>? = Files.newDirectoryStream(FileSystems.newFileSystem(
            Paths.get(instance.javaClass.protectionDomain.codeSource.location.toURI()), null).getPath("/lang/"))
        if (directoryStream != null) {
            for (i in directoryStream) {
                copyconfig(i.fileName.toString().replace(".yml", ""), true)
            }
        }
    }

    private fun copyconfig(source: String, lang: Boolean) : YamlConfiguration? {
        val configfile: File
        val resource: InputStream
        val checkfile: File
        if (lang) {
            configfile = File(pluginlangdir(), "$source.yml")
            resource = instance.javaClass.getResourceAsStream("/lang/$source.yml") ?: return YamlConfiguration.loadConfiguration(
                File(pluginlangdir(), "$source.yml")
            )
            checkfile = File(pluginlangdir(), "$source-check.yml")
        }
        else {
            configfile = File(pluginpastedir(), "$source.yml")
            resource = instance.javaClass.getResourceAsStream("/$source.yml") ?: return YamlConfiguration.loadConfiguration(
                File(pluginpastedir(), "$source.yml")
            )
            checkfile = File(pluginpastedir(), "$source-check.yml")
        }
        if (configfile.exists()) {
            val check : YamlConfiguration
            try {
                check = if (lang) {
                    YamlConfiguration.loadConfiguration(File(pluginlangdir(), "$source.yml"))
                } else {
                    YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
                }
            } catch (Ex: Exception) {
                if (lang) {
                    consoleMessage("$pluginName §cProblema na lang $source.")
                } else {
                    consoleMessage("$pluginName §cProblema na config $source.")
                }
                disableplugin()
                Ex.printStackTrace()
                return null
            }
            val v = check.getDouble("Version-file")
            if (checkfile.exists()) checkfile.delete()
            Files.copy(resource, checkfile.toPath())
            val vc = YamlConfiguration.loadConfiguration(checkfile).getDouble("Version-file")
            if (vc > v) {
                ConfigVersionChecker(configfile, checkfile, vc.toString(), lang)
            }
            else {
                ConfigChecker(configfile, checkfile, lang)
            }
        }
        else {
            if (lang) {
                (File(pluginlangdir()).mkdirs())
            } else {
                (File(pluginpastedir()).mkdirs())
            }
            Files.copy(resource, configfile.toPath())
            if (lang) {
                consoleMessage("$pluginName §eCriado arquivo de lang ${configfile.name}")
            } else {
                consoleMessage("$pluginName §eCriado arquivo de config ${configfile.name}")
            }
        }
        return if (lang) {
            val b = YamlConfiguration.loadConfiguration(File(pluginlangdir(), "$source.yml"))
            langs.add(b)
            b
        } else {
            val b = YamlConfiguration.loadConfiguration(File(pluginpastedir(), "$source.yml"))
            configs.add(b)
            b
        }
    }
    fun getString(source: YamlConfiguration, path: String): String = source.getString(path)
    fun getStringList(source: YamlConfiguration, path: String): List<String> = source.getStringList(path)
    fun getInt(source: YamlConfiguration, path: String): Int = source.getInt(path)
    fun getIntList(source: YamlConfiguration, path: String): List<Int> = source.getIntegerList(path)
}