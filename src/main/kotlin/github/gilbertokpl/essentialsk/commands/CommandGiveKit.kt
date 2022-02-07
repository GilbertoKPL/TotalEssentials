package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.data.dao.KitData
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandGiveKit : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.kitsActivated,
            consoleCanUse = true,
            commandName = "givekit",
            timeCoolDown = null,
            permission = "essentialsk.commands.givekit",
            minimumSize = 2,
            maximumSize = 2,
            commandUsage = listOf(
                "/givekit <playerName> <kitName>"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(LangConfig.kitsNameLength)
            return false
        }

        val dataInstance = KitData[args[1]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(LangConfig.kitsNotExist)
            return false
        }

        //check if player not exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        //give kit
        ItemUtil.giveKit(p, dataInstance.itemsCache, true, drop = true)
        s.sendMessage(
            LangConfig.kitsGiveKitMessageOther.replace("%kit%", dataInstance.fakeNameCache).replace("%player%", p.name)
        )
        p.sendMessage(LangConfig.kitsGiveKitMessage.replace("%kit%", dataInstance.fakeNameCache))
        return false
    }
}
