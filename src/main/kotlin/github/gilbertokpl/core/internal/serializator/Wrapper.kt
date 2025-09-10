package github.gilbertokpl.core.internal.serializator

import com.google.common.collect.ImmutableMap
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.io.Serializable

internal class Wrapper<T> private constructor(val map: Map<String, *>) :
    Serializable where T : Map<String, *>, T : Serializable {
    companion object {
        private const val serialVersionUID = -986209235411767547L
        fun newWrapper(obj: ConfigurationSerializable): Wrapper<ImmutableMap<String, *>> {
            return Wrapper(
                ImmutableMap.builder<String, Any>().put("==", ConfigurationSerialization.getAlias(obj.javaClass))
                    .putAll(obj.serialize()).build()
            )
        }
    }
}