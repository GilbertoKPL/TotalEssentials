package io.github.gilbertodamim.kcore.management

import com.google.common.collect.ImmutableMap
import io.github.gilbertodamim.kcore.KCoreMain.instance
import io.github.gilbertodamim.kcore.KCoreMain.pluginTagName
import io.github.gilbertodamim.kcore.config.langs.TimeLang
import io.github.gilbertodamim.kcore.config.langs.TimeLang.*
import io.github.gilbertodamim.kcore.management.dao.Dao.Materials
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.entity.Player
import java.io.*


object Manager {
    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage("$pluginTagName $msg")
    }

    fun pluginPasteDir(): String = instance.dataFolder.path
    fun pluginLangDir(): String = instance.dataFolder.path + "/lang/"
    fun getPlayerUUID(p: Player): String {
        return p.uniqueId.toString()
    }

    fun convertMillisToString(time: Long, short: Boolean): String {
        val toSend = ArrayList<String>()
        fun helper(time: Long, sendShort: String, send: String) {
            if (time > 0L) {
                if (short) {
                    toSend.add(sendShort)
                } else {
                    toSend.add(send)
                }
            }
        }

        var seconds = time / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        val days = hours / 24
        seconds %= 60
        minutes %= 60
        hours %= 24
        val uniDays = if (days < 2) {
            TimeLang.timeDay
        } else TimeLang.timeDays
        helper(days, "$days $timeDayShort", "$days $uniDays")
        val uniHours = if (hours < 2) {
            TimeLang.timeHour
        } else TimeLang.timeHours
        helper(hours, "$hours $timeHourShort", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            TimeLang.timeMinute
        } else TimeLang.timeMinutes
        helper(minutes, "$minutes $timeMinuteShort", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            TimeLang.timeSecond
        } else TimeLang.timeSeconds
        helper(seconds, "$seconds $timeSecondShort", "${seconds % 60} $uniSeconds")
        var toReturn = ""
        var quaint = 0
        for (values in toSend) {
            quaint += 1
            toReturn = if (quaint == values.length) {
                if (toReturn == "") {
                    "$values."
                } else {
                    "$toReturn, $values."
                }
            } else {
                if (toReturn == "") {
                    values
                } else {
                    "$toReturn, $values"
                }
            }
        }
        return toReturn
    }

    fun startMaterials() {
        fun help(material: List<String>): Material {
            var mat = Material.AIR
            for (i in material) {
                val toPut = Material.getMaterial(i)
                if (toPut != null) {
                    mat = toPut
                    break
                }
            }
            return mat
        }
        Materials["glass"] = help(listOf("STAINED_GLASS_PANE", "THIN_GLASS", "YELLOW_STAINED_GLASS"))
        Materials["clock"] = help(listOf("CLOCK", "WATCH"))
        Materials["feather"] = help(listOf("FEATHER"))
    }
}

internal class Wrapper<T> private constructor(val map: ImmutableMap<String, *>) :
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

class KCoreBukkitObjectOutputStream(out: OutputStream?) : ObjectOutputStream(out) {
    init {
        enableReplaceObject(true)
    }

    @Throws(IOException::class)
    override fun replaceObject(obj: Any): Any {
        var obj1: Any = obj
        if (obj1 !is Serializable && obj1 is ConfigurationSerializable) obj1 = Wrapper.newWrapper(
            (obj1 as ConfigurationSerializable?)!!
        )
        return super.replaceObject(obj1)
    }
}
