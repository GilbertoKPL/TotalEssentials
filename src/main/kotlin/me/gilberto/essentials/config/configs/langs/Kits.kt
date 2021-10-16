package me.gilberto.essentials.config.configs.langs

import me.gilberto.essentials.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object Kits {
    lateinit var notexist: String
    lateinit var exist: String
    lateinit var editkittime: String
    lateinit var createkitusage: String
    lateinit var delkitusage: String
    lateinit var createkitsuccess: String
    lateinit var createkitproblem: String
    lateinit var delkitsuccess: String
    lateinit var delkitproblem: String
    lateinit var editkitsuccess: String
    lateinit var editkitproblem: String
    fun reload(source1: YamlConfiguration) {
        notexist = ConfigMain.getString(source1, "Kits.notexist", true)
        exist = ConfigMain.getString(source1, "Kits.exist", true)
        editkittime = ConfigMain.getString(source1, "Kits.editkittime", true)
        createkitusage = ConfigMain.getString(source1, "Kits.createkitusage", true)
        delkitusage = ConfigMain.getString(source1, "Kits.delkitusage", true)
        createkitsuccess = ConfigMain.getString(source1, "Kits.createkitsuccess", true)
        createkitproblem = ConfigMain.getString(source1, "Kits.createkitproblem", true)
        delkitsuccess = ConfigMain.getString(source1, "Kits.delkitsuccess", true)
        delkitproblem = ConfigMain.getString(source1, "Kits.delkitproblem", true)
        editkitsuccess = ConfigMain.getString(source1, "Kits.editkitsuccess", true)
        editkitproblem = ConfigMain.getString(source1, "Kits.editkitproblem", true)
    }
}