# GenesysCardinal

* A core system for minecraft server that supports most versions (1.5.2 - 1.18.0), using only database as a storage
  medium and various built-in commands / systems, which can be disabled at any time.

## Languages

- pt_BR
- en_US

## Commands

| Command                      | Description                                                                                     | Permission                    |
|------------------------------|-------------------------------------------------------------------------------------------------|-------------------------------|
| /cardinal, /c                | Main command os plugin.                                                                         | cardinal.commands.essentialsk |
| /createkit and /criarkit     | Create a kit with an interface / GUI.                                                           | cardinal.commands.createkit   |
| /delkit                      | Delete kit with name.                                                                           | cardinal.commands.delkit      |
| /editkit and /editarkit      | Editkit a kit with an interface / GUI.                                                          | cardinal.commands.editkit     |
| /kit and /kits               | Create a kit with an interface / GUI see all kits through the interface / GUI and also get them | cardinal.commands.kit         |
| /givekit and /darkit         | Give kit to player.                                                                             | cardinal.commands.givekit     |
| /nick and /nome              | Give kit to player.                                                                             | cardinal.commands.nick        |
| /sethome and /setarhome      | Set home of player with name.                                                                   | cardinal.commands.sethome     |
| /delhome and /deletarhome    | Del home of player.                                                                             | cardinal.commands.delhome     |
| /home and /h                 | Go to home of player.                                                                           | cardinal.commands.home        |
| /warp                        | go to warp location.                                                                            | cardinal.commands.warp        |
| /setwarp and /setarwarp      | Set determined warp in location.                                                                | cardinal.commands.setwarp     |
| /delwarp and /deletarwarp    | Delete warp with determined name.                                                               | cardinal.commands.delwarp     |
| /tp                          | Tp to other player or location.                                                                 | cardinal.commands.tp          |
| /tpa, /tpaccept and /tpadeny | Tp to other player or location.                                                                 | cardinal.commands.tpa         |
| /echest and /ec              | Open enderchest.                                                                                | cardinal.commands.ec          |
| /gamemode and /gm            | Switch gamemode.                                                                                | cardinal.commands.gamemode    |
| /vanish and /v               | Switch vanish.                                                                                  | cardinal.commands.vanish      |
| /feed and /comer             | Allow player to set max food.                                                                   | cardinal.commands.feed        |
| /heal and /h                 | Allow player to set max health.                                                                 | cardinal.commands.heal        |
| /light and /luz              | Switch light.                                                                                   | cardinal.commands.light       |
| /back                        | Back to old location.                                                                           | cardinal.commands.back        |
| /spawn                       | Go to spawn lo location.                                                                        | cardinal.commands.spawn       |
| /setspawn                    | Set Location of spawn.                                                                          | cardinal.commands.setspawn    |
| /announce and /anunciar      | Announce to all player.                                                                         | cardinal.commands.announce    |
| /craft                       | Open craft to player.                                                                           | cardinal.commands.craft       |
| /trash and /lixo             | Open trash to player.                                                                           | cardinal.commands.trash       |
| /speed                       | set speed of player.                                                                            | cardinal.commands.speed       |

## Permission Color

To use in chat, commands, signs etc.

| Color          | Permission           |
|----------------|----------------------|
| Black          | cardinal.color.&0 |
| DarkBlue       | cardinal.color.&1 |
| DarkGreen      | cardinal.color.&2 |
| DarkTurquoise  | cardinal.color.&3 |
| DarkRed        | cardinal.color.&4 |
| Purple         | cardinal.color.&5 |
| DarkYellow     | cardinal.color.&6 |
| LightGray      | cardinal.color.&7 |
| DarkGray       | cardinal.color.&8 |
| LightBlue      | cardinal.color.&9 |
| LightGreen     | cardinal.color.&a |
| LightTurquoise | cardinal.color.&b |
| LightRed       | cardinal.color.&c |
| Magenta        | cardinal.color.&d |
| LightYellow    | cardinal.color.&e |
| White          | cardinal.color.&f |
| Scrambled      | cardinal.color.&k |
| Reset          | cardinal.color.&r |
| Trace          | cardinal.color.&l |
| Italic         | cardinal.color.&n |

## Other permissions

| Permission                                     | Function                                            |
|------------------------------------------------|-----------------------------------------------------|
| cardinal.commands.sethome.(number of homes) | Set limit of homes to player                        |
| cardinal.commands.kit.(kit name)            | Allow the player to pick up the given kit           |
| cardinal.commands.warp.(warp name)          | Allow the player go to determined warp              |
| cardinal.commands.nick.other                | Allow the player rename other players               |
| cardinal.commands.ec.other                  | Allow the player open others enderChest             |
| cardinal.commands.gamemode.other            | Allow the player switch gamemode for other player   |
| cardinal.commands.heal.other                | Allow the player use heal for other players         |
| cardinal.commands.feed.other                | Allow the player use feed for other players         |
| cardinal.commands.vanish.other              | Allow the player use vanish to other player         |
| cardinal.commands.light.other               | Allow the player use light to other player          |
| cardinal.commands.home.other                | Allow the player go to other players homes          |
| cardinal.commands.delhome.other             | Allow the player delhome for other players          |
| cardinal.commands.sethome.other             | Allow the player sethome for other players          |
| cardinal.commands.spawn.other               | Allow the player telepot to spawn for other players |
| cardinal.commands.speed.other               | Allow the player set player of other player         |
| cardinal.bypass.teleport                    | Bypass time to teleport                             |
| cardinal.bypass.homelimit                   | Bypass home limit                                   |
| cardinal.bypass.kitcatch                    | Bypass kit time limit                               |
| cardinal.bypass.vanish                      | Bypass the vanish                                   |
| cardinal.bypass.bed                         | Bypass the block bed                                |
| cardinal.bypass.vehicles                    | Bypass the block vehicles                           |
| cardinal.bypass.nametag                     | Bypass the block nametag                            |
| cardinal.bypass.netherceiling               | Bypass the block nether ceiling                     |
| cardinal.bypass.shiftcontainer              | Bypass the block shift in container                 |
| cardinal.bypass.opencontainer               | Bypass the block open container                     |
| cardinal.bypass.waitcommand                 | Bypass wait time to use command again               |
| cardinal.bypass.backblockedworlds           | Bypass blocked worlds of back                       |
| cardinal.bypass.homeblockedworlds           | Bypass blocked worlds of home                       |
| cardinal.bypass.nickblockednicks            | Bypass blocked nicks of nick                        |

## Resources ( Internal )

* UUID system:
    - If Server is in online mode, automatically use UUID.

* Own Lib using new features such as:
    - MariaDB latest version;
    - H2 V2 latest version;
    - HikariCP;
    - Kotlin;
    - ExposedSQL.
* Reload system.
    - system based on reload all configuration, besides being possible to change your lang.
* Lang's and Config system and check;
    - This system is based on the difference of the configuration file between versions, if it has a new path, it adds
      it if it doesn't have it, in addition to the comments of the configurations that are updated.
* Version checker.
    - For now, it auto-updates only on linux systems, as windows have manage problems.

## TODO List

- [ ] Add suport of version Checker for a Windows.
- [ ] Add suport UUID to offline server.

# Discord

* Gilberto_KPL#5620
