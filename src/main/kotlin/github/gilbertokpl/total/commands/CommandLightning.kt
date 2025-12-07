package github.gilbertokpl.total.commands

import github.gilbertokpl.core.command.CommandManager
import github.gilbertokpl.core.command.pattern.CommandPattern
import github.gilbertokpl.core.command.type.CommandTargetType
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandLightning : CommandManager("lightning") {

    companion object {
        private const val MAX_TARGET_DISTANCE = 50

        // Materiais transparentes para getTargetBlock (compatível com versões antigas)
        private val TRANSPARENT_MATERIALS = setOf(
            Material.AIR,
            Material.WATER
        )
    }

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("raio", "thor"),
            active = MainConfig.lightningActivated,
            target = CommandTargetType.ALL,
            countdown = 0,
            permission = "totalessentials.commands.lightning",
            minimumSize = 0,
            maximumSize = 1,
            usage = listOf(
                "/lightning <Player>",
                "P_/lightning"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return strikeLightningAtTargetBlock(sender)
        }

        return strikeLightningAtPlayer(sender, args[0])
    }

    @Suppress("DEPRECATION")
    private fun strikeLightningAtTargetBlock(sender: CommandSender): Boolean {
        if (sender !is Player) return true

        // Usa getTargetBlock para compatibilidade com versões antigas
        val targetBlock = try {
            sender.getTargetBlock(TRANSPARENT_MATERIALS, MAX_TARGET_DISTANCE)
        } catch (e: Exception) {
            // Fallback para versões muito antigas
            sender.getTargetBlock(null as Set<Material>?, MAX_TARGET_DISTANCE)
        }

        val lightningLocation = targetBlock.location.add(0.5, 1.0, 0.5)
        sender.world.strikeLightning(lightningLocation)
        sender.sendMessage(LangConfig.lightningMessage)

        Bukkit.getBukkitVersion()

        return false
    }

    private fun strikeLightningAtPlayer(sender: CommandSender, targetName: String): Boolean {
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return true
        }

        target.world.strikeLightning(target.location)
        sender.sendMessage(
            LangConfig.lightningOtherMessage.replace("%player%", target.name)
        )

        return false
    }
}