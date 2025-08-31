package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.annotations.CommandPattern
import github.gilbertokpl.core.command.external.CommandCreator
import github.gilbertokpl.core.command.interfaces.CommandTarget
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.cache.data.KitsData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.ItemUtil
import org.bukkit.command.CommandSender

class CommandGiveKit : CommandCreator("givekit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("darkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.givekit",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf("/givekit <playerName> <kitName>")
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        // Verifica tamanho do nome do jogador
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        // Verifica se o kit existe
        if (!KitsData.checkIfExist(args[1])) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        // Pega o jogador online
        val p = TotalEssentials.getInstance().server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        // Dá o kit ao jogador
        ItemUtil.giveKit(p, KitsData.kitItems[args[1]]!!, true, drop = true)

        val fakeName = KitsData.kitFakeName[args[1]]!!

        // Mensagens para executor e jogador
        s.sendMessage(LangConfig.kitsGiveKitMessageOther.replace("%kit%", fakeName).replace("%player%", p.name))
        p.sendMessage(LangConfig.kitsGiveKitMessage.replace("%kit%", fakeName))

        return false
    }
}
