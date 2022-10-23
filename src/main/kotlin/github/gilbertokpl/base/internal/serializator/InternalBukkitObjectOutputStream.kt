package github.gilbertokpl.base.internal.serializator

import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.io.Serializable

internal class InternalBukkitObjectOutputStream(out: OutputStream?) : ObjectOutputStream(out) {
    init {
        enableReplaceObject(true)
    }

    override fun replaceObject(obj: Any): Any {
        var obj1: Any = obj
        if (obj1 !is Serializable && obj1 is ConfigurationSerializable) obj1 =
            Wrapper.newWrapper(
                (obj1 as ConfigurationSerializable?)!!
            )
        return super.replaceObject(obj1)
    }
}