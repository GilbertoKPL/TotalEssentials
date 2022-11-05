package github.gilbertokpl.total.cache.internal

import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.util.TaskUtil
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

internal data class TpaData(
    val p: Player,
    var otherPlayer: Player?,
    var wait: Boolean
) {
    companion object {
        private val tpaData = HashMap<Player, TpaData>()

        operator fun get(p: Player) = tpaData[p]

        fun remove(p: Player) {
            tpaData.remove(p)
        }

        fun checkTpa(p: Player): Boolean {
            return tpaData.contains(p)
        }

        fun checkOtherTpa(p: Player): Boolean {
            for (i in tpaData) {
                if (i.value.otherPlayer == p) {
                    return true
                }
            }
            return false
        }

        fun getTpa(p: Player): Player? {
            for (i in tpaData) {
                if (i.value.otherPlayer == p) {
                    return i.key
                }
            }
            return null
        }

        fun createNewTpa(pSender: Player, pReceived: Player, time: Int) {

            tpaData[pSender] = TpaData(pSender, pReceived, true)

            CompletableFuture.runAsync({
                TimeUnit.SECONDS.sleep(time.toLong())
                try {
                    val sender = tpaData[pSender] ?: return@runAsync
                    if (sender.wait) {
                        github.gilbertokpl.total.TotalEssentials.instance.server.scheduler.runTask(
                            github.gilbertokpl.total.TotalEssentials.instance,
                            Runnable {
                                pSender.sendMessage(
                                    LangConfig.tpaRequestOtherDenyTime.replace(
                                        "%player%",
                                        pReceived.name
                                    )
                                )
                            })
                        tpaData.remove(pSender)
                    }
                } catch (ex: Exception) {
                    tpaData.remove(pSender)
                }
            }, TaskUtil.getTeleportExecutor())
        }

    }
}
