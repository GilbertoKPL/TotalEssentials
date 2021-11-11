package io.github.gilbertodamim.kcore.management

import io.github.gilbertodamim.kcore.KCoreMain.instance
import io.github.gilbertodamim.kcore.KCoreMain.pluginTagName
import io.github.gilbertodamim.kcore.config.langs.TimeLang.*
import io.github.gilbertodamim.kcore.dao.Dao.Materials
import org.bukkit.Material
import org.bukkit.entity.Player


object Manager {

    fun consoleMessage(msg: String) {
        instance.server.consoleSender.sendMessage("$pluginTagName $msg")
    }

    fun pluginPasteDir(): String = instance.dataFolder.path

    fun pluginLangDir(): String = instance.dataFolder.path + "/lang/"

    fun getPlayerUUID(p: Player): String {
        return p.uniqueId.toString()
    }

    fun Player.sendMessageWithSound(message: String, Sound : String, sound : Boolean = true) {
        player?.sendMessage(message)
        if (!sound) return
        try {
            player?.playSound(player!!.location, org.bukkit.Sound.valueOf(Sound), 1F, 1F)
        } catch (il : IllegalArgumentException) {
            try {
                player?.playSound(
                    player!!.location,
                    org.bukkit.Sound.valueOf(
                        Sound.replace("_BLOCK_", "").replace("_ENTITY_", "").replace("_ITEM_", "")
                    ),
                    1F,
                    1F
                )
            } catch (il : IllegalArgumentException) {
                player?.playSound(
                    player!!.location,
                    org.bukkit.Sound.valueOf(
                        Sound.replace("BLOCK_", "").replace("_BLOCK_", "").replace("ENTITY_", "").replace("ITEM_", "")
                    ),
                    1F,
                    1F
                )
            }
        }
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
            timeDay
        } else timeDays
        helper(days, "$days $timeDayShort", "$days $uniDays")
        val uniHours = if (hours < 2) {
            timeHour
        } else timeHours
        helper(hours, "$hours $timeHourShort", "${hours % 24} $uniHours")
        val uniMinutes = if (minutes < 2) {
            timeMinute
        } else timeMinutes
        helper(minutes, "$minutes $timeMinuteShort", "${minutes % 60} $uniMinutes")
        val uniSeconds = if (seconds < 2) {
            timeSecond
        } else timeSeconds
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