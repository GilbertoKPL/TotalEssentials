package github.gilbertokpl.total

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.data.OtherConfig
import github.gilbertokpl.total.database.KitsDataSQL
import github.gilbertokpl.total.database.PlayerDataSQL
import github.gilbertokpl.total.database.SpawnDataSQL
import github.gilbertokpl.total.database.WarpsDataSQL
import github.gilbertokpl.total.manager.EColor
import github.gilbertokpl.total.util.MainUtil
import github.gilbertokpl.total.util.MaterialUtil
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal class TotalEssentials : JavaPlugin() {

    override fun onLoad() {

        instance = this

        MaterialUtil.startMaterials()

        super.onLoad()
    }

    override fun onEnable() {

        basePlugin = BasePlugin(this)

        basePlugin.start(
            "github.gilbertokpl.total.commands",
            "github.gilbertokpl.total.listeners",
            "github.gilbertokpl.total.config.files",
            "github.gilbertokpl.total.cache",
            listOf(KitsDataSQL, PlayerDataSQL, SpawnDataSQL, WarpsDataSQL)
        )

        OtherConfig.start(
            MainConfig.announcementsListAnnounce,
            LangConfig.deathmessagesCauseReplacer,
            LangConfig.deathmessagesEntityReplacer
        )

        MainUtil.startInventories()

        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true
        }


        super.onEnable()
    }

    override fun onDisable() {

        MainUtil.consoleMessage(EColor.YELLOW.color + LangConfig.generalSaveDataMessage + EColor.RESET.color)
        basePlugin.stop()
        MainUtil.consoleMessage(EColor.YELLOW.color + LangConfig.generalSaveDataSuccess + EColor.RESET.color)

        TaskUtil.disable()

        super.onDisable()
    }

    companion object {
        lateinit var instance: TotalEssentials

        lateinit var basePlugin: BasePlugin

        var lowVersion = false
    }
}
