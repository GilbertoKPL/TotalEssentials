# EssentialsK

* A core system for minecraft server that supports most versions (1.5.2 - 1.18.0), using only database as a storage
  medium and various built-in commands / systems, which can be disabled at any time.

## Languages

- pt_BR
- en_US

## Commands

| Command                            | Description                                                                                     | Permission                       |
|------------------------------------|-------------------------------------------------------------------------------------------------|----------------------------------|
| /essentialsk, /system, /essentials | Main command os plugin.                                                                         | essentialsk.commands.essentialsk |
| /createkit and /criarkit           | Create a kit with an interface / GUI.                                                           | essentialsk.commands.createkit   |
| /delkit                            | Delete kit with name.                                                                           | essentialsk.commands.delkit      |
| /editkit and /editarkit            | Editkit a kit with an interface / GUI.                                                          | essentialsk.commands.editkit     |
| /kit and /kits                     | Create a kit with an interface / GUI see all kits through the interface / GUI and also get them | essentialsk.commands.kit         |
| /givekit and /darkit               | Give kit to player.                                                                             | essentialsk.commands.givekit     |
| /nick and /nome                    | Give kit to player.                                                                             | essentialsk.commands.nick        |
| /sethome and /setarhome            | Set home of player with name.                                                                   | essentialsk.commands.sethome     |
| /delhome and /deletarhome          | Del home of player.                                                                             | essentialsk.commands.delhome     |
| /home and /h                       | Go to home of player.                                                                           | essentialsk.commands.home        |
| /setwarp and /setarwarp            | Set determined warp in location.                                                                | essentialsk.commands.setwarp     |
| /delwarp and /deletarwarp          | Delete warp with determined name.                                                               | essentialsk.commands.delwarp     |
| /tp                                | Tp to other player or location.                                                                 | essentialsk.commands.tp          |
| /tpa, /tpaccept and /tpadeny       | Tp to other player or location.                                                                 | essentialsk.commands.tpa         |
| /echest and /ec                    | Open enderchest.                                                                                | essentialsk.commands.ec          |
| /gamemode and /gm                  | Switch gamemode.                                                                                | essentialsk.commands.gamemode    |
| /vanish and /v                     | Switch vanish.                                                                                  | essentialsk.commands.vanish      |
| /feed and /comer                   | Allow player to set max food.                                                                   | essentialsk.commands.feed        |
| /heal and /h                       | Allow player to set max health.                                                                 | essentialsk.commands.heal        |
| /light and /luz                    | Switch light.                                                                                   | essentialsk.commands.light       |
| /back                              | Back to old location.                                                                           | essentialsk.commands.back        |

## Permission Color

To use in chat, commands, signs etc.

| Permission     | Color                |
|----------------|----------------------|
| Black          | essentialsk.color.&0 |
| DarkBlue       | essentialsk.color.&1 |
| DarkGreen      | essentialsk.color.&2 |
| DarkTurquoise  | essentialsk.color.&3 |
| DarkRed        | essentialsk.color.&4 |
| Purple         | essentialsk.color.&5 |
| DarkYellow     | essentialsk.color.&6 |
| LightGray      | essentialsk.color.&7 |
| DarkGray       | essentialsk.color.&8 |
| LightBlue      | essentialsk.color.&9 |
| LightGreen     | essentialsk.color.&a |
| LightTurquoise | essentialsk.color.&b |
| LightRed       | essentialsk.color.&c |
| Magenta        | essentialsk.color.&d |
| LightYellow    | essentialsk.color.&e |
| White          | essentialsk.color.&f |

## Other permissions

| Permission                                     | Function                                          |
|------------------------------------------------|---------------------------------------------------|
| essentialsk.commands.sethome.(number of homes) | Set limit of homes to player                      |
| essentialsk.commands.kit.(kit name)            | Allow the player to pick up the given kit         |
| essentialsk.commands.warp.(warp name)          | Allow the player go to determined warp            |
| essentialsk.commands.nick.other                | Allow the player rename other players             |
| essentialsk.commands.ec.other                  | Allow the player open others enderChest           |
| essentialsk.commands.gamemode.other            | Allow the player switch gamemode for other player |
| essentialsk.commands.heal.other                | Allow the player use heal for other players       |
| essentialsk.commands.feed.other                | Allow the player use feed for other players       |
| essentialsk.commands.vanish.other              | Allow the player use vanish to other player       |
| essentialsk.commands.light.other               | Allow the player use light to other player        |
| essentialsk.commands.home.other                | Allow the player go to other players homes        |
| essentialsk.commands.delhome.other             | Allow the player delhome for other players        |
| essentialsk.commands.sethome.other             | Allow the player sethome for other players        |
| essentialsk.bypass.teleport                    | Bypass time to teleport                           |
| essentialsk.bypass.homelimit                   | Bypass home limit                                 |
| essentialsk.bypass.kitcatch                    | Bypass kit time limit                             |
| essentialsk.bypass.vanish                      | Bypass the vanish                                 |

## Resources ( Internal )

* UUID system:
    - If Server is in online mode, automatically use UUID.

* Own Lib using new features such as:
    - MariaDB latest version;
    - HikariCP;
    - Kotlin;
    - ExposedSQL.
* Reload system.
    - system based on reload all configuration, besides being possible to change your lang.
* Lang's and Config system and check;
    - This system is based on the difference of the configuration file between versions, if it has a new path, it adds
      it if it doesn't have it, in addition to the comments of the configurations that are updated.
* Version checker.
    - For now it auto-updates only on linux systems, as windows have manage problems.

## TODO List

- [ ] Add Weight to kits, to organize the GUI.
- [ ] Add Support to H2 2.0.
- [ ] Add suport of version Checker for a Windows.
- [ ] Add suport UUID to offline server.

# Discord

* Gilberto_KPL#5620
