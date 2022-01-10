package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.data.DataManager
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CommandGiveKit : CommandCreator {
    override val consoleCanUse: Boolean = true
    override val commandName = "givekit"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.givekit"
    override val minimumSize = 2
    override val maximumSize = 2
    override val commandUsage = listOf("/givekit <playerName> <kitName>")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //check length of kit name
        if (args[0].length > 16) {
            s.sendMessage(GeneralLang.kitsNameLength)
            return false
        }

        val dataInstance = DataManager.kitCacheV2[args[1]]

        //check if not exist
        if (dataInstance == null) {
            s.sendMessage(GeneralLang.kitsNotExist)
            return false
        }

        //check if player not exist
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.generalPlayerNotOnline)
            return false
        }

        //give kit
        ItemUtil.giveKit(p, dataInstance.items, true, drop = true)
        s.sendMessage(
            GeneralLang.kitsGiveKitMessageOther.replace("%kit%", dataInstance.fakeName).replace("%player%", p.name)
        )
        p.sendMessage(GeneralLang.kitsGiveKitMessage.replace("%kit%", dataInstance.fakeName))
        return false
    }
}
