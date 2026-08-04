# TotalEssentials

## Velocity login sharing

The generated `TotalEssentials` jar can be installed in the Velocity proxy and
in every backend server. The proxy keeps the authenticated session while the
player moves between servers, so login and registration are only requested in
the configured lobby.

1. Put the same jar in Velocity's `plugins` folder and in the lobby/MCPC
   `plugins` folders.
2. Start Velocity once and edit
   `plugins/totalessentials/velocity-auth.properties` if the lobby server is not
   named `lobby` in `velocity.toml`.
3. List multiple authentication lobbies separated by commas, for example:
   `authentication-servers=lobby,lobby2`.
4. Keep backend ports private. Use Velocity `modern` forwarding only when every
   backend is Minecraft 1.13 or newer. Networks containing 1.5.2/1.8 backends
   must use `legacy` or `bungeeguard` forwarding and enable BungeeCord forwarding
   in each backend. Players must not be able to connect directly to backend
   ports.

Until login succeeds, Velocity redirects the initial connection to an
authentication lobby and denies transfers to MCPC. The session is discarded
when the player disconnects from the proxy.

When a backend starts again, it requests the current session from Velocity as
the player joins. Therefore a player who already authenticated in the lobby is
also logged in automatically after being reconnected to the restarted server.
Every trusted backend can refresh that proxy session, and `/totalconnect`
reconfirms it before changing servers so the destination never asks for login
again when the player is already authenticated.

If a backend goes offline or drops the connection, the Velocity/Bungee
companion redirects the player to the configured lobby and retries the failed
server until it becomes available. An authenticated player remains logged in
while waiting in the lobby. Configure this in `velocity-auth.properties` on
Velocity or `bungee-auth.properties` on BungeeCord:

```properties
reconnect-enabled=true
reconnect-fallback-server=lobby
reconnect-delay-seconds=3
reconnect-interval-seconds=5
reconnect-timeout-seconds=300
reconnect-waiting-message=&eO servidor caiu. Tentaremos reconectar automaticamente.
reconnect-success-message=&aO servidor voltou. Reconectado com sucesso.
```

The proxy retries for `reconnect-timeout-seconds` (five minutes by default).
Only time spent online and waiting in the lobby consumes this limit. If the
player disconnects, every backend goes offline, or the proxy restarts, the
destination and remaining time are kept locally in
`plugins/totalessentials/reconnect-state.properties`. The countdown resumes
when the player returns; offline time is never added to it. The same behavior
applies to a normal proxy disconnect: the last playable server is remembered
and the player is sent back to it after passing through the configured
authentication lobby.
Moving to the lobby manually clears that remembered destination, so disconnecting
from the lobby and joining the network again leaves the player in the lobby.

The same velocity-auth.properties file controls the network MOTD. Multiple
random entries can be separated by |; it supports color codes, a second line,
and the players_online / players_max placeholders documented in the file.

## Native network chat

TotalEssentials now contains its own channel chat and no longer needs
LegendChat. Vault is the only chat-format integration: `{vault_prefix}` and
`{vault_suffix}` are read from the permission/chat provider registered in
Vault. The Bukkit-side chat path uses APIs available from 1.5.2 through 1.20,
without direct linkage to modern-only chat events. The native formatter also supports `{channel}`, `{channel_prefix}`,
`{player}`, `{player_name}`, `{player_displayname}`, `{magnata}`,
`{chat_color}` and `{message}`.

When PlaceholderAPI is installed, channel formats also resolve any player
placeholder, including `%totalessentials_money%`, `%totalessentials_money_raw%`
and placeholders supplied by other expansions. The player placeholders
`%totalessentials_nickname%` and `%totalessentials_vanished%` are also available.

Global ranking placeholders can be used without a player context, including in
Citizens NPC names and holograms:

- `%totalessentials_money_top_1_name%`
- `%totalessentials_money_top_1_value%`
- `%totalessentials_money_top_1_raw%`
- `%totalessentials_playtime_top_1_name%`
- `%totalessentials_playtime_top_1_time%`
- `%totalessentials_playtime_top_1_raw%`

Replace `1` with a position from `1` through `10`. The aliases `moneytop` and
`playtimetop` (without the underscore before `top`) are also accepted.
`vanished` returns `yes` or `no`. Placeholder text typed by the player is not
expanded; only the configured channel format is processed.

1. Install the same generated jar on every Bukkit/Spigot/Paper/MCPC backend.
2. For network channels, also put that jar in either Velocity or BungeeCord's
   `plugins` folder. Do not install both proxy variants on the same proxy.
3. Start the backends once and edit `plugins/TotalEssentials/ChatConfig.yml`.
4. Set `network: true` on channels that must reach every server. Local channels
   can use `distance`, while every channel supports aliases, cooldown, filter,
   autojoin, format and separate access/speaking permissions.

The defaults provide `/g` and `/global` for the network channel, and `/l` and
`/local` for the 230-block local channel. Running an alias without text selects
that channel; adding text sends a one-off message. `/chat` lists channels,
`/chat join <channel>` and `/chat leave <channel>` manage listening, and
`/chat reload` reloads the file. Reload requires
`totalessentials.chat.reload`; cooldown bypass uses
`totalessentials.chat.bypass.cooldown`.

Private messages use `/tell <player> <message>` (aliases `/msg`, `/w` and
`/sussurrar`) and `/r <message>`. They work locally and across Velocity or
BungeeCord. Settings and aliases are stored under `tell.*` in `ChatConfig.yml`;
all user-facing texts and private-message formats are stored under `chat.*` and
`tell.*` in the selected language file.

The proxy validates that the player identity in a network-chat payload belongs
to the connection that delivered it. The backend sends the message locally and the
proxy sends it only to other servers, preventing duplicates on the origin.
Backends select the plugin-message identifier automatically. The namespaced
`totaless:auth` channel is only 13 characters, so it works on both modern Paper
and MCPC/Bukkit versions with the legacy 16-character limit. The legacy
`TotalEssentials` identifier remains supported during updates.

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
| /totalconnect \<servidor\>   | Connect to any server registered in Velocity.                       | totalessentials.commands.totalconnect  |
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
| /chat, /g, /l                | Select, send to, join or leave native chat channels.                 | Configured per channel                  |
| /tell, /msg, /w, /r          | Send and reply to local or cross-server private messages.            | totalessentials.commands.tell           |
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


## Safe automatic updates

The Bukkit, BungeeCord and Velocity entrypoints check the latest GitHub release
when they start and then every 30 minutes. Only a release newer than the running
version is accepted. The downloaded artifact must contain all three plugin
entrypoints, both YAML descriptors and a `plugin.yml` version matching the
release tag.

The updater never reloads the running classes and never shuts down a server or
proxy. Bukkit places the validated jar in `plugins/update`. BungeeCord and
Velocity replace the on-disk jar while keeping the current version in memory;
on Windows a detached helper waits for the normal process exit before replacing
the locked jar. In every case, the new code becomes active on the next normal
restart.

Backend settings are generated in `MainConfig.yml`:

```yaml
general:
  auto-update: true
  auto-update-interval-minutes: 30
```

Proxy settings are generated in `bungee-auth.properties` and
`velocity-auth.properties`:

```properties
auto-update-enabled=true
auto-update-interval-minutes=30
```

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
