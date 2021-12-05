package github.gilbertokpl.essentialsk

import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import github.gilbertokpl.libchecker.app.builder.ApplicationBuilder
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.Duration
import java.time.Instant

class EssentialsK : JavaPlugin() {

    override fun onLoad() {

        val startInstant = Instant.now()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Loading Libraries, please wait...${EColor.RESET.color}"
        )

        ApplicationBuilder.appending("essentialsK").downloadDirectoryPath(
            File(this.dataFolder.path, "lib").toPath()
        ).build()

        instance = this

        val timeTakenMillis = Duration.between(startInstant, Instant.now()).toMillis()
        println(
            "${EColor.CYAN.color}[${name}]${EColor.RESET.color} " +
                    "${EColor.YELLOW.color}Loaded libraries in ${timeTakenMillis / 1000} seconds${EColor.RESET.color}"
        )

        ManifestUtil.getInstance().start()

        PluginUtil.getInstance().disableLoggers()

        PluginUtil.getInstance().startMetrics()

        super.onLoad()
    }

    override fun onEnable() {

        if (VersionUtil.getInstance().check()) return

        ConfigUtil.getInstance().start()

        SqlUtil.getInstance().startSql()

        SqlUtil.getInstance().startTables()

        PluginUtil.getInstance().startCommands()

        PluginUtil.getInstance().startMaterials()

        PluginUtil.getInstance().startInventories()

        PluginUtil.getInstance().startEvents()

        super.onEnable()
    }

    override fun onDisable() {

        TaskUtil.getInstance().disable()

        super.onDisable()
    }

    companion object {
        lateinit var instance: EssentialsK
    }
}