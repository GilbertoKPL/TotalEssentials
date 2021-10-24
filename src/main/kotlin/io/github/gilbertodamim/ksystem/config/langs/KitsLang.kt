package io.github.gilbertodamim.ksystem.config.langs

import io.github.gilbertodamim.ksystem.config.ConfigMain
import org.bukkit.configuration.file.YamlConfiguration

object KitsLang {
    lateinit var notExist: String
    lateinit var Exist: String
    lateinit var editKitTime: String
    lateinit var createKitUsage: String
    lateinit var delKitUsage: String
    lateinit var createKitSuccess: String
    lateinit var createKitProblem: String
    lateinit var delKitSuccess: String
    lateinit var delKitProblem: String
    lateinit var editKitSuccess: String
    lateinit var editKitProblem: String
    lateinit var editkitInventoryItemsName: String
    lateinit var editkitInventoryItemsLore: String
    lateinit var editkitInventoryTimeName: String
    lateinit var editkitInventoryTimeLore: String
    lateinit var editkitInventoryNameName: String
    lateinit var editkitInventoryNameLore: String
    lateinit var editkitInventoryTimeMessage: String
    lateinit var editkitInventoryNameMessage: String
    lateinit var kitInventoryIconBackName: String
    lateinit var kitInventoryIconNextName: String
    fun reload(source1: YamlConfiguration) {
        notExist = ConfigMain.getString(source1, "Kits.not-exist", true)
        Exist = ConfigMain.getString(source1, "Kits.exist", true)
        editKitTime = ConfigMain.getString(source1, "Kits.editkit-time", true)
        createKitUsage = ConfigMain.getString(source1, "Kits.createkit-usage", true)
        delKitUsage = ConfigMain.getString(source1, "Kits.delkit-usage", true)
        createKitSuccess = ConfigMain.getString(source1, "Kits.createkit-success", true)
        createKitProblem = ConfigMain.getString(source1, "Kits.createkit-problem", true)
        delKitSuccess = ConfigMain.getString(source1, "Kits.delkit-success", true)
        delKitProblem = ConfigMain.getString(source1, "Kits.delkit-problem", true)
        editKitSuccess = ConfigMain.getString(source1, "Kits.editkit-success", true)
        editKitProblem = ConfigMain.getString(source1, "Kits.editkit-problem", true)
        editkitInventoryItemsName = ConfigMain.getString(source1, "Kits.editkit-inventory-items-name", true)
        editkitInventoryItemsLore = ConfigMain.getString(source1, "Kits.editkit-inventory-items-lore", true)
        editkitInventoryTimeName = ConfigMain.getString(source1, "Kits.editkit-inventory-time-name", true)
        editkitInventoryTimeMessage = ConfigMain.getString(source1, "Kits.editkit-inventory-time-message", true)
        editkitInventoryTimeLore = ConfigMain.getString(source1, "Kits.editkit-inventory-time-lore", true)
        editkitInventoryNameName = ConfigMain.getString(source1, "Kits.editkit-inventory-name-name", true)
        editkitInventoryNameMessage = ConfigMain.getString(source1, "Kits.editkit-inventory-name-message", true)
        editkitInventoryNameLore = ConfigMain.getString(source1, "Kits.editkit-inventory-name-lore", true)
        kitInventoryIconBackName = ConfigMain.getString(source1, "Kits.kit-inventory-icon-back-name", true)
        kitInventoryIconNextName = ConfigMain.getString(source1, "Kits.kit-inventory-icon-next-name", true)
    }
}