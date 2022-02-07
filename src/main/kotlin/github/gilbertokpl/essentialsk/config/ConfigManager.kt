package github.gilbertokpl.essentialsk.config

import github.gilbertokpl.essentialsk.config.annotations.Comment
import github.gilbertokpl.essentialsk.config.annotations.PrimaryComment
import github.gilbertokpl.essentialsk.config.field.FieldHelper
import github.gilbertokpl.essentialsk.config.get.GetValues
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.configs.StartLang
import github.gilbertokpl.essentialsk.util.MainUtil
import org.simpleyaml.configuration.file.YamlFile
import java.io.File

object ConfigManager {

    data class NewConfig(
        val javaClass: Class<*>,
        val classIntance: Any,
        val file : File,
        val lang: Boolean
    )

    private val listConfigs = listOf(
        NewConfig(MainConfig().javaClass ,MainConfig(), File(MainUtil.mainPath, "MainConfig.yml"),false)
    )

    fun start() {
        listConfigs.forEach {
            startClass(it)
        }
    }

    private fun startClass(config: NewConfig) {

        var check = false

        val checkYaml = genYaml(config.javaClass, config.classIntance)

        if (!config.file.exists()) {
            checkYaml.save(config.file)
            return
        }

        val currentConfig = loadYaml(config.file)

        for (i in checkYaml.getValues(true)) {
            if (currentConfig.get(i.key) == null) {
                currentConfig.addDefault(i.key, i.value)
                check = true
                checkYaml.addDefault(i.key, i.value)
                MainUtil.consoleMessage(
                    StartLang.addMessage.replace("%path%", i.key)
                        .replace("%file%", config.file.name)
                )
            }
        }

        for (i in currentConfig.getValues(true)) {
            if (checkYaml.get(i.key) != null) {
                checkYaml.set(i.key, i.value)
            } else {
                check = true
                MainUtil.consoleMessage(
                    StartLang.removeMessage.replace("%path%", i.key)
                        .replace("%file%", config.file.name)
                )
            }
        }

        checkYaml.save(config.file)

        OtherConfig.serverPrefix = GetValues.getString(checkYaml, "general.server-prefix") ?: ""

        if (check) {
            startClass(config)
            return
        }

        load(config, checkYaml)
    }

    private fun load(newConfig: NewConfig, yamlFile: YamlFile) {
        FieldHelper.setValuesOfClass(newConfig.javaClass, newConfig.classIntance, yamlFile)
    }

    private fun loadYaml(file: File) : YamlFile {
        val yamlFile = YamlFile(file)
        yamlFile.loadWithComments()
        return yamlFile
    }

    private fun genYaml(cl : Class<*>, clIntance : Any) : YamlFile {

        val tempFile = File.createTempFile("check", ".yml")

        val tempYamlFile = loadYaml(tempFile)

        for (i in cl.declaredFields) {
            val path = FieldHelper.nameFieldHelper(i)

            tempYamlFile.set(path, i.get(clIntance))

            for (comments in i.annotations) {
                if (comments is Comment && comments.annotations != "") {
                    tempYamlFile.setComment(path, comments.annotations)
                    continue
                }
                if (comments is PrimaryComment && comments.primaryAnnotations != "") {
                    tempYamlFile.setComment(path.split(".")[0], comments.primaryAnnotations)
                    continue
                }
            }
        }

        return tempYamlFile
    }
}