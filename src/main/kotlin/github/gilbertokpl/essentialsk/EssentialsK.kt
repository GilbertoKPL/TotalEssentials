package github.gilbertokpl.essentialsk

import github.gilbertokpl.essentialsk.manager.EColor
import github.gilbertokpl.essentialsk.util.*
import github.gilbertokpl.libchecker.app.builder.ApplicationBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture

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

        ManifestUtil.start()

        MainUtil.startMetrics()

        super.onLoad()
    }

    override fun onEnable() {

        if (VersionUtil.check()) return

        ConfigUtil.start()

        SqlUtil.startSql()

        SqlUtil.startTables()

        MainUtil.startCommands()

        MaterialUtil.startMaterials()

        MainUtil.startInventories()

        MainUtil.startEvents()

        if (Bukkit.getBukkitVersion().contains("1.5.2") || Bukkit.getVersion().contains("1.5.2")) {
            lowVersion = true
        }

        CompletableFuture.runAsync {
            DiscordUtil.startBot()
        }

        super.onEnable()
    }

    override fun onDisable() {

        TaskUtil.disable()

        super.onDisable()
    }

    companion object {
        lateinit var instance: EssentialsK
        var lowVersion = false
    }
}
