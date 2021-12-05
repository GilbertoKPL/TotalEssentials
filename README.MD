# EssentialsK

* A core system for minecraft server that supports most versions (1.5.2 - 1.18.0), using only database as a storage medium and various built-in commands / systems, which can be disabled at any time.

##Languages

- pt_BR
- en_US

## Commands - Kit System
|Command         |Description                      |Permission                    |
|----------------|-------------------------------|-----------------------------|
|/createkit and /criarkit |Create a kit with an interface / GUI|essentialsk.commands.createkit    |
|/delkit |Delete kit with name|essentialsk.commands.delkit    |
|/editkit and /editarkit |Editkit a kit with an interface / GUI|essentialsk.commands.editkit    |
|/kit and /kits |Create a kit with an interface / GUI see all kits through the interface / GUI and also get them|essentialsk.commands.kit    |
|/givekit and /darkit |Give kit to player.|essentialsk.commands.givekit    |
|/nick and /nome |Give kit to player.|essentialsk.commands.nick    |

## Permission Color
To use in chat, commands, signs etc.

|Permission         |Color                       |
|----------------|------------------------------- |
| Black | essentialsk.color.&0 |
| DarkBlue | essentialsk.color.&1 |
| DarkGreen | essentialsk.color.&2 |
| DarkTurquoise | essentialsk.color.&3 |
| DarkRed | essentialsk.color.&4 |
| Purple | essentialsk.color.&5 |
| DarkYellow | essentialsk.color.&6 |
| LightGray | essentialsk.color.&7 |
| DarkGray | essentialsk.color.&8 |
| LightBlue | essentialsk.color.&9 |
| LightGreen | essentialsk.color.&a |
| LightTurquoise | essentialsk.color.&b |
| LightRed | essentialsk.color.&c |
| Magenta | essentialsk.color.&d |
| LightYellow | essentialsk.color.&e |
| White| essentialsk.color.&f |

## Other permissions
|Permission         | Function |
|----------------|----------------|
|essentialsk.kit.(kit name) | allow the player to pick up the given kit |
|essentialsk.commands.nick.other | allow the player rename other players |




# Resources ( Internal )

* Own Lib using new features such as:
    - MariaDB latest version;
    - H2 database latest version;
    - HikariCP;
    - Kotlin;
    - ExposedSql.
* Lang's system and check;
* Config system and check;
* Check system and lib;
* Version checker.

# Discord

* Gilberto_KPL#5620
