package github.gilbertokpl.core.config

import github.gilbertokpl.core.TotalCore
import github.gilbertokpl.core.config.annotations.*
import github.gilbertokpl.core.config.defaults.DefaultConfig
import github.gilbertokpl.core.config.defaults.DefaultLang
import github.gilbertokpl.core.config.types.LangTypes
import org.simpleyaml.configuration.file.YamlFile
import java.io.File

class ConfigManager(private val totalCore: TotalCore) {

    // Core references
    lateinit var messages: DefaultLang
    lateinit var configs: DefaultConfig

    // Default language
    private var lang = LangTypes.PT_BR

    /**
     * Data holder for each configuration class
     */
    data class NewConfig(
        val javaClass: Class<*>,
        val classInstance: Any,
        val file: File,
        val lang: LangTypes?
    )

    /**
     * Start loading and generating configs based on annotated classes.
     */
    fun start(configPackage: String) {
        val listClass = totalCore.getReflection().getClasses(configPackage).toMutableList()

        // ========== Main config ==========
        var mainConfigClass: Class<*>? = null
        for (cl in listClass) {
            val configPattern = cl.getAnnotation(ConfigPattern::class.java) ?: continue
            val instance = cl.getDeclaredConstructor().newInstance()

            if (instance is DefaultConfig) {
                configs = instance
                startClass(
                    NewConfig(
                        cl,
                        instance,
                        File(totalCore.mainPath.replace(".paper-remapped/", ""), "${configPattern.name}.yml"),
                        null
                    ),
                    true
                )
                mainConfigClass = cl
                break
            }
        }
        listClass.remove(mainConfigClass)

        // ========== Other configs ==========
        for (cl in listClass) {
            val instance = cl.getDeclaredConstructor().newInstance()

            when (instance) {
                is DefaultLang -> {
                    messages = instance
                    // Generate configs for all available languages
                    for (i in LangTypes.entries) {
                        startClass(
                            NewConfig(cl, instance, i.getFile(totalCore), i),
                            false
                        )
                    }
                }

                else -> {
                    val configPattern = cl.getAnnotation(ConfigPattern::class.java)
                    startClass(
                        NewConfig(
                            cl,
                            instance,
                            File(totalCore.mainPath.replace(".paper-remapped/", ""), "${configPattern?.name}.yml"),
                            null
                        ),
                        null
                    )
                }
            }
        }
    }

    /**
     * Start or update configuration files for a given class
     */
    private fun startClass(config: NewConfig, mainConfig: Boolean?) {
        val configFile = config.file

        // File does not exist -> generate new
        if (!configFile.exists()) {
            val yamlFile = genYaml(config, config.lang ?: LangTypes.PT_BR)
            yamlFile.save(configFile)
            if (config.lang == null || config.lang == lang) {
                load(config, yamlFile)
            }
            return
        }

        // Load current file
        val currentConfig = loadYaml(configFile, false)

        // Handle main config language selection
        if (mainConfig == true) {
            lang = try {
                LangTypes.valueOf(currentConfig.getString("general.selected-lang").uppercase())
            } catch (_: Exception) {
                LangTypes.PT_BR
            }
        }

        // Handle language-specific prefix
        if (mainConfig == false && lang == config.lang) {
            totalCore.serverPrefix = try {
                currentConfig.getString("general.server-prefix").replace("&", "§")
            } catch (_: Exception) {
                ""
            }
        }

        // Sync file with defaults
        val checkYaml = genYaml(config, lang)
        val toSet = HashMap<String, Any?>()
        val toRemove = ArrayList<String>()
        val toAdd = HashMap<String, Any?>()

        for ((key, value) in currentConfig.getValues(true).toMap()) {
            if (checkYaml.get(key) == null) {
                toRemove.add(key)
            } else {
                toSet[key] = value
            }
        }

        for ((key, value) in checkYaml.getValues(true)) {
            if (currentConfig.get(key) == null && key.contains(".")) {
                toAdd[key] = value
            }
        }

        // Apply modifications
        for ((key, value) in toSet) if (!toRemove.contains(key)) checkYaml.set(key, value)
        for (key in toRemove) checkYaml.set(key, null)
        for ((key, value) in toAdd) checkYaml.set(key, value)

        checkYaml.save(configFile)

        // Load values into class instance
        if (config.lang == null || config.lang == lang) {
            load(config, checkYaml)
        }
    }

    /**
     * Load values from YamlFile into class instance via reflection
     */
    private fun load(newConfig: NewConfig, yamlFile: YamlFile) {
        totalCore.getReflection().setValuesOfClass(newConfig.javaClass, newConfig.classInstance, yamlFile)
    }

    /**
     * Load YamlFile from File
     */
    private fun loadYaml(file: File, comment: Boolean): YamlFile {
        return YamlFile(file).apply {
            if (comment) loadWithComments() else load()
        }
    }

    /**
     * Generate YamlFile based on annotations and default values
     */
    private fun genYaml(newConfig: NewConfig, lang: LangTypes): YamlFile {
        val tempYamlFile = YamlFile()

        for (field in newConfig.javaClass.declaredFields) {
            val path = totalCore.getReflection().nameFieldHelper(field)

            // Default values
            if (newConfig.lang == null) {
                tempYamlFile.set(path, field.get(newConfig.classInstance))
            }

            val multiComments = mutableListOf<MultiComment>()
            var rootComments: RootComments? = null
            var values: MultiValue? = null

            // Collect annotations
            for (annotation in field.annotations) {
                when (annotation) {
                    is MultiComment -> multiComments.add(annotation)
                    is RootComments -> rootComments = annotation
                    is MultiValue -> values = annotation
                }
            }

            // Multi-line comments
            for (comments in multiComments) {
                for (com in comments.value) {
                    if (com.annotations.isNotEmpty() && lang == com.lang) {
                        tempYamlFile.setComment(path, com.annotations)
                    }
                }
            }

            // Root comments
            rootComments?.let {
                for (com in it.value) {
                    if (com.primaryAnnotations.isNotEmpty() && lang == com.lang) {
                        tempYamlFile.setComment(path.split(".")[0], com.primaryAnnotations)
                    }
                }
            }

            // Values by language
            values?.let {
                for (com in it.value) {
                    if (lang == com.lang) {
                        if (com.value.contains("|")) {
                            tempYamlFile.set(path, com.value.split("|"))
                        } else {
                            tempYamlFile.set(path, com.value)
                        }
                    }
                }
            }
        }

        return tempYamlFile
    }
}
