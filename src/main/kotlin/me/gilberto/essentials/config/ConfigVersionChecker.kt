package me.gilberto.essentials.config

import me.gilberto.essentials.EssentialsMain.pluginName
import me.gilberto.essentials.management.Manager.consoleMessage
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class ConfigVersionChecker(file: File, newfile: File, version: String?, lang:Boolean) {
    private var map = HashMap<String, Int>()
    private var map1 = HashMap<String, Int>()

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
        val it: ListIterator<Map.Entry<String, Int>> = ArrayList<Map.Entry<String, Int>>(map.entries).listIterator(map.entries.size)
        while (it.hasPrevious()) {
            val (key, value) = it.previous()
            reversedMap[key] = value
        }
        return reversedMap
    }
    private val conffile: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
    private val confnewfile: YamlConfiguration = YamlConfiguration.loadConfiguration(newfile)
    init {
        val newconfig = YamlConfiguration.loadConfiguration(newfile)
        for (i in confnewfile.getKeys(true)) {
            if (i == "Version-file") {
                newconfig.set(i, confnewfile.get(i))
                if (version != null) {
                    consoleMessage("$pluginName §eAtualizado a Version-file do arquivo ${file.name} para $version")
                }
                else {
                    if (lang) {
                        consoleMessage("$pluginName §cDetectado modificação na lang executando Checker!")
                    } else {
                        consoleMessage("$pluginName §cDetectado modificação na config executando Checker!")
                    }
                }
                val header = conffile.options()!!
                val newheader = confnewfile.options()!!
                if (header.header().toString() != newheader.header().toString()) {
                    newconfig.options().header(newheader.header().toString())
                    consoleMessage("$pluginName §eAtualizado a Header do arquivo ${file.name}")
                }
                continue
            }
            if (conffile.get(i) != null) {
                newconfig.set(i, conffile.get(i))
            }
            else {
                map1[i] = i.split(".").size.plus(1)
            }
        }
        val putsorted = sortMapByValuesWithDuplicates(map1)
        for (i in putsorted) {
            newconfig.set(i.key, confnewfile.get(i.key))
            consoleMessage("$pluginName §eAdicionado path ${i.key} no arquivo ${file.name}")
        }
        for (i in conffile.getKeys(true)) {
            if (i == "Version-file") continue
            if (confnewfile.get(i) == null) {
                map[i] = i.split(".").size.plus(1)
            }
        }
        val removesorted = reverse(sortMapByValuesWithDuplicates(map))
        for (i in removesorted) {
            newconfig.set(i.key, null)
            consoleMessage("$pluginName §cRetirado path ${i.key} no arquivo ${file.name}")
        }
        newconfig.save(file)
        newfile.delete()
    }
}