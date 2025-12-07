package github.gilbertokpl.total.login

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player

object LoginManager {

    private const val REMINDER_INTERVAL_SECONDS = 10L

    fun loginMessage(player: Player) {
        sendAuthMessage(player)
        startLoginReminder(player)
    }

    private fun sendAuthMessage(player: Player) {
        val message = getAuthMessage(player)
        player.sendMessage(message)
    }

    private fun getAuthMessage(player: Player): String {
        return if (LoginData.doesPlayerExist(player)) {
            LangConfig.authLoginMessage
        } else {
            LangConfig.authRegisterMessage
        }
    }

    private fun startLoginReminder(player: Player) {
        val task = TotalEssentials.getCore().getTask()

        task.async {
            var attempts = LoginData.values[player] ?: 0

            while (shouldContinueReminding(player)) {
                task.waitSeconds(REMINDER_INTERVAL_SECONDS)

                if (!shouldContinueReminding(player)) break

                attempts++
                LoginData.values[player] = attempts

                if (hasExceededMaxAttempts(attempts)) {
                    task.sync {
                        if (player.isOnline) {
                            player.kickPlayer(LangConfig.authKickMessageTime)
                        }
                    }
                    break
                }

                task.sync {
                    if (player.isOnline) {
                        sendAuthMessage(player)
                    }
                }
            }
        }
    }

    private fun shouldContinueReminding(player: Player): Boolean {
        return player.isOnline && !LoginData.isPlayerLoggedIn(player)
    }

    private fun hasExceededMaxAttempts(attempts: Int): Boolean {
        return attempts >= MainConfig.authMaxAttempts + 1
    }
}