package io.github.gilbertodamim.essentials.config

import io.github.gilbertodamim.essentials.config.langs.StartLang
import io.github.gilbertodamim.essentials.config.langs.StartLang.*
import io.github.gilbertodamim.essentials.management.Manager.consoleMessage
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class ConfigVersionChecker(file: File, newFile: File, version: String?, lang: Boolean) {
    private var toRemove = HashMap<String, Int>()
    private var toPut = HashMap<String, Int>()

    private fun sortMapByValuesWithDuplicates(passedMap: HashMap<String, Int>): LinkedHashMap<String, Int> {
        val mapKeys: ArrayList<String> = ArrayList(passedMap.keys)
        val mapValues: ArrayList<Int> = ArrayList(passedMap.values)
        mapValues.sort()
        mapKeys.sort()
        val sortedMap: LinkedHashMap<String, Int> = LinkedHashMap()
        val valueIt = mapValues.iterator()
        while (valueIt.hasNext()) {
            val t = valueIt.next()
            val keyIt = mapKeys.iterator()
            while (keyIt.hasNext()) {
                val key = keyIt.next()
                val comp1 = passedMap[key].toString()
                val comp2 = t.toString()
                if (comp1 == comp2) {
                    passedMap.remove(key)
                    mapKeys.remove(key)
                    sortedMap[key] = t
                    break
                }
            }
        }
        return sortedMap
    }

    private fun reverse(map: LinkedHashMap<String, Int>): LinkedHashMap<String, Int> {
        val reversedMap = LinkedHashMap<String, Int>()
        val it: ListIterator<Map.Entry<String, Int>> =
            ArrayList<Map.Entry<String, Int>>(map.entries).listIterator(map.entries.size)
        while (it.hasPrevious()) {
            val (key, value) = it.previous()
            reversedMap[key] = value
        }
        return reversedMap
    }

    private val confFile: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
    private val confNewFile: YamlConfiguration = YamlConfiguration.loadConfiguration(newFile)

    init {
        val newConfig = YamlConfiguration.loadConfiguration(newFile)
        for (FileKeys in confNewFile.getKeys(true)) {
            if (FileKeys == "Version-file") {
                newConfig.set(FileKeys, confNewFile.get(FileKeys))
                if (version != null) {
                    consoleMessage(StartLang.updateMessage.replace("%file%", file.name).replace("%version%", version))
                } else {
                    if (lang) {
                        consoleMessage(modConfig.replace("%to%", "lang"))
                    } else {
                        consoleMessage(modConfig.replace("%to%", "config"))
                    }
                }
                val header = confFile.options()
                val newHeader = confNewFile.options()
                if (header.header().toString() != newHeader.header().toString()) {
                    newConfig.options().header(newHeader.header().toString())
                    consoleMessage(updateHeader.replace("%file%", file.name))
                }
                continue
            }
            if (confFile.get(FileKeys) != null) {
                newConfig.set(FileKeys, confFile.get(FileKeys))
            } else {
                toPut[FileKeys] = FileKeys.split(".").size.plus(1)
            }
        }
        val putSorted = sortMapByValuesWithDuplicates(toPut)
        for (createConfig in putSorted) {
            newConfig.set(createConfig.key, confNewFile.get(createConfig.key))
            consoleMessage(addMessage.replace("%path%", createConfig.key).replace("%file%", file.name))
        }
        for (i in confFile.getKeys(true)) {
            if (i == "Version-file") continue
            if (confNewFile.get(i) == null) {
                toRemove[i] = i.split(".").size.plus(1)
            }
        }
        val removeSorted = reverse(sortMapByValuesWithDuplicates(toRemove))
        for (deleteConfig in removeSorted) {
            newConfig.set(deleteConfig.key, null)
            consoleMessage(removeMessage.replace("%path%", deleteConfig.key).replace("%file%", file.name))
        }
        newConfig.save(file)
        newFile.delete()
    }
}