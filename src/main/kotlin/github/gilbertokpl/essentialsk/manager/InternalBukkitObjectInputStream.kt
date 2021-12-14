package github.gilbertokpl.essentialsk.manager

import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.io.InputStream
import java.io.ObjectInputStream

internal class InternalBukkitObjectInputStream(input: InputStream?) : ObjectInputStream(input) {
    init {
        enableResolveObject(true)
    }

    override fun resolveObject(obj: Any): Any {
        var obj1: Any? = obj
        if (obj1 is Wrapper<*>) try {
            ConfigurationSerialization.deserializeObject(obj1.map).also {
                obj1 = it
            }!!.javaClass
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
        return super.resolveObject(obj1)
    }
}