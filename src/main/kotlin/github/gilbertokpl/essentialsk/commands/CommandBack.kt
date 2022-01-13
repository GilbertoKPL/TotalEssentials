package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.objects.PlayerDataV2
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : CommandCreator {

    override val active: Boolean = MainConfig.backActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "back"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.back"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/back")

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val p = s as Player

        val playerCache = PlayerDataV2[p] ?: return false

        val loc = playerCache.backLocation ?: run {
            p.sendMessage(GeneralLang.backSendNotToBack)
            return false
        }

        if (MainConfig.backDisabledWorlds.contains(loc.world!!.name.lowercase())) {
            p.sendMessage(GeneralLang.backSendNotToBack)
            playerCache.clearBack()
            return false
        }

        p.teleport(loc)

        playerCache.clearBack()

        p.sendMessage(GeneralLang.backSendSuccess)
        return false
    }
}
