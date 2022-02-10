package github.gilbertokpl.essentialsk.config.files;

import github.gilbertokpl.essentialsk.config.annotations.Comment;
import github.gilbertokpl.essentialsk.config.annotations.Comments;
import github.gilbertokpl.essentialsk.config.annotations.PrimaryComment;
import github.gilbertokpl.essentialsk.config.annotations.PrimaryComments;
import github.gilbertokpl.essentialsk.config.lang.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainConfig {

    //general

    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações gerais", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "General settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Selected Lang to run your plugin;\n" +
                            "available: pt_BR, en_US", lang = Lang.PT_BR),
                    @Comment(annotations = "Selected Lang to run your plugin;\n" +
                            "available: pt_BR, en_US", lang = Lang.EN_US)
            }
    )
    public static String generalSelectedLang = "pt_BR";
    @Comments(
            {
                    @Comment(annotations = "Nome do seu servidor.", lang = Lang.PT_BR),
                    @Comment(annotations = "Server name", lang = Lang.EN_US)
            }
    )
    public static String generalServerName = "unamed";
    @Comments(
            {
                    @Comment(annotations = "Auto Update", lang = Lang.PT_BR),
                    @Comment(annotations = "Auto Update", lang = Lang.EN_US)
            }
    )
    public static Boolean generalAutoUpdate = true;

    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações do banco de dados.", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Database config.", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Selecione a sua conexão, Disponiveis H2 (Local) e Mysql (Nuvem).", lang = Lang.PT_BR),
                    @Comment(annotations = "Select you connection, avaliable H2(Local) and Mysql(CloudOnline).", lang = Lang.EN_US)
            }
    )
    public static String databaseType = "H2";
    @Comments(
            {
                    @Comment(annotations = "Se selecionou MYSQL coloque essas informações para conexão.", lang = Lang.PT_BR),
                    @Comment(annotations = "If you selected MYSQL, enter this information for connection.", lang = Lang.EN_US)
            }
    )
    public static String databaseSqlIp = "localhost";
    public static String databaseSqlPort = "3306";
    public static String databaseSqlUsername = "!u0_essentialsk";
    public static String databaseSqlDatabase = "!s0_essentialsk";
    public static String databaseSqlPassword = "!essentialsk123";

    //Commands

    //announceCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos announce", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "announce command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be enabled.", lang = Lang.EN_US)
            }
    )
    public static Boolean announceActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Tempo em segundos para executar o comando novamente.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time in seconds to run the command again.", lang = Lang.EN_US)
            }
    )
    public static int announceCooldown = 300;

    //backCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos back", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Back command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean backActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Lista de mundos bloqueados de salvarem no back, use o nome do mundo exemplo: world.", lang = Lang.PT_BR),
                    @Comment(annotations = "List of worlds blocked from saving in the back, use the world name example: world.", lang = Lang.EN_US)
            }
    )
    public static List<String> backDisabledWorlds = Collections.singletonList("essentialsk");

    //craftCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos craft", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Craft command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean craftActivated = true;

    //kitCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos kits", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Kit command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean kitsActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), será enviado um menu ao ‘player’.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), a menu will be sent to the ‘player’.", lang = Lang.EN_US)
            }
    )
    public static Boolean kitsMenuKit = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), será enviado o tempo em pequena escala, exemplo segundos para s.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), the time will be sent in small scale, ex. seconds for s.", lang = Lang.EN_US)
            }
    )
    public static Boolean kitsUseShortTime = false;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true) dropar items caso o invetário esteja cheio.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true) drop items if the inventory is full.", lang = Lang.EN_US)
            }
    )
    public static Boolean kitsDropItemsInCatch = false;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), será Equipado a armadura caso tenha no kit.", lang = Lang.PT_BR),
                    @Comment(annotations = "If it is on (true), the armor will be Equipped if you have it in the kit.", lang = Lang.EN_US)
            }
    )
    public static Boolean kitsEquipArmorInCatch = true;

    //homeCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos homes", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Home command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean homesActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Quantidade de homes maximas caso não tenha a permissão.", lang = Lang.PT_BR),
                    @Comment(annotations = "Maximum number of men if you do not have the permission.", lang = Lang.EN_US)
            }
    )
    public static int homesDefaultLimitHomes = 3;
    @Comments(
            {
                    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time to teleport, you are allowed to bypass.", lang = Lang.EN_US)
            }
    )
    public static int homesTimeToTeleport = 5;
    @Comments(
            {
                    @Comment(annotations = "Lista de mundos bloqueados de usar sethome, use o nome do mundo exemplo: world.", lang = Lang.PT_BR),
                    @Comment(annotations = "List of worlds blocked from using sethome, use world name example: world.", lang = Lang.EN_US)
            }
    )
    public static List<String> homesBlockWorlds = Collections.singletonList("essentialsk");

    //warpCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos warps", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Warps command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean warpsActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time to teleport, you are allowed to bypass.", lang = Lang.EN_US)
            }
    )
    public static int warpsTimeToTeleport = 5;

    //echestCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos echest", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Enderchest command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean echestActivated = true;

    //feedCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos feed", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Feed command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean feedActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Só vai executar se a sua fome não estiver cheia, em versões antias pode bugar no true.", lang = Lang.PT_BR),
                    @Comment(annotations = "It will only execute if your hunger is not full, in old versions it can bug in true.", lang = Lang.EN_US)
            }
    )
    public static Boolean feedNeedEatBelow = false;

    //flyCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos fly", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Fly command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean flyActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Lista de mundos bloqueados de usar o fly, use o nome do mundo exemplo: world.", lang = Lang.PT_BR),
                    @Comment(annotations = "List of worlds blocked from using fly, use world name example: world.", lang = Lang.EN_US)
            }
    )
    public static List<String> flyDisabledWorlds = Collections.singletonList("essentialsk");

    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos gamemodes", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Gamemodes command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean gamemodeActivated = true;

    //hatCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos hat", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Hat command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean hatActivated = true;

    //healCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos heal", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Heal command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean healActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Só vai executar se a sua vida não estiver cheia, em versões antias pode bugar no true.", lang = Lang.PT_BR),
                    @Comment(annotations = "It will only run if your health is not full, in old versions it can bug in true.", lang = Lang.EN_US)
            }
    )
    public static Boolean healNeedHealBelow = false;

    //invseeCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos invsee", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Invsee command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean invseeActivated = true;

    //lightCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos light", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Light command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )

    public static Boolean lightActivated = true;

    //moneyCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos money", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Money command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean moneyActivated = false;
    @Comments(
            {
                    @Comment(annotations = "Dinheiro que o player ganhará ao entrar a primeira vez.", lang = Lang.PT_BR),
                    @Comment(annotations = "Money the player will earn upon entering the first time.", lang = Lang.EN_US)
            }
    )
    public static Integer moneyDefault = 1000;

    //nickCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos nicks", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Nick command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean nicksActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true) players poderão usar o mesmo nick.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true) players will be able to use the same nick.", lang = Lang.EN_US)
            }
    )
    public static Boolean nicksCanPlayerHaveSameNick = false;
    @Comments(
            {
                    @Comment(annotations = "Lista de nicks bloqueados.", lang = Lang.PT_BR),
                    @Comment(annotations = "List of blocked nicks.", lang = Lang.EN_US)
            }
    )
    public static List<String> nicksBlockedNicks = Collections.singletonList("essentialsk");

    //onlineCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos online", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Online command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean onlineActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando online contará também os players de vanish.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), Online command will also count vanish players.", lang = Lang.EN_US)
            }
    )
    public static Boolean onlineCountRemoveVanish = false;

    //spawnCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos spawn", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Spawn command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean spawnActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado o player será mandado para o spawn no login.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled the player will be sent to spawn on login.", lang = Lang.EN_US)
            }
    )
    public static Boolean spawnSendToSpawnOnLogin = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado o player será mandado para o spawn quando ele morre.", lang = Lang.PT_BR),
                    @Comment(annotations = "If activated the player will be sent to spawn when he dies.", lang = Lang.EN_US)
            }
    )
    public static Boolean spawnSendToSpawnOnDeath = true;
    @Comments(
            {
                    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time to teleport, you are allowed to bypass.", lang = Lang.EN_US)
            }
    )
    public static int spawnTimeToTeleport = 5;

    //speedCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos speed", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Speed ​​command settings", lang = Lang.EN_US)
            }
    )

    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean speedActivated = true;

    //tpCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tp", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "TP command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean tpActivated = true;

    //tpaCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tpa", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "TPA command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean tpaActivated = true;
    @Comments(
            {
                    @Comment(annotations = "Tempo para aceitar o pedido.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time to accept the order.", lang = Lang.EN_US)
            }
    )
    public static int tpaTimeToAccept = 10;
    @Comments(
            {
                    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time to teleport, you are allowed to bypass.", lang = Lang.EN_US)
            }
    )
    public static int tpaTimeToTeleport = 5;

    //tphereCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tphere", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "tphere command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean tphereActivated = true;

    //trashCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos trash", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Trash command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean trashActivated = true;

    //vanishCommand
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações dos comandos vanish", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Vanish command settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The command will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean vanishActivated = true;

    //discord
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações discord", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Discord settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Token do Bot.", lang = Lang.PT_BR),
                    @Comment(annotations = "BotToken", lang = Lang.EN_US)
            }
    )
    public static String discordbotToken = "123456";
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O discord conectará com o chat do discord.", lang = Lang.PT_BR),
                    @Comment(annotations = "If it is on (true), Discord will connect to the Discord chat.", lang = Lang.EN_US)
            }
    )
    public static Boolean discordbotConnectDiscordChat = false;
    @Comments(
            {
                    @Comment(annotations = "Caso o connect esteja ativado terá que colocar o id do chat conectado aqui.", lang = Lang.PT_BR),
                    @Comment(annotations = "If connect is activated, you will have to put the id of the connected chat here.", lang = Lang.EN_US)
            }
    )
    public static String discordbotIdDiscordChat = "123456";
    @Comments(
            {
                    @Comment(annotations = "Comando que ira detectar para enviar ao Discord.", lang = Lang.PT_BR),
                    @Comment(annotations = "Command that will detect to send to Discord.", lang = Lang.EN_US)
            }
    )
    public static List<String> discordbotCommandChat = Arrays.asList("/g", "/global");
    @Comments(
            {
                    @Comment(annotations = "Tamanho máximo da mensagem que o Discord conseguirá enviar ao server.", lang = Lang.PT_BR),
                    @Comment(annotations = "Maximum message size that Discord will be able to send to the server.", lang = Lang.EN_US)
            }
    )
    public static int discordbotMaxMessageLenght = 80;
    @Comments(
            {
                    @Comment(annotations = "Mandar mensagem ao discord (embed) que o ‘player’ logou.", lang = Lang.PT_BR),
                    @Comment(annotations = "Message discord (embed) that the player has logged in.", lang = Lang.EN_US)
            }
    )
    public static Boolean discordbotSendLoginMessage = false;
    @Comments(
            {
                    @Comment(annotations = "Mandar mensagem ao discord (embed) que o ‘player‘ saiu. Mandar mensagem ao discord (embed) que o ‘player‘ saiu.", lang = Lang.PT_BR),
                    @Comment(annotations = "Message discord (embed) that the 'player' has left. Message discord (embed) that the 'player' has left.", lang = Lang.EN_US)
            }
    )
    public static Boolean discordbotSendLeaveMessage = false;
    @Comments(
            {
                    @Comment(annotations = "Mudar o assunto do canal a cada 10 minutos, com a mensagem setada na sua lang.", lang = Lang.PT_BR),
                    @Comment(annotations = "Change the channel subject every 10 minutes, with the message set in your lang.", lang = Lang.EN_US)
            }
    )
    public static Boolean discordbotSendTopicUpdate = false;

//systems

    //motd
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações da MOTD", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "MOTD Settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), O Motd será ativado.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The MOTD will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean motdEnabled = true;
    @Comments(
            {
                    @Comment(annotations = "Adicionar lista para ver no motd use (/ n> 1.5.2) para pular linhas;\n" +
                            "Lista de motds aletórias, caso queira estático utilize só 1 linha;\n" +
                            "Variaveis -> %players_online%.", lang = Lang.PT_BR),
                    @Comment(annotations = "Add list to see in motd use (/n>1.5.2) to skip lines;\n" +
                            "List of random motds, if you want static use only 1 line;\n" +
                            "Variables -> %players_online%.", lang = Lang.EN_US)
            }
    )
    public static List<String> motdListMotd = Arrays.asList(
            "&3Minecraft Server \n&3Está aberto com players %players_online% online!",
            "&9Minecraft Server \n&9Está aberto com players %players_online% online!",
            "&eMinecraft Server \n&eEstá aberto com players %players_online% online!",
            "&fMinecraft Server \n&fEstá aberto com players %players_online% online!"
    );

    //auto messages
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações das mensagens automáticas", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Automatic message settings", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), as mensagens automáticas serão ativadas.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The auto-messages will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean announcementsEnabled = true;
    @Comments(
            {
                    @Comment(annotations = "Tempo entre as mensagens em minutos para enviar.", lang = Lang.PT_BR),
                    @Comment(annotations = "Time between messages in minutes to send.", lang = Lang.EN_US)
            }
    )
    public static int announcementsTime = 5;

    //messages
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Configurações das mensagens que podem ser ativadas", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "Message settings that can be enabled", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), as mensagens do deathmessages serão ativadas.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The deathmessages will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean messagesDeathmessagesMessage = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), as mensagens do login será ativada.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The login-messages will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean messagesLoginMessage = true;
    @Comments(
            {
                    @Comment(annotations = "Se estiver ativado (true), as mensagens do leave será ativada.", lang = Lang.PT_BR),
                    @Comment(annotations = "If enabled (true), The leave-messages will be actived.", lang = Lang.EN_US)
            }
    )
    public static Boolean messagesLeaveMessage = true;

    //antibugs
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Neste local, contém os recursos antibugs que podem ser ativados ou desativados.", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "This location contains the anti-bug features that can be turned on or off.", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o jogador é impedido de subir na cama;\n" +
                            "Permissão Bypass -> essentialsk.bypass.bed", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the player is prevented from getting on the bed;\n" +
                            "Permission Bypass -> essentialsk.bypass.bed", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockBed = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o jogador é impedido de subir nos veículos;\n" +
                            "Permissão Bypass -> essentialsk.bypass.vehicles", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the player is prevented from boarding vehicles;\n" +
                            "Permission Bypass -> essentialsk.bypass.vehicles", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockClimbingOnVehicles = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o jogador é impedido de usar NameTag em mobs;\n" +
                            "Permissão Bypass -> essentialsk.bypass.nametag", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the player is prevented from using NameTag on mobs;\n" +
                            "Permission Bypass -> essentialsk.bypass.nametag", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockNametag = false;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, os mobs são proibidos de pegar itens do chão.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, mobs are prohibited from picking up items from the ground.", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockMobCatch = false;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, bloqueia o jogador de ir além da borda com Ender Pearls.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, blocks the player from going over the edge with Ender Pearls.", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockGoingEdgeEnderpearl = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, bloqueia o jogador de ir para o teto do Nether;\n" +
                            "Permissão Bypass -> essentialsk.bypass.netherceiling", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, blocks the player from going to the Nether ceiling;\n" +
                            "Permission Bypass -> essentialsk.bypass.netherceiling", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockPlayerGoToNetherCeiling = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, os blocos de jogadores se teletransportam em portais;\n" +
                            "Permissão Bypass -> essentialsk.bypass.teleportportal", lang = Lang.PT_BR),
                    @Comment(annotations = "TEST" + "Permission Bypass ->", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockPlayerTeleportPortal = false;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, bloqueia todas as entidades criar portais.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, blocks all entities from creating portals.", lang = Lang.EN_US)
            }
    )
    public static Boolean antibugsBlockCreatePortal = false;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, bloquear os player de utilizarem esses comandos;\n" +
                            "Permissão Bypass -> essentialsk.bypass.blockedcmd", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, block players from using these commands;\n" +
                            "Permission Bypass -> essentialsk.bypass.blockedcmd", lang = Lang.EN_US)
            }
    )
    public static List<String> antibugsBlockCmds = Arrays.asList("/pl", "/plugins");

    //containers
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Neste local, contém as opções do container, lista de valores em (indisponível por enquanto)", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "This location contains container options, list of values ​​in (unavailable for now)", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Cancelar o uso de shift nos contêineres da lista;\n" +
                            "Permissão Bypass -> essentialsk.bypass.shiftcontainer", lang = Lang.PT_BR),
                    @Comment(annotations = "Override using shift in list containers;\n" +
                            "Permission Bypass -> essentialsk.bypass.shiftcontainer", lang = Lang.EN_US)
            }
    )
    public static Boolean containersBlockShiftEnable = false;
    @Comments(
            {
                    @Comment(annotations = "Lista para bloquear shift.", lang = Lang.PT_BR),
                    @Comment(annotations = "Lock-shift List.", lang = Lang.EN_US)
            }
    )
    public static List<String> containersBlockShift = Collections.singletonList("ANVIL");
    @Comments(
            {
                    @Comment(annotations = "Cancelar abrir os contêineres da lista;\n" +
                            "Permissão Bypass -> essentialsk.bypass.opencontainer", lang = Lang.PT_BR),
                    @Comment(annotations = "Cancel open list containers;\n" +
                            "Permission Bypass -> essentialsk.bypass.opencontainer", lang = Lang.EN_US)
            }
    )
    public static Boolean containersBlockOpenEnable = false;
    @Comments(
            {
                    @Comment(annotations = "Lista para bloquear de abrir.", lang = Lang.PT_BR),
                    @Comment(annotations = "List to lock from opening.", lang = Lang.EN_US)
            }
    )
    public static List<String> containersBlockOpen = Collections.singletonList("ANVIL");

    //addons
    @PrimaryComments(
            {
                    @PrimaryComment(primaryAnnotations = "Neste local, possui addons para minecraft.", lang = Lang.PT_BR),
                    @PrimaryComment(primaryAnnotations = "This place has addons for minecraft.", lang = Lang.EN_US)
            }
    )
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, a bigorna é infinita.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the anvil is infinite.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsInfinityAnvil = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o jogador é teletransportado para o spawn se o jogador cair no void.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the player is teleported to the spawn if the player falls into the void.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsBlockPlayerGoToVoid = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, os itens dropados não podem explodir.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, dropped items cannot explode.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsBlockExplodeItems = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o jogador não pode quebrar a plantação quando cair.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, the player cannot break the crop when it falls.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsBlockPlayerBreakPlantationFall = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, o fogo não pode se propagar.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, fire cannot propagate.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsBlockPropagationFire = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, pode ser colocado & no nome do ‘item’, tem permissão para isso;\n" +
                            "Permissão -> essentialsk.color.&0 (Exemplo).", lang = Lang.PT_BR),
                    @Comment(annotations = "In this resource, you can put & in the 'item' name, have permission to do so;\n" +
                            "Permission -> essentialsk.color.&0 (Ex).", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsColorInAnvil = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, pode ser colocado & no sinal, tem uma permissão para isso;\n" +
                            "Permissão -> essentialsk.color.&0 (Exemplo).", lang = Lang.PT_BR),
                    @Comment(annotations = "In this resource, you can put & in the sign, have a permission for that;\n" +
                            "Permission -> essentialsk.color.&0 (Ex).", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsColorInSign = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, pode ser colocado & no bate-papo, tem permissão para isso.Neste recurso, pode ser colocado & no bate-papo, tem permissão para isso.\n" +
                            "Permissão -> essentialsk.color.&0 (Exemplo).", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, can be placed & in chat, have permission to do so. In this feature, can be placed & in chat, have permission to do so.\n" +
                            "Permission -> essentialsk.color.&0 (Ex).", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsColorInChat = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, evita que o jogador perca XP na morte.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, it prevents the player from losing XP on death.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsPlayerPreventLoseXp = true;
    @Comments(
            {
                    @Comment(annotations = "Neste recurso, evita que começe a chover.", lang = Lang.PT_BR),
                    @Comment(annotations = "In this feature, it prevents it from starting to rain.", lang = Lang.EN_US)
            }
    )
    public static Boolean addonsDisableRain = false;
}
