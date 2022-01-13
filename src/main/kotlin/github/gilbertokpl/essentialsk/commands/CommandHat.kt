package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHat : CommandCreator {
    override val active: Boolean = MainConfig.hatActivated
    override val consoleCanUse: Boolean = false
    override val commandName = "hat"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.hat"
    override val minimumSize = 0
    override val maximumSize = 0
    override val commandUsage = listOf(
        "/hat"
    )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = s as Player

        val itemHand = p.inventory.itemInMainHand

        if (itemHand.type == Material.AIR) {
            p.sendMessage(GeneralLang.hatSendNotFound)
            return false
        }

        val helmet = p.inventory.helmet

        p.inventory.helmet = itemHand
        p.inventory.setItemInMainHand(helmet)

        p.sendMessage(GeneralLang.hatSendSuccess)

        return false
    }
}