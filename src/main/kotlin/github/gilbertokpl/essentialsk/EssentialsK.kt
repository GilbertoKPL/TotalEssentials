package github.gilbertokpl.essentialsk

import github.gilbertokpl.essentialsk.api.EssentialsKAPI
import github.gilbertokpl.essentialsk.config.ConfigManager
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.config.otherConfigs.StartLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.manager.loops.DataLoop
import github.gilbertokpl.essentialsk.player.loader.DataLoader
import github.gilbertokpl.essentialsk.util.*
import github.slimjar.app.builder.ApplicationBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture

internal class EssentialsK : JavaPlugin() {

    override fun onLoad() {

        val startInstant = Instant.now()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Loading Libary, please wait, first time may take up to 1 minute...${EColor.RESET.color}"
        )

        ApplicationBuilder.appending("essentialsK").downloadDirectoryPath(
            File(this.dataFolder.path, "lib").toPath()
        ).build()

        instance = this

        api = EssentialsKAPI(this)

        val timeTakenMillis = Duration.between(startInstant, Instant.now()).toMillis()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Libary loaded in ${timeTakenMillis / 1000} seconds${EColor.RESET.color}"
        )

        MaterialUtil.startMaterials()

        ConfigManager.start()

        if (MainConfig.moneyActivated) {
            MainUtil.setupEconomy()
        }

        super.onLoad()
    }

    override fun onEnable() {

        ManifestUtil.start()

        if (VersionUtil.check()) return

        MainUtil.startMetrics()

        DataManager.startSql()

        DataManager.startTables()

        MainUtil.consoleMessage(StartLang.startLoadData)

        val quant = DataLoader.loadCache()

        MainUtil.consoleMessage(StartLang.finishLoadData.replace("%quant%", quant.toString()))

        MainUtil.startCommands()

        MainUtil.startInventories()

        MainUtil.startEvents()

        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true
        }

        TimeUtil.start()

        DataLoop.start()

        MoneyUtil.refreashTycoon()

        CompletableFuture.runAsync {
            DiscordUtil.startBot()
        }

        super.onEnable()
    }

    override fun onDisable() {

        MainUtil.consoleMessage(EColor.YELLOW.color + LangConfig.generalSaveDataMessage + EColor.RESET.color)
        DataManager.save()
        MainUtil.consoleMessage(EColor.YELLOW.color + LangConfig.generalSaveDataSuccess + EColor.RESET.color)

        TaskUtil.disable()

        DiscordUtil.jda = null

        super.onDisable()
    }

    companion object {
        lateinit var instance: EssentialsK

        lateinit var api: EssentialsKAPI

        var lowVersion = false
    }
}
