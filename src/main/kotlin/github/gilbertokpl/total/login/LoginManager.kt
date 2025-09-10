package github.gilbertokpl.total.login

import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.LoginData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.entity.Player

object LoginManager {
    fun loginMessage(player: Player) {
        val task = TotalEssentials.getCore().getTask()

        if (LoginData.doesPlayerExist(player)) {
            player.sendMessage(LangConfig.authLoginMessage)
        } else {
            player.sendMessage(LangConfig.authRegisterMessage)
        }

        task.async {
            while (true) {
                task.waitSeconds(10)

                if (!player.isOnline || LoginData.isPlayerLoggedIn(player)) {
                    break
                }

                if ((MainConfig.authMaxAttempts + 1) == LoginData.values[player]) {
                    task.sync {
                        player.kickPlayer(LangConfig.authKickMessageTime)
                    }
                    break
                }

                task.sync {
                    if (LoginData.doesPlayerExist(player)) {
                        player.sendMessage(LangConfig.authLoginMessage)
                    } else {
                        player.sendMessage(LangConfig.authRegisterMessage)
                    }
                }

                LoginData.values[player] = LoginData.values[player]?.plus(1)!!
            }
        }
    }

}