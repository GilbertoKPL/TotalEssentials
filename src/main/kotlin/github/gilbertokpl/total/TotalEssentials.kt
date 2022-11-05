package github.gilbertokpl.total

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.internal.OtherConfig
import github.gilbertokpl.total.cache.sql.KitsDataSQL
import github.gilbertokpl.total.cache.sql.PlayerDataSQL
import github.gilbertokpl.total.cache.sql.SpawnDataSQL
import github.gilbertokpl.total.cache.sql.WarpsDataSQL
import github.gilbertokpl.total.util.ColorUtil
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

        basePlugin = CorePlugin(this)

        basePlugin.start(
            "github.gilbertokpl.total.commands",
            "github.gilbertokpl.total.listeners",
            "github.gilbertokpl.total.config.files",
            "github.gilbertokpl.total.cache.local",
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

        MainUtil.consoleMessage(ColorUtil.YELLOW.color + LangConfig.generalSaveDataMessage + ColorUtil.RESET.color)
        basePlugin.stop()
        MainUtil.consoleMessage(ColorUtil.YELLOW.color + LangConfig.generalSaveDataSuccess + ColorUtil.RESET.color)

        TaskUtil.disable()

        super.onDisable()
    }

    companion object {
        lateinit var instance: TotalEssentials

        lateinit var basePlugin: CorePlugin

        var lowVersion = false
    }
}
