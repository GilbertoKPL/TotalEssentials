package github.genesyspl.cardinal.commands

import github.genesyspl.cardinal.manager.ICommand
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTp : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "tp"
    override val timeCoolDown: Long? = null
    override val permission: String = "cardinal.commands.tp"
    override val minimumSize = 1
    override val maximumSize = 4
    override val commandUsage = listOf(
        "/tp <playerName>",
        "/tp <playerName> <OtherPlayerName>",
        "/tp <world> <x> <y> <z>",
        "/tp <x> <y> <z> <world>",
        "/tp <x> <y> <z>",
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //only player name
        if (args.size == 1) {
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            (s as Player).teleport(p.location)
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpTeleportedSuccess)
            return false
        }

        //player to other player
        if (args.size == 2) {
            val p = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            val p1 = github.genesyspl.cardinal.Cardinal.instance.server.getPlayer(args[1]) ?: run {
                s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().generalPlayerNotOnline)
                return false
            }

            p.teleport(p1.location)

            p.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpTeleportedOtherSuccess)

            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpTeleportedSuccess)
            return false
        }

        //only x y and z
        if (args.size == 3) {
            val loc = try {
                Location((s as Player).world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
            } catch (ex: Exception) {
                return true
            }
            s.teleport(loc)
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpTeleportedSuccess)
            return false
        }

        //only x y and z world
        if (args.size == 4) {
            val world = try {
                github.genesyspl.cardinal.Cardinal.instance.server.getWorld(args[0])
                    ?: github.genesyspl.cardinal.Cardinal.instance.server.getWorld(args[3])
                    ?: return true
            } catch (ex: Exception) {
                return true
            }
            val loc = try {
                Location(world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
            } catch (ex: Exception) {
                try {
                    Location(world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                } catch (ex: Exception) {
                    return true
                }
            }

            (s as Player).teleport(loc)
            s.sendMessage(github.genesyspl.cardinal.configs.GeneralLang.getInstance().tpTeleportedSuccess)
            return false
        }
        return false
    }
}