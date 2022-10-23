package github.gilbertokpl.total.commands

import github.gilbertokpl.base.external.command.CommandTarget
import github.gilbertokpl.base.external.command.annotations.CommandPattern
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.cache.KitsData
import github.gilbertokpl.total.util.ItemUtil
import org.bukkit.command.CommandSender

class CommandGiveKit : github.gilbertokpl.base.external.command.CommandCreator("givekit") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("darkit"),
            active = MainConfig.kitsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.givekit",
            minimumSize = 2,
            maximumSize = 2,
            usage = listOf(
                "/givekit <playerName> <kitName>"
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }


        //check if not exist
        if (!KitsData.checkIfExist(args[0])) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        //check if player not exist
        val p = github.gilbertokpl.total.TotalEssentials.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        //give kit
        ItemUtil.giveKit(p, KitsData.kitItems[args[1]]!!, true, drop = true)

        val fakeName = KitsData.kitFakeName[args[1]]!!

        s.sendMessage(
            LangConfig.kitsGiveKitMessageOther.replace("%kit%", fakeName)
                .replace("%player%", p.name)
        )
        p.sendMessage(
            LangConfig.kitsGiveKitMessage.replace(
                "%kit%",
                fakeName
            )
        )
        return false
    }
}
