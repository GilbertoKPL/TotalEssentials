package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import org.bukkit.entity.Player

internal data class DataTeleport(
    val p: Player,
    var otherPlayer: Player?,
    var wait: Boolean
) {
    companion object {
        private val tpaData = HashMap<Player, DataTeleport>()

        operator fun get(p: Player) = tpaData[p]

        fun remove(p: Player) {
            tpaData.remove(p)
        }

        fun checkTpa(p: Player): Boolean {
            return tpaData.contains(p)
        }

        fun checkOtherTpa(p: Player): Boolean {
            return tpaData.values.any { it.otherPlayer == p }
        }

        fun getTpa(p: Player): Player? {
            return tpaData.entries.find { it.value.otherPlayer == p }?.key
        }


        fun createNewTpa(pSender: Player, pReceived: Player, time: Int) {
            val task = TotalEssentials.getCore().getTask()

            val dataTeleport = DataTeleport(pSender, pReceived, true)
            tpaData[pSender] = dataTeleport

            task.supplyLater(time.toLong()) {
                val senderData = tpaData[pSender]
                if (senderData?.wait == true) {
                    tpaData.remove(pSender)

                    task.sync {
                        pSender.sendMessage(
                            LangConfig.tpaRequestOtherDenyTime.replace("%player%", pReceived.name)
                        )
                        pReceived.sendMessage(
                            LangConfig.tpaRequestDeny.replace("%player%", pSender.name)
                        )
                    }
                }
            }
        }

    }
}
