package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHat : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.hatActivated,
            consoleCanUse = false,
            commandName = "hat",
            timeCoolDown = null,
            permission = "essentialsk.commands.hat",
            minimumSize = 0,
            maximumSize = 0,
            commandUsage = listOf(
                "/hat"
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = s as Player

        val itemHand = try {
            p.inventory.itemInMainHand
        } catch (e: NoSuchMethodError) {
            p.itemInHand
        }

        if (itemHand.type == Material.AIR) {
            p.sendMessage(LangConfig.hatSendNotFound)
            return false
        }

        val helmet = p.inventory.helmet

        p.inventory.helmet = itemHand

        try {
            p.inventory.setItemInMainHand(helmet)
        } catch (e: NoSuchMethodError) {
            p.setItemInHand(helmet)
        }


        p.sendMessage(LangConfig.hatSendSuccess)

        return false
    }
}