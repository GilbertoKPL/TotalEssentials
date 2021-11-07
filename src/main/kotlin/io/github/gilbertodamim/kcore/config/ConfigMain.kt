package io.github.gilbertodamim.kcore.config

import io.github.gilbertodamim.kcore.KCoreMain.disablePlugin
import io.github.gilbertodamim.kcore.KCoreMain.instance
import io.github.gilbertodamim.kcore.config.configs.DatabaseConfig
import io.github.gilbertodamim.kcore.config.configs.DatabaseConfig.langName
import io.github.gilbertodamim.kcore.config.configs.KitsConfig
import io.github.gilbertodamim.kcore.config.langs.GeneralLang
import io.github.gilbertodamim.kcore.config.langs.KitsLang
import io.github.gilbertodamim.kcore.config.langs.StartLang.*
import io.github.gilbertodamim.kcore.config.langs.TimeLang
import io.github.gilbertodamim.kcore.inventory.KitsInventory
import io.github.gilbertodamim.kcore.management.ErrorClass
import io.github.gilbertodamim.kcore.management.Manager.consoleMessage
import io.github.gilbertodamim.kcore.management.Manager.pluginLangDir
import io.github.gilbertodamim.kcore.management.Manager.pluginPasteDir
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.*

object ConfigMain {
    private var configList = ArrayList<YamlConfiguration>()
    private var langList = ArrayList<YamlConfiguration>()
    private lateinit var essentialsConfig: YamlConfiguration
    private lateinit var langConfig: YamlConfiguration

    fun start() {
        consoleMessage(startVerification.replace("%to%", "config"))
        essentialsConfig = kCoreConfig("KCoreMainConfig") ?: return
        reloadConfig(true)
        consoleMessage(completeVerification)
        consoleMessage(startVerification.replace("%to%", "lang"))
        reloadLang(true)
        consoleMessage(completeVerification)
    }

    private fun reloadLang(firstTime: Boolean = false) {
        if (firstTime) {
            val patternLang = YamlConfiguration.loadConfiguration(File(pluginLangDir(), "pt_BR.yml"))
            val directoryStream: DirectoryStream<Path>? = Files.newDirectoryStream(
                FileSystems.newFileSystem(
                    Paths.get(instance.javaClass.protectionDomain.codeSource.location.toURI()),
                    instance.javaClass.classLoader
                ).getPath("/lang/")
            )
            if (directoryStream != null) {
                for (i in directoryStream) {
                    kCoreConfig(i.fileName.toString().replace(".yml", ""), true)
                }
            }
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
                ErrorClass().sendException(ex)
            }
        }
        KitsLang.reload(langConfig)
        TimeLang.reload(langConfig)
        GeneralLang.reload(langConfig)
        KitsInventory().editKitInventory()
    }

    private fun reloadConfig(firstTime: Boolean = false) {
        if (!firstTime) {
            for (configs in configList) {
                val check: YamlConfiguration
                try {
                    check = YamlConfiguration.loadConfiguration(File(pluginPasteDir(), "${configs.name}.yml"))
                } catch (ex: Exception) {
                    ErrorClass().sendException(ex)
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

    private fun kCoreConfig(source: String, lang: Boolean = false): YamlConfiguration? {
        val location = if (lang) {
            "/lang/$source.yml"
        } else {
            "/$source.yml"
        }
        val dir = if (lang) {
            pluginLangDir()
        } else {
            pluginPasteDir()
        }
        val message = if (lang) {
            "lang"
        } else {
            "config"
        }
        try {
            val configFile = File(dir, "$source.yml")
            val resource = instance.javaClass.getResourceAsStream(location)
            if (configFile.exists()) {
                val configYaml = YamlConfiguration.loadConfiguration(configFile)
                if (resource != null) {
                    val checkFile = File(dir, "checker.yml")
                    if (checkFile.exists()) checkFile.delete()
                    Files.copy(resource, checkFile.toPath())
                    val checkYaml = YamlConfiguration.loadConfiguration(checkFile)
                    var checkCurrent = true
                    for (fileKeys in checkYaml.getKeys(true)) {
                        if (configYaml.get(fileKeys) == null) {
                            ConfigVersionChecker(configFile, checkFile)
                            checkCurrent = false
                            break
                        }
                    }
                    if (checkCurrent) {
                        for (fileKeys in configYaml.getKeys(true)) {
                            if (checkYaml.get(fileKeys) == null) {
                                ConfigVersionChecker(configFile, checkFile)
                                break
                            }
                        }
                    }
                    checkFile.delete()
                    return YamlConfiguration.loadConfiguration(configFile)
                } else {
                    return configYaml
                }
            } else {
                File(dir).mkdirs()
                Files.copy(resource!!, configFile.toPath())
                consoleMessage(createMessage.replace("%to%", message).replace("%file%", configFile.name))
                val configYaml = YamlConfiguration.loadConfiguration(configFile)
                if (lang) {
                    langList.add(configYaml)
                } else {
                    configList.add(configYaml)
                }
                return configYaml
            }
        } catch (ex: Exception) {
            consoleMessage(problemMessage.replace("%to%", message).replace("%file%", source))
            ErrorClass().sendException(ex)
            disablePlugin()
        }
        return null
    }

    fun getString(source: YamlConfiguration, path: String, color: Boolean = false): String {
        return if (color) {
            source.getString(path)!!.replace("&", "§")
        } else source.getString(path)!!
    }

    fun getStringList(source: YamlConfiguration, path: String, color: Boolean = false): List<String> {
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