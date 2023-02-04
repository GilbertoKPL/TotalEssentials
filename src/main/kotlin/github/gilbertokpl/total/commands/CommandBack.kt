package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandCreator
import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : CommandCreator("back") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("voltar"),
            active = MainConfig.backActivated,
            target = CommandTarget.PLAYER,
            countdown = 0,
            permission = "totalessentials.commands.back",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf("/back")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        val loc = PlayerData.backLocation[p] ?: run {
            p.sendMessage(LangConfig.backNotToBack)
            return false
        }

        if (MainConfig.backDisabledWorlds.contains(loc.world!!.name.lowercase())) {
            p.sendMessage(LangConfig.backNotToBack)
            PlayerData.backLocation[p.name] = null
            return false
        }

        p.teleport(loc)

        PlayerData.backLocation[p.name] = null

        p.sendMessage(LangConfig.backSuccess)
        return false
    }
}
