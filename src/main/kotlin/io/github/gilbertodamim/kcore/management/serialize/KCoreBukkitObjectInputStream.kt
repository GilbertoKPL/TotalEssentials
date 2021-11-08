package io.github.gilbertodamim.kcore.management.serialize

import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.io.IOException
import java.io.InputStream
import java.io.ObjectInputStream

class KCoreBukkitObjectInputStream(`in`: InputStream?) : ObjectInputStream(`in`) {
    init {
        enableResolveObject(true)
    }

    @Throws(IOException::class)
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