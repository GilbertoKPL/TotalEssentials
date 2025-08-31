package github.gilbertokpl.core

import github.gilbertokpl.core.cache.CoreCache
import github.gilbertokpl.core.config.external.Config
import github.gilbertokpl.core.config.values.Value
import github.gilbertokpl.core.events.CoreEvents
import github.gilbertokpl.core.task.CoreTask
import github.gilbertokpl.core.utils.*
import github.gilbertokpl.total.TotalEssentials
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database

class CorePlugin(pl: Plugin) {

    val plugin = pl

    lateinit var sql: Database

    private lateinit var configPackageReload: String

    lateinit var mainPath: String

    lateinit var langPath: String

    var online = System.currentTimeMillis()

    var serverPrefix = ""

    //obf
    private val timeInstance = TimeUtil(this)

    //obf
    private val colorInstance = ColorUtil()

    //obf
    private val reflectionInstance = ReflectionUtil(this)

    //obf
    private val databaseInstance = DatabaseUtil(this)

    //obf
    private val configInstance = Config(this)

    //deobf
    private val valueInstance = Value(this)

    //deobf
    private val taskInstance = CoreTask(TotalEssentials.getInstance())

    //obf
    private val hostInstance = HostUtil(this)

    //deobf
    private val cacheInstance = CoreCache(this)

    //obf
    private val inventoryInstance = InventoryUtil()

    //obf
    private val encryptInstance = EncryptUtil()

    //logger
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
        CoreEvents(this).start(listenerPackage)
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

    fun getConfig(): Config {
        return configInstance
    }

    fun getValue(): Value {
        return valueInstance
    }

    fun getTask(): CoreTask {
        return taskInstance
    }

    fun getHost(): HostUtil {
        return hostInstance
    }

    fun getCache(): CoreCache {
        return cacheInstance
    }

    fun getInventory(): InventoryUtil {
        return inventoryInstance
    }

    fun getEncrypt(): EncryptUtil {
        return encryptInstance
    }


}