package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.config.files.LangConfig
import github.gilbertokpl.essentialsk.config.files.MainConfig
import github.gilbertokpl.essentialsk.manager.CommandCreator
import github.gilbertokpl.essentialsk.manager.CommandData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTp : CommandCreator {
    override val commandData: CommandData
        get() = CommandData(
            active = MainConfig.tpActivated,
            consoleCanUse = true,
            commandName = "tp",
            timeCoolDown = null,
            permission = "essentialsk.commands.tp",
            minimumSize = 1,
            maximumSize = 4,
            commandUsage = listOf(
                "P_/tp <world> <x> <y> <z>",
                "P_/tp <x> <y> <z> <world>",
                "P_/tp <x> <y> <z>",
                "P_/tp <playerName>",
                "/tp <playerName> <OtherPlayerName>",
                "/tp <playerName> <world> <x> <y> <z>",
                "/tp <playerName> <x> <y> <z> <world>",
                "/tp <playerName> <x> <y> <z>",
            )
        )

    override fun funCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        //only player name
        if (args.size == 1 && s is Player) {
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            s.teleport(p.location)
            s.sendMessage(LangConfig.tpTeleportedSuccess)
            return false
        }

        //player to other player
        if (args.size == 2) {
            val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            val p1 = EssentialsK.instance.server.getPlayer(args[1]) ?: run {
                s.sendMessage(LangConfig.generalPlayerNotOnline)
                return false
            }

            p.teleport(p1.location)

            p.sendMessage(LangConfig.tpTeleportedOtherSuccess)

            s.sendMessage(LangConfig.tpTeleportedSuccess)
            return false
        }

        //only x y and z
        if (args.size == 3 && s is Player) {
            val loc = try {
                Location(s.world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
            } catch (ex: Exception) {
                return true
            }
            s.teleport(loc)
            s.sendMessage(LangConfig.tpTeleportedSuccess)
            return false
        }

        //only x y and z world
        if (args.size == 4) {

            val p = Bukkit.getPlayer(args[0].lowercase())

            if (p != null) {

                val loc = try {
                    Location(p.world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
                } catch (ex: Exception) {
                    return true
                }
                p.teleport(loc)
                s.sendMessage(LangConfig.tpTeleportedSuccess)
                p.sendMessage(LangConfig.tpTeleportedOtherSuccess)

                return false
            }

            if (s is Player) {
                val world = try {
                    EssentialsK.instance.server.getWorld(args[0]) ?: EssentialsK.instance.server.getWorld(args[3])
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

                s.teleport(loc)
                s.sendMessage(LangConfig.tpTeleportedSuccess)
                return false
            }
        }

        //only player x y and z world
        if (args.size == 5) {
            val p = Bukkit.getPlayer(args[0].lowercase()) ?: return true

            val world = try {
                EssentialsK.instance.server.getWorld(args[1]) ?: EssentialsK.instance.server.getWorld(args[4])
                ?: return true
            } catch (ex: Exception) {
                return true
            }
            val loc = try {
                Location(world, args[1].toDouble(), args[2].toDouble(), args[3].toDouble())
            } catch (ex: Exception) {
                try {
                    Location(world, args[2].toDouble(), args[3].toDouble(), args[4].toDouble())
                } catch (ex: Exception) {
                    return true
                }
            }

            p.teleport(loc)
            s.sendMessage(LangConfig.tpTeleportedSuccess)
            p.sendMessage(LangConfig.tpTeleportedOtherSuccess)

        }

        return true
    }
}
