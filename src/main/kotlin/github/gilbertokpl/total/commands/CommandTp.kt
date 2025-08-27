package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.FoliaUtil.teleportSafe
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTp : github.gilbertokpl.core.external.command.CommandCreator("tp") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("teleport"),
            active = MainConfig.tpActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.tp",
            minimumSize = 1,
            maximumSize = 5,
            usage = listOf(
                "P_/tp <playerName>",
                "/tp <playerName> <OtherPlayerName>",
                "P_/tp <x> <y> <z>",
                "P_/tp <x> <y> <z> <world>",
                "/tp <playerName> <x> <y> <z>",
                "/tp <playerName> <x> <y> <z> <world>"
            )
        )
    }

    override fun funCommand(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true

        return when {
            args.size == 1 && sender is Player -> teleportToPlayer(sender, args[0])
            args.size == 2 -> teleportPlayerToPlayer(sender, args[0], args[1])
            args.size in 3..4 && sender is Player -> teleportByCoordinates(sender, args)
            args.size in 4..5 -> teleportPlayerByCoordinates(sender, args)
            else -> true
        }
    }

    private fun teleportToPlayer(player: Player, targetName: String): Boolean {
        val target = Bukkit.getPlayer(targetName) ?: run {
            player.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }
        player.teleportSafe(target.location)
        player.sendMessage(LangConfig.tpTeleportedSuccess)
        return false
    }

    private fun teleportPlayerToPlayer(sender: CommandSender, fromName: String, toName: String): Boolean {
        val from = Bukkit.getPlayer(fromName) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }
        val to = Bukkit.getPlayer(toName) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }
        from.teleportSafe(to.location)
        from.sendMessage(LangConfig.tpTeleportedOtherSuccess)
        sender.sendMessage(LangConfig.tpTeleportedSuccess)
        return false
    }

    private fun teleportByCoordinates(player: Player, args: Array<out String>): Boolean {
        val loc = try {
            if (args.size == 3) Location(player.world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
            else {
                val world = Bukkit.getWorld(args[3]) ?: return true
                Location(world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
            }
        } catch (e: Exception) {
            return true
        }

        player.teleportSafe(loc)
        player.sendMessage(LangConfig.tpTeleportedSuccess)
        return false
    }

    private fun teleportPlayerByCoordinates(sender: CommandSender, args: Array<out String>): Boolean {
        val target = Bukkit.getPlayer(args[0]) ?: run {
            sender.sendMessage(LangConfig.generalPlayerNotOnline)
            return false
        }

        val loc = try {
            when (args.size) {
                4 -> Location(target.world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                5 -> {
                    val world = Bukkit.getWorld(args[4]) ?: return true
                    Location(world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                }
                else -> return true
            }
        } catch (e: Exception) {
            return true
        }

        target.teleportSafe(loc)
        sender.sendMessage(LangConfig.tpTeleportedSuccess)
        target.sendMessage(LangConfig.tpTeleportedOtherSuccess)
        return false
    }
}
