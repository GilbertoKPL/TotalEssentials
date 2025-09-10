# TotalEssentials

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/5113aa86f46b45caa40bdfd8508bcd3e)](https://www.codacy.com/gh/GilbertoKPL/TotalEssentials/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=GilbertoKPL/TotalEssentials&amp;utm_campaign=Badge_Grade)

* A core system for minecraft server that supports most versions (1.5.2 - 1.21.8) and Folia, using only database as a storage
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

| Command                      | Description                                                         | Permission                             |
|------------------------------|---------------------------------------------------------------------|----------------------------------------|
| /total, /system, /essentials | Main command of the plugin.                                         | totalessentials.commands.essentials    |
| /createkit, /criarkit        | Create a kit using an interface / GUI.                              | totalessentials.commands.createkit     |
| /delkit                      | Delete a kit by name.                                               | totalessentials.commands.delkit        |
| /editkit, /editarkit         | Edit an existing kit using an interface / GUI.                      | totalessentials.commands.editkit       |
| /kit, /kits                  | View all kits through the interface / GUI and claim them.           | totalessentials.commands.kit           |
| /givekit, /darkit            | Give a kit to a player.                                             | totalessentials.commands.givekit       |
| /nick, /nome                 | Change the player’s nickname.                                       | totalessentials.commands.nick          |
| /sethome, /setarhome         | Set a home at the current location.                                 | totalessentials.commands.sethome       |
| /delhome, /deletarhome       | Delete a home.                                                      | totalessentials.commands.delhome       |
| /home, /h                    | Teleport to a home.                                                 | totalessentials.commands.home          |
| /warp                        | Teleport to a warp location.                                        | totalessentials.commands.warp          |
| /setwarp, /setarwarp         | Set a warp at the current location.                                 | totalessentials.commands.setwarp       |
| /delwarp, /deletarwarp       | Delete a warp by name.                                              | totalessentials.commands.delwarp       |
| /tp                          | Teleport to another player or location.                             | totalessentials.commands.tp            |
| /tpa, /tpaccept, /tpadeny    | Request to teleport to another player, and accept or deny requests. | totalessentials.commands.tpa           |
| /echest, /ec                 | Open the Ender Chest.                                               | totalessentials.commands.ec            |
| /gamemode, /gm               | Change the player’s gamemode.                                       | totalessentials.commands.gamemode      |
| /vanish, /v                  | Toggle vanish mode.                                                 | totalessentials.commands.vanish        |
| /feed, /comer                | Refill hunger.                                                      | totalessentials.commands.feed          |
| /heal, /curar                | Restore health.                                                     | totalessentials.commands.heal          |
| /light, /luz                 | Toggle personal light source.                                       | totalessentials.commands.light         |
| /back                        | Return to the last location.                                        | totalessentials.commands.back          |
| /spawn                       | Teleport to spawn.                                                  | totalessentials.commands.spawn         |
| /setspawn                    | Set the spawn location.                                             | totalessentials.commands.setspawn      |
| /announce, /anunciar         | Broadcast a message to all players.                                 | totalessentials.commands.announce      |
| /craft                       | Open a crafting table.                                              | totalessentials.commands.craft         |
| /trash, /lixo                | Open the trash GUI.                                                 | totalessentials.commands.trash         |
| /speed                       | Set walking/flying speed.                                           | totalessentials.commands.speed         |
| /hat, /chapeu                | Wear the item in hand as a hat.                                     | totalessentials.commands.hat           |
| /fly                         | Enable or disable flight.                                           | totalessentials.commands.fly           |
| /invsee                      | View another player’s inventory.                                    | totalessentials.commands.invsee        |
| /login, /logar               | Log into your account.                                              | totalessentials.commands.login         |
| /register, /registrar        | Register a new account.                                             | totalessentials.commands.register      |
| /changepass                  | Change your account password.                                       | totalessentials.commands.changepass    |
| /vip                         | Access VIP features.                                                | totalessentials.commands.vip           |
| /shop, /loja                 | Access player shops.                                                | totalessentials.commands.shop          |
| /clearentities               | Remove dropped items/entities from the server.                      | totalessentials.commands.clearentities |
| /day, /dia                   | Set time to day in the world (or all worlds if console).            | totalessentials.commands.day           |
| /night, /noite               | Set time to night in the world (or all worlds if console).          | totalessentials.commands.night         |
| /thor, /raio, /lightning     | Summon lightning at a player or at the block you are looking at.    | totalessentials.commands.thor          |


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

| Permission                                         | Function                                              |
|----------------------------------------------------|-------------------------------------------------------|
| totalessentials.commands.sethome.(number of homes) | Set the maximum number of homes for a player          |
| totalessentials.commands.kit.(kit name)            | Allow the player to claim the specified kit           |
| totalessentials.commands.warp.(warp name)          | Allow the player to teleport to the specified warp    |
| totalessentials.commands.nick.other                | Allow the player to change another player’s nickname  |
| totalessentials.commands.ec.other                  | Allow the player to open another player’s Ender Chest |
| totalessentials.commands.gamemode.other            | Allow the player to change another player’s gamemode  |
| totalessentials.commands.heal.other                | Allow the player to heal other players                |
| totalessentials.commands.feed.other                | Allow the player to feed other players                |
| totalessentials.commands.vanish.other              | Allow the player to toggle vanish for other players   |
| totalessentials.commands.light.other               | Allow the player to toggle light for other players    |
| totalessentials.commands.home.other                | Allow the player to teleport to other players’ homes  |
| totalessentials.commands.delhome.other             | Allow the player to delete other players’ homes       |
| totalessentials.commands.sethome.other             | Allow the player to set homes for other players       |
| totalessentials.commands.spawn.other               | Allow the player to teleport other players to spawn   |
| totalessentials.commands.speed.other               | Allow the player to change another player’s speed     |
| totalessentials.commands.login.other               | Allow the player to log in another player             |
| totalessentials.commands.register.other            | Allow the player to register another player           |
| totalessentials.commands.invsee.admin              | Allow the player to view any player’s inventory       |
| totalessentials.bypass.teleport                    | Bypass teleport delay                                 |
| totalessentials.bypass.homelimit                   | Bypass the home limit restriction                     |
| totalessentials.bypass.kitcatch                    | Bypass kit cooldown                                   |
| totalessentials.bypass.vanish                      | Bypass vanish detection                               |
| totalessentials.bypass.bed                         | Bypass blocked beds                                   |
| totalessentials.bypass.vehicles                    | Bypass blocked vehicles                               |
| totalessentials.bypass.nametag                     | Bypass blocked name tags                              |
| totalessentials.bypass.netherceiling               | Bypass nether ceiling restriction                     |
| totalessentials.bypass.shiftcontainer              | Bypass shift-click restrictions in containers         |
| totalessentials.bypass.opencontainer               | Bypass blocked container opening                      |
| totalessentials.bypass.waitcommand                 | Bypass command cooldown                               |
| totalessentials.bypass.backblockedworlds           | Bypass blocked worlds for /back                       |
| totalessentials.bypass.homeblockedworlds           | Bypass blocked worlds for /home                       |
| totalessentials.bypass.nickblockednicks            | Bypass nickname restrictions                          |


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

- [ X ] Add support of version Checker for a Windows.
- [ X ] Add Support to H2 DataBase.
- [ X ] Create CacheSQL to more powerful speed.
- [ X ] Create reload system.
- [ X ] Create auto config system with comments.
- [ X ] Create auto lang system with comments.

# Discord

* _gilberto_
