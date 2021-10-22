package io.github.gilbertodamim.ksystem.config

import io.github.gilbertodamim.ksystem.KSystemMain.disablePlugin
import io.github.gilbertodamim.ksystem.KSystemMain.instance
import io.github.gilbertodamim.ksystem.config.configs.DatabaseConfig
import io.github.gilbertodamim.ksystem.config.configs.DatabaseConfig.langName
import io.github.gilbertodamim.ksystem.config.configs.KitsConfig
import io.github.gilbertodamim.ksystem.config.langs.GeneralLang
import io.github.gilbertodamim.ksystem.config.langs.KitsLang
import io.github.gilbertodamim.ksystem.config.langs.StartLang.*
import io.github.gilbertodamim.ksystem.config.langs.TimeLang
import io.github.gilbertodamim.ksystem.management.Manager.consoleMessage
import io.github.gilbertodamim.ksystem.management.Manager.pluginLangDir
import io.github.gilbertodamim.ksystem.management.Manager.pluginPasteDir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStream
import java.nio.file.*

object ConfigMain {
    private var configList = ArrayList<YamlConfiguration>()
    private var langList = ArrayList<YamlConfiguration>()

    private lateinit var essentialsConfig: YamlConfiguration
    private lateinit var langConfig: YamlConfiguration

    fun startConfig() {
        consoleMessage(startVerification.replace("%to%", "config"))
        essentialsConfig = kSystemConfig("KSystemMainConfig", false) ?: return
        reloadConfig(true)
        consoleMessage(completeVerification)
        consoleMessage(startVerification.replace("%to%", "lang"))
        startLang()
        val patternLang = YamlConfiguration.loadConfiguration(File(pluginLangDir(), "pt_BR.yml"))
        try {
            val langSelected = File(pluginLangDir(), "$langName.yml")
            if (langSelected.exists()) {
                langConfig = YamlConfiguration.loadConfiguration(langSelected)
                consoleMessage(langSelectedMessage.replace("%lang%", langName))
            } else {
                langConfig = patternLang
                consoleMessage(langError)
            }
        } catch (ex: Exception) {
            langConfig = patternLang
            consoleMessage(langError)
        }
        reloadLang()
        consoleMessage(completeVerification)
    }

    private fun reloadLang() {
        KitsLang.reload(langConfig)
        TimeLang.reload(langConfig)
        GeneralLang.reload(langConfig)
    }

    private fun reloadConfig(firstTime: Boolean = false) {
        if (!firstTime) {
            for (configs in configList) {
                val check: YamlConfiguration
                try {
                    check = YamlConfiguration.loadConfiguration(File(pluginPasteDir(), "${configs.name}.yml"))
                } catch (Ex: Exception) {
                    Ex.printStackTrace()
                    consoleMessage(problemReload.replace("%file%", configs.name))
                    configs.save(configs.currentPath)
                    return
                }
                check.save(configs.currentPath)
            }
        }
        DatabaseConfig.reload(essentialsConfig)
        KitsConfig.reload(essentialsConfig)
    }


    private fun startLang() {
        val directoryStream: DirectoryStream<Path>? = Files.newDirectoryStream(
            FileSystems.newFileSystem(
                Paths.get(instance.javaClass.protectionDomain.codeSource.location.toURI()), null
            ).getPath("/lang/")
        )
        if (directoryStream != null) {
            for (i in directoryStream) {
                kSystemConfig(i.fileName.toString().replace(".yml", ""), true)
            }
        }
    }

    private fun kSystemConfig(source: String, lang: Boolean): YamlConfiguration? {
        val configFile: File
        val resource: InputStream
        val checkFile: File

        if (lang) {
            configFile = File(pluginLangDir(), "$source.yml")
            resource = instance.javaClass.getResourceAsStream("/lang/$source.yml")
                ?: return YamlConfiguration.loadConfiguration(
                    File(pluginLangDir(), "$source.yml")
                )
            checkFile = File(pluginLangDir(), "$source-check.yml")
        } else {
            configFile = File(pluginPasteDir(), "$source.yml")
            resource =
                instance.javaClass.getResourceAsStream("/$source.yml") ?: return YamlConfiguration.loadConfiguration(
                    File(pluginPasteDir(), "$source.yml")
                )
            checkFile = File(pluginPasteDir(), "$source-check.yml")
        }
        if (configFile.exists()) {
            val check: YamlConfiguration
            try {
                check = if (lang) {
                    YamlConfiguration.loadConfiguration(File(pluginLangDir(), "$source.yml"))
                } else {
                    YamlConfiguration.loadConfiguration(File(pluginPasteDir(), "$source.yml"))
                }
            } catch (Ex: Exception) {
                if (lang) {
                    consoleMessage(problemMessage.replace("%to%", "lang").replace("%file%", source))
                } else {
                    consoleMessage(problemMessage.replace("%to%", "config").replace("%file%", source))
                }
                Ex.printStackTrace()
                disablePlugin()
                return null
            }
            val v = check.getDouble("Version-file")
            if (checkFile.exists()) checkFile.delete()
            Files.copy(resource, checkFile.toPath())
            val vc = YamlConfiguration.loadConfiguration(checkFile).getDouble("Version-file")
            if (vc > v) {
                ConfigVersionChecker(configFile, checkFile, vc.toString(), lang)
            } else {
                ConfigChecker(configFile, checkFile, lang)
            }
        } else {
            if (lang) {
                (File(pluginLangDir()).mkdirs())
            } else {
                (File(pluginPasteDir()).mkdirs())
            }
            Files.copy(resource, configFile.toPath())
            if (lang) {
                consoleMessage(createMessage.replace("%to%", "lang").replace("%file%", configFile.name))
            } else {
                consoleMessage(createMessage.replace("%to%", "config").replace("%file%", configFile.name))
            }
        }
        return if (lang) {
            val toReturn = YamlConfiguration.loadConfiguration(File(pluginLangDir(), "$source.yml"))
            langList.add(toReturn)
            toReturn
        } else {
            val toReturn = YamlConfiguration.loadConfiguration(File(pluginPasteDir(), "$source.yml"))
            configList.add(toReturn)
            toReturn
        }
    }

    fun getString(source: YamlConfiguration, path: String, color: Boolean): String {
        return if (color) {
            source.getString(path)!!.replace("&", "§")
        } else source.getString(path)!!
    }

    fun getStringList(source: YamlConfiguration, path: String, color: Boolean): List<String> {
        return if (color) {
            val ret: MutableList<String> = ArrayList()
            for (i in source.getStringList(path)) {
                ret.add(i.replace("&", "§"))
            }
            ret
        } else source.getStringList(path)
    }

    fun getInt(source: YamlConfiguration, path: String): Int = source.getInt(path)
    fun getIntList(source: YamlConfiguration, path: String): List<Int> = source.getIntegerList(path)
    fun getBoolean(source: YamlConfiguration, path: String): Boolean = source.getBoolean(path)
}