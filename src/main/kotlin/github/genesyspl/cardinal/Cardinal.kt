package github.genesyspl.cardinal

import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.util.*
import github.gilbertokpl.libchecker.app.builder.ApplicationBuilder
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture


class Cardinal : JavaPlugin() {

    override fun onLoad() {

        val startInstant = Instant.now()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Loading Libraries, please wait...${EColor.RESET.color}"
        )

        ApplicationBuilder.appending("cardinal").downloadDirectoryPath(
            File(this.dataFolder.path, "lib").toPath()
        ).build()

        github.genesyspl.cardinal.Cardinal.Companion.instance = this

        val timeTakenMillis = Duration.between(startInstant, Instant.now()).toMillis()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Loaded libraries in ${timeTakenMillis / 1000} seconds${EColor.RESET.color}"
        )

        ManifestUtil.getInstance().start()

        PluginUtil.getInstance().startMetrics()

        super.onLoad()
    }

    override fun onEnable() {

        if (VersionUtil.getInstance().check()) return

        ConfigUtil.getInstance().start()

        SqlUtil.getInstance().startSql()

        SqlUtil.getInstance().startTables()

        PluginUtil.getInstance().startCommands()

        MaterialUtil.getInstance().startMaterials()

        PluginUtil.getInstance().startInventories()

        PluginUtil.getInstance().startEvents()

        CompletableFuture.runAsync {
            DiscordUtil.getInstance().startBot()
        }

        super.onEnable()
    }

    override fun onDisable() {

        TaskUtil.getInstance().disable()

        super.onDisable()
    }

    companion object {
        lateinit var instance: github.genesyspl.cardinal.Cardinal
    }
}