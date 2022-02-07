package github.gilbertokpl.essentialsk.configs;

import github.gilbertokpl.essentialsk.config.annotations.Comment;
import github.gilbertokpl.essentialsk.config.annotations.PrimaryComment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainConfig {

    //general

    @PrimaryComment(primaryAnnotations = "Configurações gerais")
    @Comment(annotations = "Selected Lang to run your plugin, it is necessary to have it in your lang folder, You can add more languages by contributing to the plugin....")
    public static String generalSelectedLang = "pt_BR";
    @Comment(annotations = "Nome do seu servidor.")
    public static String generalServerName = "unamed";
    @Comment(annotations = "Auto Update")
    public static Boolean generalAutoUpdate = true;

    @PrimaryComment(primaryAnnotations = "Configurações database")
    @Comment(annotations = "Selecione a sua conexão, Disponiveis H2 (Local) e Mysql (Nuvem).")
    public static String databaseType = "H2";
    @Comment(annotations = "Se selecionou MYSQL coloque essas informações para conexão.")
    public static String databaseSqlIp = "localhost";
    public static String databaseSqlPort = "3306";
    public static String databaseSqlUsername = "!u0_essentialsk";
    public static String databaseSqlDatabase = "!s0_essentialsk";
    public static String databaseSqlPassword = "!essentialsk123";

    //Commands

    //announceCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos announce")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean announceActivated = true;
    @Comment(annotations = "Tempo em segundos para executar o comando novamente.")
    public static int announceCooldown = 300;

    //backCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos back")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean backActivated = true;
    @Comment(annotations = "Lista de mundos bloqueados de salvarem no back, use o nome do mundo exemplo: world.")
    public static List<String> backDisabledWorlds = Arrays.asList("essentialsk");

    //craftCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos craft")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean craftActivated = true;

    //kitCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos kits")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean kitsActivated = true;
    @Comment(annotations = "Se estiver ativado (true), será enviado um menu ao ‘player’.")
    public static Boolean kitsMenuKit = true;
    @Comment(annotations = "Se estiver ativado (true), será enviado o tempo em pequena escala, exemplo segundos para s.")
    public static Boolean kitsUseShortTime = false;
    @Comment(annotations = "Se estiver ativado (true) dropar items caso o invetário esteja cheio.")
    public static Boolean kitsDropItemsInCatch = false;
    @Comment(annotations = "Se estiver ativado (true), será Equipado a armadura caso tenha no kit.")
    public static Boolean kitsEquipArmorInCatch = true;

    //homeCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos homes")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean homesActivated = true;
    @Comment(annotations = "Quantidade de homes maximas caso não tenha a permissão.")
    public static int homesDefaultLimitHomes = 3;
    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.")
    public static int homesTimeToTeleport = 5;
    @Comment(annotations = "Lista de mundos bloqueados de usar sethome, use o nome do mundo exemplo: world.")
    public static List<String> homesBlockWorlds = Arrays.asList("essentialsk");

    //warpCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos warps")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean warpsActivated = true;
    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.")
    public static int warpsTimeToTeleport = 5;

    //echestCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos echest")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean echestActivated = true;

    //feedCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos feed")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean feedActivated = true;
    @Comment(annotations = "Só vai executar se a sua fome não estiver cheia, em versões antias pode bugar no true.")
    public static Boolean feedNeedEatBelow = false;

    //flyCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos fly")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean flyActivated = true;
    @Comment(annotations = "Lista de mundos bloqueados de usar o fly, use o nome do mundo exemplo: world.")
    public static List<String> flyDisabledWorlds = Arrays.asList("essentialsk");

    //gamemodeCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos gamemodes")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean gamemodeActivated = true;

    //hatCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos hat")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean hatActivated = true;

    //healCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos heal")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean healActivated = true;
    @Comment(annotations = "Só vai executar se a sua vida não estiver cheia, em versões antias pode bugar no true.")
    public static Boolean healNeedHealBelow = false;

    //invseeCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos invsee")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean invseeActivated = true;

    //lightCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos light")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean lightActivated = true;

    //moneyCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos money")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean moneyActivated = true;

    //nickCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos nicks")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean nicksActivated = true;
    @Comment(annotations = "Se estiver ativado (true) players poderão usar o mesmo nick.")
    public static Boolean nicksCanPlayerHaveSameNick = false;
    @Comment(annotations = "Lista de nicks bloqueados.")
    public static List<String> nicksBlockedNicks = Arrays.asList("essentialsk");

    //onlineCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos online")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean onlineActivated = true;
    @Comment(annotations = "Se estiver ativado (true), O comando online contará também os players de vanish.")
    public static Boolean onlineCountRemoveVanish = false;

    //spawnCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos spawn")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean spawnActivated = true;
    @Comment(annotations = "Se estiver ativado o player será mandado para o spawn no login.")
    public static Boolean spawnSendToSpawnOnLogin = true;
    @Comment(annotations = "Se estiver ativado o player será mandado para o spawn quando ele morre.")
    public static Boolean spawnSendToSpawnOnDeath = true;
    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.")
    public static int spawnTimeToTeleport = 5;

    //speedCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos speed")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean speedActivated = true;

    //tpCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tp")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean tpActivated = true;

    //tpaCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tpa")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean tpaActivated = true;
    @Comment(annotations = "Tempo para aceitar o pedido.")
    public static int tpaTimeToAccept = 10;
    @Comment(annotations = "Tempo para teleportar, tem permissão para burlar isso.")
    public static int tpaTimeToTeleport = 5;

    //tphereCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos tphere")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean tphereActivated = true;

    //trashCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos trash")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean trashActivated = true;

    //vanishCommand
    @PrimaryComment(primaryAnnotations = "Configurações dos comandos vanish")
    @Comment(annotations = "Se estiver ativado (true), O comando será ativado.")
    public static Boolean vanishActivated = true;

    //discord
    @PrimaryComment(primaryAnnotations = "Configurações discord")
    @Comment(annotations = "Token do Bot.")
    public static String discordbotToken = "123456";
    @Comment(annotations = "Se estiver ativado (true), O discord conectará com o chat do discord.")
    public static Boolean discordbotConnectDiscordChat = false;
    @Comment(annotations = "Caso o connect esteja ativado terá que colocar o id do chat conectado aqui.")
    public static String discordbotIdDiscordChat = "123456";
    @Comment(annotations = "Comando que ira detectar para enviar ao discord.")
    public static List<String>discordbotCommandChat = Arrays.asList("/g", "/global");
    @Comment(annotations = "Tamanho máximo da mensagem que o discord conseguirá enviar ao server.")
    public static int discordbotMaxMessageLenght = 80;
    @Comment(annotations = "Mandar mensagem ao discord (embed) que o ‘player’ logou.")
    public static Boolean discordbotSendLoginMessage = false;
    @Comment(annotations = "Mandar mensagem ao discord (embed) que o ‘player‘ saiu. Mandar mensagem ao discord (embed) que o ‘player‘ saiu.")
    public static Boolean discordbotSendLeaveMessage = false;
    @Comment(annotations = "Mudar o assunto do canal a cada 10 minutos, com a mensagem setada na sua lang.")
    public static Boolean discordbotSendTopicUpdate = false;

    //systems

    //motd
    @PrimaryComment(primaryAnnotations = "Configurações da MOTD")
    @Comment(annotations = "Se estiver ativado (true), O Motd será ativado.")
    public static Boolean motdEnabled = true;
    @Comment(annotations = "Adicionar lista para ver no motd use (/ n> 1.5.2) para pular linhas;\n" +
            "Lista de motds aletórias, caso queira estático utilize só 1 linha;\n" +
            "Variaveis -> %players_online%.")
    public static List<String> motdListMotd = Arrays.asList(
            "&3Minecraft Server \n&3Está aberto com players %players_online% online!",
            "&9Minecraft Server \n&9Está aberto com players %players_online% online!",
            "&eMinecraft Server \n&eEstá aberto com players %players_online% online!",
            "&fMinecraft Server \n&fEstá aberto com players %players_online% online!"
    );

    //auto messages
    @PrimaryComment(primaryAnnotations = "Configurações das mensagens automáticas")
    @Comment(annotations = "Se estiver ativado (true), as mensagens automáticas serão ativadas.")
    public static Boolean announcementsEnabled = true;
    @Comment(annotations = "Tempo entre as mensagens em minutos para enviar.")
    public static int announcementsTime = 5;

    //messages
    @PrimaryComment(primaryAnnotations = "Configurações das mensagens que podem ser ativadas")
    @Comment(annotations = "Se estiver ativado (true), as mensagens do deathmessages serão ativadas.")
    public static Boolean messagesDeathmessagesMessage = true;
    @Comment(annotations = "Se estiver ativado (true), as mensagens do login será ativada.")
    public static Boolean messagesLoginMessage = true;
    @Comment(annotations = "Se estiver ativado (true), as mensagens do leave será ativada.")
    public static Boolean messagesLeaveMessage = true;

    //antibugs
    @PrimaryComment(primaryAnnotations = "Neste local, contém os recursos antibugs que podem ser ativados ou desativados.")
    @Comment(annotations = "Neste recurso, o jogador é impedido de subir na cama;\n"+
    "Permissão Bypass -> essentialsk.bypass.bed")
    public static Boolean antibugsBlockBed = true;
    @Comment(annotations = "Neste recurso, o jogador é impedido de subir nos veículos;\n"+
            "Permissão Bypass -> essentialsk.bypass.vehicles")
    public static Boolean antibugsBlockClimbingOnVehicles = true;
    @Comment(annotations = "Neste recurso, o jogador é impedido de usar NameTag em mobs;\n"+
            "Permissão Bypass -> essentialsk.bypass.nametag")
    public static Boolean antibugsBlockNametag = false;
    @Comment(annotations = "Neste recurso, os mobs são proibidos de pegar itens do chão.")
    public static Boolean antibugsBlockMobCatch = false;
    @Comment(annotations = "Neste recurso, bloqueia o jogador de ir além da borda com Ender Pearls.")
    public static Boolean antibugsBlockGoingEdgeEnderpearl = true;
    @Comment(annotations = "Neste recurso, bloqueia o jogador de ir para o teto do Nether;\n"+
            "Permissão Bypass -> essentialsk.bypass.netherceiling")
    public static Boolean antibugsBlockPlayerGoToNetherCeiling = true;
    @Comment(annotations = "Neste recurso, os blocos de jogadores se teletransportam em portais;\n"+
            "Permissão Bypass -> essentialsk.bypass.teleportportal")
    public static Boolean antibugsBlockPlayerTeleportPortal = false;
    @Comment(annotations = "Neste recurso, bloqueia todas as entidades criar portais.")
    public static Boolean antibugsBlockCreatePortal = false;
    @Comment(annotations = "Neste recurso, bloquear os player de utilizarem esses comandos;\n"+
            "Permissão Bypass -> essentialsk.bypass.blockedcmd")
    public static List<String> antibugsBlockCmds = Arrays.asList("/pl", "/plugins");

    //containers
    @PrimaryComment(primaryAnnotations = "Neste local, contém as opções do container, lista de valores em (indisponível por enquanto)")
    @Comment(annotations = "Cancelar o uso de shift nos contêineres da lista;\n"+
            "Permissão Bypass -> essentialsk.bypass.shiftcontainer")
    public static Boolean containersBlockShiftEnable = false;
    @Comment(annotations = "Lista para bloquear shift.")
    public static List<String> containersBlockShift = Collections.singletonList("ANVIL");
    @Comment(annotations = "Cancelar abrir os contêineres da lista;\n"+
            "Permissão Bypass -> essentialsk.bypass.opencontainer")
    public static Boolean containersBlockOpenEnable = false;
    @Comment(annotations = "Lista para bloquear de abrir.")
    public static List<String> containersBlockOpen = Collections.singletonList("ANVIL");

    //addons
    @PrimaryComment(primaryAnnotations = "Neste local, possui addons para minecraft.")
    @Comment(annotations = "Neste recurso, a bigorna é infinita.")
    public static Boolean addonsInfinityAnvil = true;
    @Comment(annotations = "Neste recurso, o jogador é teletransportado para o spawn se o jogador cair no void.")
    public static Boolean addonsBlockPlayerGoToVoid = true;
    @Comment(annotations = "Neste recurso, os itens dropados não podem explodir.")
    public static Boolean addonsBlockExplodeItems = true;
    @Comment(annotations = "Neste recurso, o jogador não pode quebrar a plantação quando cair.")
    public static Boolean addonsBlockPlayerBreakPlantationFall = true;
    @Comment(annotations = "Neste recurso, o fogo não pode se propagar.")
    public static Boolean addonsBlockPropagationFire = true;
    @Comment(annotations = "Neste recurso, pode ser colocado & no nome do ‘item’, tem permissão para isso;\n"+
            "Permissão -> essentialsk.color.&0 (Exemplo).")
    public static Boolean addonsColorInAnvil = true;
    @Comment(annotations = "Neste recurso, pode ser colocado & no sinal, tem uma permissão para isso;\n"+
            "Permissão -> essentialsk.color.&0 (Exemplo).")
    public static Boolean addonsColorInSign = true;
    @Comment(annotations = "Neste recurso, pode ser colocado & no bate-papo, tem permissão para isso.Neste recurso, pode ser colocado & no bate-papo, tem permissão para isso.\n"+
            "Permissão -> essentialsk.color.&0 (Exemplo).")
    public static Boolean addonsColorInChat = true;
    @Comment(annotations = "Neste recurso, evita que o jogador perca XP na morte.")
    public static Boolean addonsPlayerPreventLoseXp = true;
    @Comment(annotations = "Neste recurso, evita que começe a chover.")
    public static Boolean addonsDisableRain = false;
}
