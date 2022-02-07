package github.gilbertokpl.essentialsk.config

import github.gilbertokpl.essentialsk.config.annotations.Comments
import github.gilbertokpl.essentialsk.config.annotations.PrimaryComments
import github.gilbertokpl.essentialsk.config.annotations.Values
import github.gilbertokpl.essentialsk.config.field.FieldHelper
import github.gilbertokpl.essentialsk.config.get.GetValues
import github.gilbertokpl.essentialsk.config.lang.Lang
import github.gilbertokpl.essentialsk.config.otherConfigs.OtherConfig
import github.gilbertokpl.essentialsk.config.otherConfigs.StartLang
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.util.MainUtil
import org.simpleyaml.configuration.file.YamlFile
import java.io.File

object ConfigManager {

    private var lang = Lang.PT_BR

    data class NewConfig(
        val javaClass: Class<*>,
        val classIntance: Any,
        val file: File,
        val lang: Boolean
    )

    private val listConfigs = listOf(
        NewConfig(
            MainConfig().javaClass,
            MainConfig(), File(MainUtil.mainPath, "MainConfig.yml"), false
        ),
    )

    fun start() {
        listConfigs.forEach {
            startClass(it)
        }

        for (i in Lang.values()) {
            startClass(
                NewConfig(
                    LangConfig().javaClass,
                    LangConfig(), i.getFile(), true
                )
            )
        }

        OtherConfig.start()
    }

    private fun startClass(config: NewConfig) {

        var check = false

        if (!config.file.exists()) {
            genYaml(config, Lang.PT_BR).save(config.file)
            return
        }

        val currentConfig = loadYaml(config.file)

        if (!config.lang) {
            lang = try {
                Lang.valueOf(currentConfig.getString("general.selected-lang").uppercase())
            } catch (il: IllegalArgumentException) {
                Lang.PT_BR
            }
        }

        val checkYaml = genYaml(config, lang)

        for (i in checkYaml.getValues(true)) {
            if (currentConfig.get(i.key) == null) {
                currentConfig.addDefault(i.key, i.value)
                checkYaml.addDefault(i.key, i.value)
                check = true
                if (i.key.contains(".")) {
                    MainUtil.consoleMessage(
                        StartLang.addMessage.replace("%path%", i.key)
                            .replace("%file%", config.file.name)
                    )
                }
            }
        }

        for (i in currentConfig.getValues(true)) {
            if (checkYaml.get(i.key) == null) {
                checkYaml.remove(i.key)
                check = true
                if (i.key.contains(".")) {
                    MainUtil.consoleMessage(
                        StartLang.removeMessage.replace("%path%", i.key)
                            .replace("%file%", config.file.name)
                    )
                }
            } else {
                checkYaml.set(i.key, i.value)
            }
        }

        checkYaml.save(config.file)

        try {
            OtherConfig.serverPrefix = ""
        } catch (ignored: Exception) {
        }
        try {
            OtherConfig.vanish = GetValues.getStringList(checkYaml, "vanish.blocked-other-cmds", false)
        } catch (ignored: Exception) {
        }
        try {
            OtherConfig.announce = GetValues.getStringList(checkYaml, "announcements.list-announce", true)
        } catch (ignored: Exception) {
        }
        try {
            OtherConfig.deathMessage = GetValues.getStringList(checkYaml, "deathmessages.cause-replacer", true)
        } catch (ignored: Exception) {
        }
        try {
            OtherConfig.deathMessageEntity = GetValues.getStringList(checkYaml, "deathmessages.entity-replacer", true)
        } catch (ignored: Exception) {
        }

        if (check) {
            startClass(config)
            return
        }

        if (config.file.name.replace(".yml", "").equals(lang.name, true) && config.lang) {
            load(config, checkYaml)
        }

        if (!config.lang) {
            load(config, checkYaml)
        }

    }

    fun reloadConfig(): Boolean {
        return try {
            start()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun load(newConfig: NewConfig, yamlFile: YamlFile) {
        FieldHelper.setValuesOfClass(newConfig.javaClass, newConfig.classIntance, yamlFile)
    }

    private fun loadYaml(file: File): YamlFile {
        val yamlFile = YamlFile(file)
        yamlFile.loadWithComments()
        return yamlFile
    }

    private fun genYaml(newConfig: NewConfig, lang: Lang): YamlFile {

        val tempFile = File.createTempFile("check", ".yml")

        val tempYamlFile = loadYaml(tempFile)

        for (i in newConfig.javaClass.declaredFields) {
            val path = FieldHelper.nameFieldHelper(i)

            if (!newConfig.lang) {
                tempYamlFile.set(path, i.get(newConfig.classIntance))
            }

            for (comments in i.annotations) {
                if (comments is Comments) {
                    for (com in comments.value) {
                        if (com.annotations != "" && lang == com.lang) {
                            tempYamlFile.setComment(path, com.annotations)
                        }
                    }
                    continue
                }

                if (comments is PrimaryComments) {
                    for (com in comments.value) {
                        if (com.primaryAnnotations != "" && lang == com.lang) {
                            tempYamlFile.setComment(path.split(".")[0], com.primaryAnnotations)
                        }
                    }
                    continue
                }
                if (comments is Values) {
                    for (com in comments.value) {
                        if (lang == com.lang) {
                            if (com.value.contains("|")) {
                                tempYamlFile.set(path, com.value.split("|").toList())
                                continue
                            }
                            tempYamlFile.set(path, com.value)
                        }
                    }
                    continue
                }
            }
        }

        return tempYamlFile
    }
}