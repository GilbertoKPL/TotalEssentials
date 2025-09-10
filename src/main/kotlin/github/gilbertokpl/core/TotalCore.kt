package github.gilbertokpl.core

import github.gilbertokpl.core.cache.CacheManager
import github.gilbertokpl.core.config.ConfigManager
import github.gilbertokpl.core.config.values.ConfigValue
import github.gilbertokpl.core.event.EventManager
import github.gilbertokpl.core.task.TaskManager
import github.gilbertokpl.core.utils.*
import github.gilbertokpl.total.TotalEssentials
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database

class TotalCore(pl: Plugin) {

    val plugin = pl

    lateinit var sql: Database

    private lateinit var configPackageReload: String

    lateinit var mainPath: String

    lateinit var langPath: String

    var online = System.currentTimeMillis()

    var serverPrefix = ""

    private val timeInstance = TimeUtil(this)

    private val colorInstance = ColorUtil()

    private val reflectionInstance = ReflectionUtil(this)

    private val databaseInstance = DatabaseUtil(this)

    private val configInstance = ConfigManager(this)

    private val configValueInstance = ConfigValue(this)

    private val taskInstance = TaskManager(TotalEssentials.getInstance())

    private val hostInstance = HostUtil(this)

    private val cacheInstance = CacheManager(this)

    private val inventoryInstance = InventoryUtil()

    private val encryptInstance = EncryptUtil()

    lateinit var logger: FileLogger

    fun startConfig(configPackage: String) {
        mainPath = plugin.dataFolder.path.replace(".paper-remapped/", "")
        langPath = plugin.dataFolder.path + "/lang/".replace(".paper-remapped/", "")
        configPackageReload = configPackage
        getConfig().start(configPackage)
    }

    fun start(
        commandPackage: String,
        listenerPackage: String,
        cachePackage: String,
        databaseTable: List<Table>
    ) {
        logger = FileLogger()
        getDatabase().start(databaseTable)
        getReflection().registerCommandByPackage(commandPackage)
        EventManager(this).start(listenerPackage)
        getCache().start(cachePackage)
        getHost().start()
    }

    fun reloadConfig(): Boolean {
        return try {
            getConfig().start(configPackageReload)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun stop() {
        getCache().stop()
    }

    fun getTime(): TimeUtil {
        return timeInstance
    }

    fun getColor(): ColorUtil {
        return colorInstance
    }

    fun getReflection(): ReflectionUtil {
        return reflectionInstance
    }

    fun getDatabase(): DatabaseUtil {
        return databaseInstance
    }

    fun getConfig(): ConfigManager {
        return configInstance
    }

    fun getValue(): ConfigValue {
        return configValueInstance
    }

    fun getTask(): TaskManager {
        return taskInstance
    }

    fun getHost(): HostUtil {
        return hostInstance
    }

    fun getCache(): CacheManager {
        return cacheInstance
    }

    fun getInventory(): InventoryUtil {
        return inventoryInstance
    }

    fun getEncrypt(): EncryptUtil {
        return encryptInstance
    }

}