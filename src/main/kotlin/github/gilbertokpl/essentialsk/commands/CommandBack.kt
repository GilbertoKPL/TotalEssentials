package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.data.Dao
import github.gilbertokpl.essentialsk.data.PlayerData
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBack : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "back"
    override val timeCoolDown : Long? = null
    override val permission: String = "essentialsk.commands.back"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf("/back")
    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!Dao.getInstance().backLocation.containsKey(s)) {
            s.sendMessage(GeneralLang.getInstance().backSendNotToBack)
            return false
        }

        val loc = Dao.getInstance().backLocation[s]!!

        if (MainConfig.getInstance().backDisabledWorlds.contains(loc.world!!.name.lowercase())) {
            s.sendMessage(GeneralLang.getInstance().backSendNotToBack)
            Dao.getInstance().backLocation.remove(s)
            return false
        }

        (s as Player).teleport(loc)
        Dao.getInstance().backLocation.remove(s)
        s.sendMessage(GeneralLang.getInstance().backSendSuccess)
        return false
    }
}