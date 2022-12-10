package github.gilbertokpl.total

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.total.cache.internal.OtherConfig
import github.gilbertokpl.total.cache.internal.ShopInventory
import github.gilbertokpl.total.cache.loop.AntiAfkLoop
import github.gilbertokpl.total.cache.loop.ClearItemsLoop
import github.gilbertokpl.total.cache.loop.PluginLoop
import github.gilbertokpl.total.cache.sql.*
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.discord.Discord
import github.gilbertokpl.total.util.*
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin


internal class TotalEssentials : JavaPlugin() {

    override fun onLoad() {

        instance = this

        basePlugin = CorePlugin(this)

        MaterialUtil.startMaterials()

        basePlugin.startConfig("github.gilbertokpl.total.config.files")

        if (MainConfig.moneyActivated) {
            Bukkit.getServicesManager().register(Economy::class.java, EconomyHolder(), this, ServicePriority.Highest)
        }

        super.onLoad()
    }

    override fun onEnable() {

        basePlugin.start(
            "github.gilbertokpl.total.commands",
            "github.gilbertokpl.total.listeners",
            "github.gilbertokpl.total.cache.local",
            listOf(KitsDataSQL, PlayerDataSQL, SpawnDataSQL, WarpsDataSQL, LoginDataSQL, VipDataSQL, VipKeysSQL, ShopDataSQL)
        )

        permission = Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)!!.provider

        OtherConfig.start(
            MainConfig.announcementsListAnnounce,
            LangConfig.deathmessagesCauseReplacer,
            LangConfig.deathmessagesEntityReplacer
        )

        MainUtil.startInventories()

        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true
        }

        Discord.startBot()

        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.sendDiscordMessage(LangConfig.discordchatServerStart, true)
        }

        ClearItemsLoop.start()

        PluginLoop.start()

        this.server.logger.filter = Filter()

        super.onEnable()
    }

    override fun onDisable() {

        MainUtil.consoleMessage(ColorUtil.YELLOW.color + LangConfig.generalSaveDataMessage + ColorUtil.RESET.color)
        basePlugin.stop()
        MainUtil.consoleMessage(ColorUtil.YELLOW.color + LangConfig.generalSaveDataSuccess + ColorUtil.RESET.color)

        TaskUtil.disable()

        if (MainConfig.discordbotConnectDiscordChat) {
            Discord.sendDiscordMessage(LangConfig.discordchatServerClose, true)
        }

        super.onDisable()
    }

    companion object {
        lateinit var instance: TotalEssentials

        lateinit var basePlugin: CorePlugin

        lateinit var permission : Permission

        var lowVersion = false
    }
}
