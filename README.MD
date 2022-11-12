# TotalEssentials

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5113aa86f46b45caa40bdfd8508bcd3e)](https://www.codacy.com/gh/GilbertoKPL/TotalEssentials/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=GilbertoKPL/TotalEssentials&amp;utm_campaign=Badge_Grade)

* A core system for minecraft server that supports most versions (1.5.2 - 1.19.2), using only database as a storage
  medium and various built-in commands / systems, which can be disabled at any time.

# Bstats

<p align="center">
    <a href="https://bstats.org/plugin/bukkit/EssentialsK" title="EssentialsK on bStats">
        <img src="https://bstats.org/signatures/bukkit/totalessentials.svg"  alt=""/>
    </a>
</p>

## Languages

- pt_BR
- en_US

## Commands

| Command                      | Description                                                                                     | Permission                           |
|------------------------------|-------------------------------------------------------------------------------------------------|--------------------------------------|
| /total, /system, /essentials | Main command os plugin.                                                                         | totalessentials.commands.essentialsk |
| /createkit and /criarkit     | Create a kit with an interface / GUI.                                                           | totalessentials.commands.createkit   |
| /delkit                      | Delete kit with name.                                                                           | totalessentials.commands.delkit      |
| /editkit and /editarkit      | Editkit a kit with an interface / GUI.                                                          | totalessentials.commands.editkit     |
| /kit and /kits               | Create a kit with an interface / GUI see all kits through the interface / GUI and also get them | totalessentials.commands.kit         |
| /givekit and /darkit         | Give kit to player.                                                                             | totalessentials.commands.givekit     |
| /nick and /nome              | Give kit to player.                                                                             | totalessentials.commands.nick        |
| /sethome and /setarhome      | Set home of player with name.                                                                   | totalessentials.commands.sethome     |
| /delhome and /deletarhome    | Del home of player.                                                                             | totalessentials.commands.delhome     |
| /home and /h                 | Go to home of player.                                                                           | totalessentials.commands.home        |
| /warp                        | go to warp location.                                                                            | totalessentials.commands.warp        |
| /setwarp and /setarwarp      | Set determined warp in location.                                                                | totalessentials.commands.setwarp     |
| /delwarp and /deletarwarp    | Delete warp with determined name.                                                               | totalessentials.commands.delwarp     |
| /tp                          | Tp to other player or location.                                                                 | totalessentials.commands.tp          |
| /tpa, /tpaccept and /tpadeny | Tp to other player or location.                                                                 | totalessentials.commands.tpa         |
| /echest and /ec              | Open enderchest.                                                                                | totalessentials.commands.ec          |
| /gamemode and /gm            | Switch gamemode.                                                                                | totalessentials.commands.gamemode    |
| /vanish and /v               | Switch vanish.                                                                                  | totalessentials.commands.vanish      |
| /feed and /comer             | Allow player to set max food.                                                                   | totalessentials.commands.feed        |
| /heal and /h                 | Allow player to set max health.                                                                 | totalessentials.commands.heal        |
| /light and /luz              | Switch light.                                                                                   | totalessentials.commands.light       |
| /back                        | Back to old location.                                                                           | totalessentials.commands.back        |
| /spawn                       | Go to spawn lo location.                                                                        | totalessentials.commands.spawn       |
| /setspawn                    | Set Location of spawn.                                                                          | totalessentials.commands.setspawn    |
| /announce and /anunciar      | Announce to all player.                                                                         | totalessentials.commands.announce    |
| /craft                       | Open craft to player.                                                                           | totalessentials.commands.craft       |
| /trash and /lixo             | Open trash to player.                                                                           | totalessentials.commands.trash       |
| /speed                       | set speed of player.                                                                            | totalessentials.commands.speed       |
| /hat                         | set hat of player.                                                                              | totalessentials.commands.hat         |
| /fly                         | set fly of player.                                                                              | totalessentials.commands.fly         |
| /invsee                      | see inventory of other player.                                                                  | totalessentials.commands.invsee      |
| /login                       | log into your account.                                                                          | totalessentials.commands.login       |
| /register                    | register account.                                                                               | totalessentials.commands.register    |

## Permission Color

To use in chat, commands, signs etc.

| Color          | Permission               |
|----------------|--------------------------|
| Black          | totalessentials.color.&0 |
| DarkBlue       | totalessentials.color.&1 |
| DarkGreen      | totalessentials.color.&2 |
| DarkTurquoise  | totalessentials.color.&3 |
| DarkRed        | totalessentials.color.&4 |
| Purple         | totalessentials.color.&5 |
| DarkYellow     | totalessentials.color.&6 |
| LightGray      | totalessentials.color.&7 |
| DarkGray       | totalessentials.color.&8 |
| LightBlue      | totalessentials.color.&9 |
| LightGreen     | totalessentials.color.&a |
| LightTurquoise | totalessentials.color.&b |
| LightRed       | totalessentials.color.&c |
| Magenta        | totalessentials.color.&d |
| LightYellow    | totalessentials.color.&e |
| White          | totalessentials.color.&f |
| Scrambled      | totalessentials.color.&k |
| Reset          | totalessentials.color.&r |
| Trace          | totalessentials.color.&l |
| Italic         | totalessentials.color.&n |
| RGB            | totalessentials.color.*  |

## Other permissions

| Permission                                         | Function                                            |
|----------------------------------------------------|-----------------------------------------------------|
| totalessentials.commands.sethome.(number of homes) | Set limit of homes to player                        |
| totalessentials.commands.kit.(kit name)            | Allow the player to pick up the given kit           |
| totalessentials.commands.warp.(warp name)          | Allow the player go to determined warp              |
| totalessentials.commands.nick.other                | Allow the player rename other players               |
| totalessentials.commands.ec.other                  | Allow the player open others enderChest             |
| totalessentials.commands.gamemode.other            | Allow the player switch gamemode for other player   |
| totalessentials.commands.heal.other                | Allow the player use heal for other players         |
| totalessentials.commands.feed.other                | Allow the player use feed for other players         |
| totalessentials.commands.vanish.other              | Allow the player use vanish to other player         |
| totalessentials.commands.light.other               | Allow the player use light to other player          |
| totalessentials.commands.home.other                | Allow the player go to other players homes          |
| totalessentials.commands.delhome.other             | Allow the player delhome for other players          |
| totalessentials.commands.sethome.other             | Allow the player sethome for other players          |
| totalessentials.commands.spawn.other               | Allow the player telepot to spawn for other players |
| totalessentials.commands.speed.other               | Allow the player set speed of other player          |
| totalessentials.commands.login.other               | Allow the player login other player                 |
| totalessentials.commands.register.other            | Allow the player register other player              |
| totalessentials.commands.invsee.admin              | Allow the player see inventory of other player      |
| totalessentials.bypass.teleport                    | Bypass time to teleport                             |
| totalessentials.bypass.homelimit                   | Move itens of player.                               |
| totalessentials.bypass.kitcatch                    | Bypass kit time limit                               |
| totalessentials.bypass.vanish                      | Bypass the vanish                                   |
| totalessentials.bypass.bed                         | Bypass the block bed                                |
| totalessentials.bypass.vehicles                    | Bypass the block vehicles                           |
| totalessentials.bypass.nametag                     | Bypass the block nametag                            |
| totalessentials.bypass.netherceiling               | Bypass the block nether ceiling                     |
| totalessentials.bypass.shiftcontainer              | Bypass the block shift in container                 |
| totalessentials.bypass.opencontainer               | Bypass the block open container                     |
| totalessentials.bypass.waitcommand                 | Bypass wait time to use command again               |
| totalessentials.bypass.backblockedworlds           | Bypass blocked worlds of back                       |
| totalessentials.bypass.homeblockedworlds           | Bypass blocked worlds of home                       |
| totalessentials.bypass.nickblockednicks            | Bypass blocked nicks of nick                        |

## Resources ( Internal )

* Own Lib using new features such as:
    - MariaDB latest version;
    - H2 V2 latest version;
    - HikariCP;
    - Kotlin;
    - CacheSQL (created by me).
  
* Reload system.
    - system based on reload all configuration, besides being possible to change your lang.
* Lang's and Config system and check;
    - This system is based on the difference of the configuration file between versions, if it has a new path, it adds
      it if it doesn't have it, in addition to the comments of the configurations that are updated.
* Cache System.
    - Caching system built by me, made for the highest possible performance.

## TODO List

- [ ] Add support of version Checker for a Windows.

# Discord

* Gilberto#8778
