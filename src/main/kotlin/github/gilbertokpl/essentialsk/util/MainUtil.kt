package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.commands.*
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.KitDataV2
import github.gilbertokpl.essentialsk.data.objects.SpawnDataV2
import github.gilbertokpl.essentialsk.data.objects.WarpDataV2
import github.gilbertokpl.essentialsk.inventory.EditKitInventory
import github.gilbertokpl.essentialsk.inventory.KitGuiInventory
import github.gilbertokpl.essentialsk.listeners.*
import github.gilbertokpl.essentialsk.manager.EColor
import org.apache.commons.lang3.exception.ExceptionUtils
import org.bstats.bukkit.Metrics
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import java.awt.Color
import java.io.InputStream
import java.net.URL
import java.util.concurrent.CompletableFuture

object MainUtil {

    private const val METRICS_ID = 13_441

    val mainPath: String = EssentialsK.instance.dataFolder.path

    val langPath: String = EssentialsK.instance.dataFolder.path + "/lang/"

    val pluginPath: String = EssentialsK.instance.javaClass.protectionDomain.codeSource.location.path

    fun fileDownloader(url: String): InputStream? {
        val stream = URL(url).openConnection()
        stream.connect()
        return stream.getInputStream()
    }

    fun consoleMessage(message: String) {
        println("${EColor.CYAN.color}[${EssentialsK.instance.name}]${EColor.RESET.color} $message")
    }

    fun serverMessage(message: String) {
        ReflectUtil.getPlayers().forEach {
            it.sendMessage(message)
        }
    }

    fun startEvents() {
        startEventsHelper(
            listOf(
                InventoryClick(),
                InventoryClose(),
                ChatEventAsync(),
                PlayerJoin(),
                PlayerLeave(),
                PlayerPreCommand(),
                PlayerDeath(),
                PlayerTeleport(),
                PlayerBedEnter(),
                EntityVehicleEnter(),
                PlayerInteractEntity(),
                CreatureSpawn(),
                InventoryOpen(),
                EntityPortalCreate(),
                PlayerPortal(),
                EntityDamage(),
                PlayerInteract(),
                EntityDamageEntity(),
                EntityChangeBlock(),
                BurnEvent(),
                IgniteEvent(),
                EntitySignChange(),
                PlayerChangeWorld(),
                ServerListPing(),
                PlayerRespawn(),
                WeatherChange()
            )
        )
    }

    private fun startEventsHelper(event: List<Listener>) {
        event.forEach {
            EssentialsK.instance.server.pluginManager.registerEvents(it, EssentialsK.instance)
        }
    }

    private fun loadCache(): Boolean {
        return CompletableFuture.supplyAsync({
            try {
                KitDataV2.loadKitCache()
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                WarpDataV2.loadWarpCache()
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            try {
                SpawnDataV2.loadSpawnCache()
            } catch (ex: Exception) {
                FileLoggerUtil.logError(ExceptionUtils.getStackTrace(ex))
            }
            return@supplyAsync true
        }, TaskUtil.getExecutor()).get()
    }

    fun startInventories() {
        if (loadCache() && MainConfig.kitsActivated) {
            EditKitInventory.setup()
            KitGuiInventory.setup()
        }
    }

    fun startCommands() {
        startCommandsHelper(
            listOf(
                CommandEssentialsK()
            ),
            true
        )
        //kits
        startCommandsHelper(
            listOf(
                CommandCreateKit(),
                CommandDelKit(),
                CommandEditKit(),
                CommandKit(),
                CommandGiveKit()
            ),
            MainConfig.kitsActivated
        )
        //nick
        startCommandsHelper(
            listOf(
                CommandNick()
            ),
            MainConfig.nicksActivated
        )
        //homes
        startCommandsHelper(
            listOf(
                CommandSetHome(),
                CommandHome(),
                CommandDelHome()
            ),
            MainConfig.homesActivated
        )
        //warps
        startCommandsHelper(
            listOf(
                CommandDelWarp(),
                CommandSetWarp(),
                CommandWarp()
            ),
            MainConfig.warpsActivated
        )
        //tpa
        startCommandsHelper(
            listOf(
                CommandTpa(),
                CommandTpaccept(),
                CommandTpdeny()
            ),
            MainConfig.tpaActivated
        )
        //tp
        startCommandsHelper(
            listOf(
                CommandTp()
            ),
            MainConfig.tpActivated
        )
        //tphere
        startCommandsHelper(
            listOf(
                CommandTphere()
            ),
            MainConfig.tphereActivated
        )
        //echest
        startCommandsHelper(
            listOf(
                CommandEchest()
            ),
            MainConfig.echestActivated
        )
        //gamemode
        startCommandsHelper(
            listOf(
                CommandGamemode()
            ),
            MainConfig.gamemodeActivated
        )
        //vanish
        startCommandsHelper(
            listOf(
                CommandVanish()
            ),
            MainConfig.vanishActivated
        )
        //feed
        startCommandsHelper(
            listOf(
                CommandFeed()
            ),
            MainConfig.feedActivated
        )
        //heal
        startCommandsHelper(
            listOf(
                CommandHeal()
            ),
            MainConfig.healActivated
        )
        //light
        startCommandsHelper(
            listOf(
                CommandLight()
            ),
            MainConfig.lightActivated
        )
        //back
        startCommandsHelper(
            listOf(
                CommandBack()
            ),
            MainConfig.backActivated
        )
        //spawn
        startCommandsHelper(
            listOf(
                CommandSpawn(),
                CommandSetSpawn()
            ),
            MainConfig.spawnActivated
        )
        //fly
        startCommandsHelper(
            listOf(
                CommandFly(),
            ),
            MainConfig.flyActivated
        )
        //online
        startCommandsHelper(
            listOf(
                CommandOnline(),
            ),
            MainConfig.onlineActivated
        )
        //announce
        startCommandsHelper(
            listOf(
                CommandAnnounce(),
            ),
            MainConfig.announceActivated
        )
        //craft
        startCommandsHelper(
            listOf(
                CommandCraft(),
            ),
            MainConfig.craftActivated
        )
        //trash
        startCommandsHelper(
            listOf(
                CommandTrash(),
            ),
            MainConfig.trashActivated
        )
        //speed
        startCommandsHelper(
            listOf(
                CommandSpeed(),
            ),
            MainConfig.speedActivated
        )
    }

    private fun startCommandsHelper(commands: List<CommandExecutor>, boolean: Boolean) {
        for (to in commands) {
            val cmdName = to.javaClass.name.replace(
                "github.gilbertokpl.essentialsk.commands.Command",
                ""
            ).lowercase()
            if (!boolean) {
                ReflectUtil.removeCommand(cmdName)
                return
            }
            EssentialsK.instance.getCommand(
                cmdName
            )?.setExecutor(to)

        }
    }

    fun randomColor(): Color = Color.getHSBColor(
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat(),
        (Math.random() * 255 + 1).toFloat()
    )

    fun checkSpecialCaracteres(s: String?): Boolean {
        return s?.matches(Regex("[^A-Za-z0-9 ]")) ?: false
    }

    fun startMetrics() {
        Metrics(EssentialsK.instance, METRICS_ID)
    }
}
