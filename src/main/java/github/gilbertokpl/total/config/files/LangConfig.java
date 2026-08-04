package github.gilbertokpl.total.config.files;


import github.gilbertokpl.core.config.annotations.Value;
import github.gilbertokpl.core.config.annotations.MultiValue;
import github.gilbertokpl.core.config.defaults.DefaultLang;
import github.gilbertokpl.core.config.types.LangTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LangConfig implements DefaultLang {

    @MultiValue(
            {
                    @Value(value = "&a[Server]&r ", lang = LangTypes.PT_BR),
                    @Value(value = "&a[Server]&r ", lang = LangTypes.EN_US)
            }
    )
    public static String generalServerPrefix;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse comando é somente para player!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis command is for player only!", lang = LangTypes.EN_US)
            }
    )
    public static String generalOnlyPlayerCommand;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse comando é somente para console!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis command is for console only!", lang = LangTypes.EN_US)
            }
    )
    public static String generalOnlyConsoleCommand;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para executar esse comando.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou do not have permissions to run this command.", lang = LangTypes.EN_US)
            }
    )
    public static String generalNotPerm;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para fazer isso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou don't have permissions.", lang = LangTypes.EN_US)
            }
    )
    public static String generalNotPermAction;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse player não está online.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis player is not online.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPlayerNotOnline;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse player não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis player does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPlayerNotExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse player já existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis player exist.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPlayerExist;
    @MultiValue(
            {
                    @Value(value = "&9[TotalEssentials Ajuda]", lang = LangTypes.PT_BR),
                    @Value(value = "&9[TotalEssentials Help]", lang = LangTypes.EN_US)
            }
    )
    public static String generalCommandsUsage;
    @MultiValue(
            {
                    @Value(value = "'&9> &e%command%'", lang = LangTypes.PT_BR),
                    @Value(value = "'&9> &e%command%'", lang = LangTypes.EN_US)
            }
    )
    public static String generalCommandsUsageList;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cCaracteres proibidos", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cForbidden simbols", lang = LangTypes.EN_US)
            }
    )
    public static String generalSpecialCaracteresDisabled;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cFalta %time% para executar novamente esse comando.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThere are %time% left to run this command again.", lang = LangTypes.EN_US)
            }
    )
    public static String generalCooldownMoreTime;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cAdicione o Vault para o funcionamento do plugin!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cAdd Vault for plugin working!", lang = LangTypes.EN_US)
            }
    )
    public static String generalVaultNotExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou are already expecting a teleport.", lang = LangTypes.EN_US)
            }
    )
    public static String generalInTeleport;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê será teleportado para -> %local% em %time% segundos!.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String generalTimeToTeleport;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, spawn desativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cWorld %world% no existing, spawn disabled.", lang = LangTypes.EN_US)
            }
    )
    public static String generalWorldNotExistSpawn;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, warp %warp% desativada.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cWorld %world% no existing, warp %warp% disabled.", lang = LangTypes.EN_US)
            }
    )
    public static String generalWorldNotExistWarp;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eConfig reninciada com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eReloaded config successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String generalConfigReload;
    @MultiValue(
            {
                    @Value(value = "&aIP: &f%ip%|&aOS: &f%os%, &aversão &f%os_version%|&aNome CPU: &f%cpu_name%|&aNúcleos: &f%cores% &anúcleos|&aNúcleos Liberados: &f%cores_server% &anúcleos|&aUso do CPU : &f%cpu_usage% %|&aClock CPU: &f%cpu_clock_min% / %cpu_clock_max% mhz|&aMemória: &f%used_mem% / %max_mem% gb|&aMemória Liberada: &f%used_server_mem% / %max_server_mem% gb|&aHD Usado: &f%used_hd%/%max_hd% gb|&aGPU: &f%gpu%", lang = LangTypes.PT_BR),
                    @Value(value = "&aIP: &f%ip%|&aOS: &f%os%, &aversion &f%os_version%|&aCPU Name: &f%cpu_name%|&aCores: &f%cores% &anúcleos|&aReleased Cores: &f%cores_server% &anúcleos|&aCPU Usage : &f%cpu_usage% %|&aClock CPU: &f%cpu_clock_min% / %cpu_clock_max% mhz|&aMemory: &f%used_mem% / %max_mem% gb|&aFree memory: &f%used_server_mem% / %max_server_mem% gb|&aHD: &f%used_hd%/%max_hd% gb|&aGPU: &f%gpu%", lang = LangTypes.EN_US)
            }
    )
    public static List<String> generalHostConfig;
    @MultiValue(
            {
                    @Value(value = "%prefix%&ePegando informações da host... Aguarde!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&ecollecting information from host... Please wait!", lang = LangTypes.EN_US)
            }
    )
    public static String generalHostWait;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cPlugin não encontrado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cPlugin not found.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPluginNotFound;
    @MultiValue(
            {
                    @Value(value = "%prefix%&ePlugin descarregado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&ePlugin unloaded successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPluginUnload;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cPlugin descarregado com problemas.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cPlugin unloaded with problems.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPluginUnloadProblems;
    @MultiValue(
            {
                    @Value(value = "%prefix%&ePlugin carregado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&ePlugin loaded successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPluginLoad;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cPlugin carregado com problemas.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cPlugin loaded with problems.", lang = LangTypes.EN_US)
            }
    )
    public static String generalPluginLoadProblems;
    @MultiValue(
            {
                    @Value(value = "Salvando informações.", lang = LangTypes.PT_BR),
                    @Value(value = "Saving information.", lang = LangTypes.EN_US)
            }
    )
    public static String generalSaveDataMessage;
    @MultiValue(
            {
                    @Value(value = "Salvo com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "Saved successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String generalSaveDataSuccess;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aEnviado código ao discord do dono, apenas pegar e dar /e reset [código].", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aSent the code to the owner's Discord, just take it and run /e reset [code].", lang = LangTypes.EN_US)
            }
    )
    public static String generalResetMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cNão tem nenhum dono setado na config.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cNo owner is set in the config.", lang = LangTypes.EN_US)
            }
    )
    public static String generalResetMessageNotSet;

    @MultiValue(
            {
                    @Value(value = "Utilize o comando '/e reset %value%' para resetar as informações do servidor.", lang = LangTypes.PT_BR),
                    @Value(value = "Use the command '/e reset %value%' to reset the server information.", lang = LangTypes.EN_US)
            }
    )
    public static String generalResetDiscordMessage;

    //title

    @MultiValue(
            {
                    @Value(value = "BEM VINDO!", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String titleJoinTitle;

    @MultiValue(
            {
                    @Value(value = "Ao Servidor TotalCraft", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String titleJoinSubtitle;

    //sound
    @MultiValue(
            {
                    @Value(value = "note.pling", lang = LangTypes.PT_BR),
                    @Value(value = "note.pling", lang = LangTypes.EN_US)
            }
    )
    public static String soundJoin;

    @MultiValue(
            {
                    @Value(value = "note.pling", lang = LangTypes.PT_BR),
                    @Value(value = "note.pling", lang = LangTypes.EN_US)
            }
    )
    public static String soundClearentities;


    //time
    @MultiValue(
            {
                    @Value(value = "segundos", lang = LangTypes.PT_BR),
                    @Value(value = "seconds", lang = LangTypes.EN_US)
            }
    )
    public static String timeSeconds;
    @MultiValue(
            {
                    @Value(value = "segundo", lang = LangTypes.PT_BR),
                    @Value(value = "second", lang = LangTypes.EN_US)
            }
    )
    public static String timeSecond;
    @MultiValue(
            {
                    @Value(value = "s", lang = LangTypes.PT_BR),
                    @Value(value = "s", lang = LangTypes.EN_US)
            }
    )
    public static String timeSecondShort;
    @MultiValue(
            {
                    @Value(value = "minutos", lang = LangTypes.PT_BR),
                    @Value(value = "minutes", lang = LangTypes.EN_US)
            }
    )
    public static String timeMinutes;
    @MultiValue(
            {
                    @Value(value = "minuto", lang = LangTypes.PT_BR),
                    @Value(value = "minute", lang = LangTypes.EN_US)
            }
    )
    public static String timeMinute;
    @MultiValue(
            {
                    @Value(value = "m", lang = LangTypes.PT_BR),
                    @Value(value = "m", lang = LangTypes.EN_US)
            }
    )
    public static String timeMinuteShort;
    @MultiValue(
            {
                    @Value(value = "horas", lang = LangTypes.PT_BR),
                    @Value(value = "hours", lang = LangTypes.EN_US)
            }
    )
    public static String timeHours;
    @MultiValue(
            {
                    @Value(value = "hora", lang = LangTypes.PT_BR),
                    @Value(value = "hour", lang = LangTypes.EN_US)
            }
    )
    public static String timeHour;
    @MultiValue(
            {
                    @Value(value = "h", lang = LangTypes.PT_BR),
                    @Value(value = "h", lang = LangTypes.EN_US)
            }
    )
    public static String timeHourShort;
    @MultiValue(
            {
                    @Value(value = "dias", lang = LangTypes.PT_BR),
                    @Value(value = "days", lang = LangTypes.EN_US)
            }
    )
    public static String timeDays;
    @MultiValue(
            {
                    @Value(value = "dia", lang = LangTypes.PT_BR),
                    @Value(value = "day", lang = LangTypes.EN_US)
            }
    )
    public static String timeDay;
    @MultiValue(
            {
                    @Value(value = "d", lang = LangTypes.PT_BR),
                    @Value(value = "d", lang = LangTypes.EN_US)
            }
    )
    public static String timeDayShort;

    //auth

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê logou com VPN, por favor desative!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou logged in using a VPN, please disable it!", lang = LangTypes.EN_US)
            }
    )
    public static String authVpn;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê logou com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully logged in!", lang = LangTypes.EN_US)
            }
    )
    public static String authLoggedIn;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê foi registrado com sucesso", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have been successfully registered!", lang = LangTypes.EN_US)
            }
    )
    public static String authRegisterSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cSenha incorreta!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cIncorrect password!", lang = LangTypes.EN_US)
            }
    )
    public static String authIncorrectPassword;
    @MultiValue(
            {
                    @Value(value = "&cVocê errou mais de %quant% vezes!", lang = LangTypes.PT_BR),
                    @Value(value = "&cYou got it wrong more than %quant% times!", lang = LangTypes.EN_US)
            }
    )
    public static String authKickMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê escreveu duas senhas diferentes!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou wrote two different passwords!", lang = LangTypes.EN_US)
            }
    )
    public static String authDifferentPasswords;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê já registrou o máximo de contas permitidas que é %max%!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have already registered the maximum allowed accounts which is %max%!", lang = LangTypes.EN_US)
            }
    )
    public static String authMaxRegister;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê ultrapassou o máximo de caracteres possiveis da senha que é de 16", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have exceeded the maximum number of characters possible for the password, which is 16!", lang = LangTypes.EN_US)
            }
    )
    public static String authPasswordMaxLength;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cNo minimo a senha tem que ter 5 caracteres", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cAt least the password must be 5 characters long.", lang = LangTypes.EN_US)
            }
    )
    public static String authPasswordMinLength;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aLogado pelo sistema de auto login!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aLogged in by automatic login system", lang = LangTypes.EN_US)
            }
    )
    public static String authAutoLogin;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aLogue com o comando /logar <senha>", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aLog in with the command /login <password>", lang = LangTypes.EN_US)
            }
    )
    public static String authLoginMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aRegistre com o comando /registrar <senha> <senha>!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aRegister with the command /register <password> <password>!", lang = LangTypes.EN_US)
            }
    )
    public static String authRegisterMessage;

    @MultiValue(
            {
                    @Value(value = "&cVocê foi kickado por passar do tempo limite de login ou registro!", lang = LangTypes.PT_BR),
                    @Value(value = "&cYou've been kicked for exceeding the login or registration timeout!", lang = LangTypes.EN_US)
            }
    )
    public static String authKickMessageTime;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê logou o %player% com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully logged in to %player!", lang = LangTypes.EN_US)
            }
    )
    public static String authOtherLogin;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê registrou o %player% com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully registered %player%!", lang = LangTypes.EN_US)
            }
    )
    public static String authOtherRegister;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê alterou a senha com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully changed your password.!", lang = LangTypes.EN_US)
            }
    )
    public static String authChangePass;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê alterou a senha do %player% com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully changed the %player% password!", lang = LangTypes.EN_US)
            }
    )
    public static String authOtherChangePass;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cO %player% já está logado!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&c%player% is already logged in!!", lang = LangTypes.EN_US)
            }
    )
    public static String authOtherAlreadyLogged;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aÚltimo ip logado : %ip%!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aLast logged IP: %ip%!", lang = LangTypes.EN_US)

            }
    )
    public static String authIpMessage;

    //LIMIT

    @MultiValue(
            {
                    @Value(value = "&cEste grupo não existe no servidor", lang = LangTypes.PT_BR),
                    @Value(value = "&cThis group does not exist on the server", lang = LangTypes.EN_US)
            }
    )
    public static String limitGroupDoNotExist;

    //PLAYTIME


    @MultiValue(
            {
                    @Value(value = "&eTempo Jogado:|&e%time%", lang = LangTypes.PT_BR),
                    @Value(value = "&ePlaytime:|&e%time%", lang = LangTypes.EN_US)
            }
    )
    public static List<String> playtimeInventoryItemsLore;

    @MultiValue(
            {
                    @Value(value = "&ePlayer: &f%player%", lang = LangTypes.PT_BR),
                    @Value(value = "&ePlayer: &f%player%", lang = LangTypes.EN_US)
            }
    )
    public static String playtimeInventoryItemsName;

    @MultiValue(
            {
                    @Value(value = "&eVoltar página", lang = LangTypes.PT_BR),
                    @Value(value = "&eBack page", lang = LangTypes.EN_US)
            }
    )
    public static String playtimeInventoryIconBackName;
    @MultiValue(
            {
                    @Value(value = "&ePróxima página", lang = LangTypes.PT_BR),
                    @Value(value = "&eNext page", lang = LangTypes.EN_US)
            }
    )
    public static String playtimeInventoryIconNextName;

    @MultiValue(
            {
                    @Value(value = "%prefix%&e%player% jogou por %time%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player% played for %time%", lang = LangTypes.EN_US)
            }
    )
    public static String playtimeMessage;

// shop

    @MultiValue(
            {
                    @Value(value = "&aAberto", lang = LangTypes.PT_BR),
                    @Value(value = "&aOpen", lang = LangTypes.EN_US)
            }
    )
    public static String shopOpen;

    @MultiValue(
            {
                    @Value(value = "&cFechado", lang = LangTypes.PT_BR),
                    @Value(value = "&cClosed", lang = LangTypes.EN_US)
            }
    )
    public static String shopClosed;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa loja não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis shop does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String shopNotExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eSua loja foi setada com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour shop has been set successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String shopCreateShopSuccess;

    @MultiValue(
            {
                    @Value(value = "%open%|&eVisitas -> %visits%|&eClique para ir a loja", lang = LangTypes.PT_BR),
                    @Value(value = "%open%|&eVisits -> %visits%|&eClick to go to the shop", lang = LangTypes.EN_US)
            }
    )
    public static List<String> shopInventoryItemsLore;

    @MultiValue(
            {
                    @Value(value = "&eLoja do &f%player%", lang = LangTypes.PT_BR),
                    @Value(value = "&e%player%'s Shop", lang = LangTypes.EN_US)
            }
    )
    public static String shopInventoryItemsName;

    @MultiValue(
            {
                    @Value(value = "&eVoltar página", lang = LangTypes.PT_BR),
                    @Value(value = "&eBack page", lang = LangTypes.EN_US)
            }
    )
    public static String shopInventoryIconBackName;
    @MultiValue(
            {
                    @Value(value = "&ePróxima página", lang = LangTypes.PT_BR),
                    @Value(value = "&eNext page", lang = LangTypes.EN_US)
            }
    )
    public static String shopInventoryIconNextName;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eNenhuma loja criada ainda.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eNo shop has been created yet.", lang = LangTypes.EN_US)
            }
    )
    public static String shopNotExistShop;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê foi teleportado para a loja do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have been teleported to %player%'s shop.", lang = LangTypes.EN_US)
            }
    )
    public static String shopTeleport;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa loja se encontra fechada!.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis shop is currently closed!", lang = LangTypes.EN_US)
            }
    )
    public static String shopClosedMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê mudou o estado da para %open%&e.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou changed the status to %open%&e.", lang = LangTypes.EN_US)
            }
    )
    public static String shopSwitchMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê precisa criar uma loja primeiro!.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou need to create a shop first!", lang = LangTypes.EN_US)
            }
    )
    public static String shopNotCreated;

    @MultiValue(
            {
                    @Value(value = "&aSetar sua loja nesta posição.", lang = LangTypes.PT_BR),
                    @Value(value = "&aSet your shop at this position.", lang = LangTypes.EN_US)
            }
    )
    public static String shopLoreSet;

    @MultiValue(
            {
                    @Value(value = "&aAo cliar aqui você mudará o estado de sua loja!.", lang = LangTypes.PT_BR),
                    @Value(value = "&aClicking here will change the status of your shop!", lang = LangTypes.EN_US)
            }
    )
    public static String shopLoreSwitch;

    //kits
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse kit não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis kit don't exist", lang = LangTypes.EN_US)
            }
    )
    public static String kitsNotExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse kit já existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis kit already exists.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi criado com sucesso, para edita-lo utilize /editarkit %kit%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &ewas created successfully, to edit it use /editkit %kit%", lang = LangTypes.EN_US)
            }
    )
    public static String kitsCreateKitSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eKit %kit% &edeletado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &esuccessfully deleted.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsDelKitSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi editado com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &ehas been edited successfully!", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome do kit 16 caracteres.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cMaximum length of the kit name 16 characters.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsNameLength;
    @MultiValue(
            {
                    @Value(value = "&fNome do kit -> %realname%|&eClique para ver o kit", lang = LangTypes.PT_BR),
                    @Value(value = "&fKit name -> %realname%|&eClick to see the kit", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsInventoryItemsLore;
    @MultiValue(
            {
                    @Value(value = "&eKit &f%kitrealname%", lang = LangTypes.PT_BR),
                    @Value(value = "&eKit &f%kitrealname%", lang = LangTypes.EN_US)
            }
    )
    public static String kitsInventoryItemsName;
    @MultiValue(
            {
                    @Value(value = "&eClique aqui para editar o kit", lang = LangTypes.PT_BR),
                    @Value(value = "&eClick here to edit the kit", lang = LangTypes.EN_US)
            }
    )
    public static String kitsInventoryIconEditKitName;
    @MultiValue(
            {
                    @Value(value = "&eVoltar página", lang = LangTypes.PT_BR),
                    @Value(value = "&eBack page", lang = LangTypes.EN_US)
            }
    )
    public static String kitsInventoryIconBackName;
    @MultiValue(
            {
                    @Value(value = "&ePróxima página", lang = LangTypes.PT_BR),
                    @Value(value = "&eNext page", lang = LangTypes.EN_US)
            }
    )
    public static String kitsInventoryIconNextName;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eEditKit - Items", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eEditKit - Items", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryItemsName;
    @MultiValue(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar todos os items", lang = LangTypes.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit all items", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryItemsLore;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eTempo do kit setado para %time%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eKit cooldown is %time%.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitTime;
    @MultiValue(
            {
                    @Value(value = "&eEditKit - Tempo", lang = LangTypes.PT_BR),
                    @Value(value = "&eEditKit - Cooldown", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeName;
    @MultiValue(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o tempo de espera|&fPara pegar novamente", lang = LangTypes.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit cooldown time|&fTo pick up again", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryTimeLore;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o tempo de espera para pegar novamente o kit, formato tempo/unidade -> Exemplo 30s, unidades -> [s,m,h,d]", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what the cooldown time will be to pick up the kit again, format time/unity -> Ex. 30s, unity -> [s,m,h,d]", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeMessage;
    @MultiValue(
            {
                    @Value(value = "&eEditKit - Nome", lang = LangTypes.PT_BR),
                    @Value(value = "&eEditKit - Name", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameName;
    @MultiValue(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o nome do kit", lang = LangTypes.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit kit name", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryNameLore;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo nome fictício para seu kit pode utilizar cores, caso queira mudar o nome criado terá que criar um novo kit...", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what will be the name for your kit, you can use colors, if you want to change the name created, you will have to create a new kit...", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameMessage;
    @MultiValue(
            {
                    @Value(value = "&eEditKit - Peso", lang = LangTypes.PT_BR),
                    @Value(value = "&eEditKit - Weight", lang = LangTypes.EN_US)
            }
    )


    public static String kitsEditKitInventoryWeightName;
    @MultiValue(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o peso do kit", lang = LangTypes.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit kit weight", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryWeightLore;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo peso, lembre-se quanto maior o peso mais na frente o kit aparecerá na GUI.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what the new weight will be, remember the higher the weight the further the kit will appear in the GUI.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsEditKitInventoryWeightMessage;
    @MultiValue(
            {
                    @Value(value = "&cFalta %time% para pegar seu kit novamente", lang = LangTypes.PT_BR),
                    @Value(value = "&c%time% left to get your kit again", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGetMessage;
    @MultiValue(
            {
                    @Value(value = "&cClique aqui para pegar o seu kit", lang = LangTypes.PT_BR),
                    @Value(value = "&cClick here to get your kit", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGetIcon;
    @MultiValue(
            {
                    @Value(value = "&cVocê não consegue pegar esse kit", lang = LangTypes.PT_BR),
                    @Value(value = "&cYou can't get this kit", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGetIconNotCatch;
    @MultiValue(
            {
                    @Value(value = "&cVocê não tem permissão|", lang = LangTypes.PT_BR),
                    @Value(value = "&cYou don't have permissions|", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsGetIconLoreNotPerm;
    @MultiValue(
            {
                    @Value(value = "&fFalta|&f%time%|&fPara pegar seu kit", lang = LangTypes.PT_BR),
                    @Value(value = "&fWait %time%|&fTo get your kit", lang = LangTypes.EN_US)
            }
    )
    public static List<String> kitsGetIconLoreTime;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê pegou o kit `%kit%&e` com sucesso", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eyou get the kit `%kit%&e` successfully", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGetSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cLibere mais %slots% slots para pegar esse kit!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&crelease %slots% slots to get this kit!", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGetNoSpace;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê deu o %kit% &epara o(a) %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou gived %kit% &efor %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGiveKitMessageOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém do além deu o kit `%kit%&e` para você.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond gave the kit `%kit%&e` for you.", lang = LangTypes.EN_US)
            }
    )
    public static String kitsGiveKitMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de kits %kits%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eKit list %kits%", lang = LangTypes.EN_US)
            }
    )
    public static String kitsList;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eCrie seu kit com /criarkit (nome do kit).", lang = LangTypes.PT_BR),
                    @Value(value = "Create your kit with /createkit (kit name).", lang = LangTypes.EN_US)
            }
    )
    public static String kitsNotExistKits;

    //vips
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê criou um novo vip! '%vip%'.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou created a new VIP! '%vip%'.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCreateNew;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê criou uma key vip! '%key%'.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou created a VIP key! '%key%'.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCreateNewKey;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse grupo não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis group does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsGroupNotExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa key não existe, tente outra!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis key does not exist, try another!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsKeyNotExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê ativou o %vip% de %days% dias com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou successfully activated the %vip% for %days% days!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsActivate;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse Vip já existe!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis VIP already exists!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse Vip não existe!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis VIP does not exist!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsNotExist;


    @MultiValue(
            {
                    @Value(value = "&9> VIP '&e%vipName%&9' - &e%vipTime%", lang = LangTypes.PT_BR),
                    @Value(value = "&9> VIP '&e%vipName%' - %vipTime%", lang = LangTypes.EN_US)
            }
    )
    public static String VipsTimeMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê trocou para o vip %vipName%!", lang = LangTypes.PT_BR),
                    @Value(value = "not lang", lang = LangTypes.EN_US)
            }
    )
    public static String VipsSwitch;

    @MultiValue(
            {
                    @Value(value = "Utilize o comando '/vip token %value%' para ativar a integração com o discord do servidor", lang = LangTypes.PT_BR),
                    @Value(value = "Use the command '/vip token %value%' to activate the server's Discord integration", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eUtilize o comando '/vip token <token>' para ativar a integração, o token foi enviado ao seu discord.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eUse the command '/vip token <token>' to activate the integration, the token was sent to your Discord.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordLocalMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse token não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis token does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordTokenError;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê ativou a integração com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have successfully activated the integration.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordTokenActivate;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse grupo não existe no discord.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis group does not exist on Discord.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordRoleError;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê acabou de setar o id do grupo do vip.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou have just set the VIP group's ID.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordRoleActivate;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse userid não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis user ID does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordUserIdNotExist;

    @MultiValue(
            {
                    @Value(value = "O %player% acabou de ativar o vip %vip% por %time%!", lang = LangTypes.PT_BR),
                    @Value(value = "%player% has just activated the VIP %vip% for %time%!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsDiscordActivateMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aO %player% acabou de ativar o vip %vip% por %time%!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&a%player% has just activated the VIP %vip% for %time%!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsActivateMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aItems do vip %vip% atualizados com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aVIP %vip% items updated successfully!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsUpdateItems;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cTire todos items do inventario '/vip items' para pegar esse vip.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cRemove all items from the '/vip items' inventory to claim this VIP.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsClearItemsInventory;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse player já tem items no '/vip items' por isso ele não pode receber esse vip.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis player already has items in '/vip items', so they cannot receive this VIP.", lang = LangTypes.EN_US)
            }
    )
    public static String VipsClearItemsOtherInventory;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de comandos:", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eCommand list:", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCommandsListMessage;

    @MultiValue(
            {
                    @Value(value = "&9> '&e%command%&9'", lang = LangTypes.PT_BR),
                    @Value(value = "&9> '&e%command%'", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCommandsList;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aComando adicionado!", lang = LangTypes.PT_BR),
                    @Value(value = "&9> '&e%command%'", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCommandsAdd;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aComando retirado!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aCommand removed!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsCommandsRemove;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eTempo dos vips:!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eVIP times:!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsTimeFirstMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eTempo dos vips do %player%:!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eVIP times of %player%:!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsTimeFirstOtherMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê atualmente não tem nenhum vip ativo!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou currently do not have any active VIPs!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsTimeNoVip;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de vips:", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eVIP list:", lang = LangTypes.EN_US)
            }
    )
    public static String VipsListMessage;

    @MultiValue(
            {
                    @Value(value = "&9> '&e%vip%&9'", lang = LangTypes.PT_BR),
                    @Value(value = "&9> '&e%vip%'", lang = LangTypes.EN_US)
            }
    )
    public static String VipsList;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cEle não tem este vip ativo!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThey do not have this VIP active!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsRemoveNoVip;

    @MultiValue(
            {
                    @Value(value = "%prefix%&aVip Removido com sucesso!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aVIP successfully removed!", lang = LangTypes.EN_US)
            }
    )
    public static String VipsRemove;


    //nicks
    @MultiValue(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome é de 16 caracteres.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cMaximum name length is 16 characters.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksNameLength;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse nick está proibido de utilizar.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis nick is prohibited from using.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksBlocked;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse nick já existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThat nickname already exists.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar seu nick de %nick%&e.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just set your nick from %nick%&e.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar seu nick.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just deleted your nick.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksRemovedSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar o nick do player para %nick%&e.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just set the player's nick to %nick%&e.", lang = LangTypes.EN_US)
            }
    )
    public static String nickOtherSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de setar o seu nick de %nick%&e.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond just set your nick of %nick%&e.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksOtherPlayerSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar o nick do player.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted the player's nick.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksRemovedOtherSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de apagar seu nick.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond just deleted your nick.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksRemovedOtherPlayerSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem nick para remover.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have no nick to remove.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksAlreadyOriginal;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEle não tem nick para remover.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cHe has no nick to remove.", lang = LangTypes.EN_US)
            }
    )
    public static String nicksAlreadyOriginalOther;

    //home
    @MultiValue(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da home é de 16 caracteres.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cMaximum length of home name is 16 characters.", lang = LangTypes.EN_US)
            }
    )
    public static String homesNameLength;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa home já existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis home already exists.", lang = LangTypes.EN_US)
            }
    )
    public static String homesNameAlreadyExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa home não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis home does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String homesNameDontExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar sua home %home%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted your home %home%.", lang = LangTypes.EN_US)
            }
    )
    public static String homesRemoved;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar uma home %home% do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted a home %home% from %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String homesOtherRemoved;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar sua home %home%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just created your home %home%.", lang = LangTypes.EN_US)
            }
    )
    public static String homesCreated;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma home %home% do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just created a %player% home %home%.", lang = LangTypes.EN_US)
            }
    )
    public static String homesOtherCreated;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê não pode criar mais homes você já alcançou seu limite de %limit%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou cannot create more homes you have already reached your %limit% limit", lang = LangTypes.EN_US)
            }
    )
    public static String homesLimitMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEsse mundo está bloqueado de criar homes", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis world is locked from creating men", lang = LangTypes.EN_US)
            }
    )
    public static String homesBlockedWorld;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de homes -> %list%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eHomes -> %list%", lang = LangTypes.EN_US)
            }
    )
    public static String homesList;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de homes do %player% -> %list%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eHomes from %player% -> %list%", lang = LangTypes.EN_US)
            }
    )
    public static String homesOtherList;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para sua home %home%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to your home %home%", lang = LangTypes.EN_US)
            }
    )
    public static String homesTeleported;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para home %home% do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to %player%'s home %home%.", lang = LangTypes.EN_US)
            }
    )
    public static String homesTeleportedOther;

    //warps
    @MultiValue(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da warp é de 16 caracteres.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cMaximum warp name length is 16 characters.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsNameLength;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa warp já existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis warp already exists.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsNameAlreadyExist;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEssa warp não existe.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis warp does not exist.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsNameDontExist;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para warp %warp%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSuccessfully teleported to warp %warp%.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsTeleported;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de warps -> %list%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eWarps -> %list%", lang = LangTypes.EN_US)
            }
    )
    public static String warpsList;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma warp chamada %warp%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just created a warp %warp%.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsCreated;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acabou de deletar um warp %warp%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just deleted a %warp% warp.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsRemoved;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&c	You are already expecting a teleport.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsInTeleport;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado para o warp %warp% do além.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have been teleported to the %warp% warp from beyond.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsTeleportedOtherMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO %player% foi teleportado para warp %warp% com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player% has been teleported to warp %warp% successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String warpsTeleportedOtherSuccess;

    //tp
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou were successfully teleported", lang = LangTypes.EN_US)
            }
    )
    public static String tpTeleportedSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado do além", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou were teleported from beyond", lang = LangTypes.EN_US)
            }
    )
    public static String tpTeleportedOtherSuccess;

    //tpa
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê mandou com sucesso o pedido de teleporte para %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully sent the teleport request to %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de receber um pedido de teleporte de %player%, você tem %time% segundos para aceita-lo com /tpaccept ou rejeita-lo com /tpdeny.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just received a teleport request from %player%, you have %time% seconds to accept /tpaccept or reject it with /tpdeny.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaOtherReceived;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê acabou de enviar um TPA aguarde para enviar novamente.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have just submitted a TPA, please wait to submit again.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaAlreadySend;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para aceitar.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have no requests to accept.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaNotAnyRequest;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para cancelar.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou have no requests to cancel.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaNotAnyRequestToDeny;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de aceitar o pedido de %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just accepted %player%'s request.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestAccepted;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando em %time% segundos.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player% just accepted your teleport request, teleporting in %time% seconds", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestOtherAccepted;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player% just accepted your teleport request, teleporting.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestOtherNoDelayAccepted;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO player já tem o pedido ativo de outra pessoa aguarde.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThe player already has someone else's active request, please wait.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaAlreadyInAccept;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de cancelar o pedido de %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just canceled the request for %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestDeny;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu pedido acaba de ser cancelado pelo(a) %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour request has just been canceled by %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestOtherDeny;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu pedido de TPA foi cancelado porque o %player% não aceitou.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour TPA request was canceled because %player% did not accept it.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaRequestOtherDenyTime;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não pode enviar tpa para você mesmo.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou cannot send tpa to yourself.", lang = LangTypes.EN_US)
            }
    )
    public static String tpaSameName;

    //echest
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir seu enderChest.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just opened your enderChest.", lang = LangTypes.EN_US)
            }
    )
    public static String echestSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir o enderChest de %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just opened the %player% enderChest.", lang = LangTypes.EN_US)
            }
    )
    public static String echestOtherSuccess;

    //gamemode
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acabou de entrar no gameMode %gamemode%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just entered gameMode %gamemode%.", lang = LangTypes.EN_US)
            }
    )
    public static String gamemodeUseSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguem do além setou seu gameMode de %gamemode%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond set your gameMode to %gamemode%.", lang = LangTypes.EN_US)
            }
    )
    public static String gamemodeUseOtherSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê já está nesse gamemode.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou are already in this gamemode.", lang = LangTypes.EN_US)
            }
    )
    public static String gamemodeSameGamemode;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEle já está nesse gamemode.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cHe is already in this gamemode.", lang = LangTypes.EN_US)
            }
    )
    public static String gamemodeSameOtherGamemode;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar o gamemode do(a) %player% para %gamemode%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have just set %player%'s gamemode to %gamemode%.", lang = LangTypes.EN_US)
            }
    )
    public static String gamemodeSuccessOtherMessage;

    //vanish
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu vanish foi ativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour vanish has been activated successfully.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu vanish foi desativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour vanish has been successfully deactivated.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu vanish.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your vanish.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishOtherActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu vanish.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone deactivated their vanish.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishOtherDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi ativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Vanish has been activated.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishActivatedOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi desativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Vanish has been disabled.", lang = LangTypes.EN_US)
            }
    )
    public static String vanishDisabledOther;

    //feed
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de comida.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled your foodbar.", lang = LangTypes.EN_US)
            }
    )
    public static String feedMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê já está cheio seu guloso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou're already full your sweet tooth.", lang = LangTypes.EN_US)
            }
    )
    public static String feedFullMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de comida.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond filled your food bar.", lang = LangTypes.EN_US)
            }
    )
    public static String feedOtherMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO player já está com a barra de comida cheia.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe player already has a full food bar.", lang = LangTypes.EN_US)
            }
    )
    public static String feedOtherFullMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de comida do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled the %player% food bar.", lang = LangTypes.EN_US)
            }
    )
    public static String feedSuccessOtherMessage;

    //heal
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de vida.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled your life bar.", lang = LangTypes.EN_US)
            }
    )
    public static String healMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê já está com vida cheia.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou are already full of life.", lang = LangTypes.EN_US)
            }
    )
    public static String healFullMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de vida.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond filled your life bar.", lang = LangTypes.EN_US)
            }
    )
    public static String healOtherMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO player já está com vida cheio.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe player is already at full health.", lang = LangTypes.EN_US)
            }
    )
    public static String healOtherFullMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de vida do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled %player%'s health bar.", lang = LangTypes.EN_US)
            }
    )
    public static String healSuccessOtherMessage;

    //light
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Luz foi ativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe Light has been activated.", lang = LangTypes.EN_US)
            }
    )
    public static String lightActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Luz foi desativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe Light has been deactivated.", lang = LangTypes.EN_US)
            }
    )
    public static String lightDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém ativou sua Luz.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your Light.", lang = LangTypes.EN_US)
            }
    )
    public static String lightOtherActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém desativou sua Luz.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone turned off your Light.", lang = LangTypes.EN_US)
            }
    )
    public static String lightOtherDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi ativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe %player% Light has been activated.", lang = LangTypes.EN_US)
            }
    )
    public static String lightActivatedOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi desativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe %player% light has been turned off.", lang = LangTypes.EN_US)
            }
    )
    public static String lightDisabledOther;

    //back
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhuma localização salva para voltar.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou don't have any saved locations to go back to.", lang = LangTypes.EN_US)
            }
    )
    public static String backNotToBack;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê voltou a sua ultima localização.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have returned to your last location.", lang = LangTypes.EN_US)
            }
    )
    public static String backSuccess;

    //spawn
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso para o spawn.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to spawn.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém teleportou você para o spawn.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone teleported you to spawn.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnOtherMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso o(a) %player% para o spawn.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported %player% to spawn.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnSuccessOtherMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO Spawn ainda não está setado, utilize /setspawn para setar.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cSpawn is not set yet, use /setspawn to set it.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnNotSet;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSpawn setado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSpawn successfully set.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnSetMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou are already expecting a teleport.", lang = LangTypes.EN_US)
            }
    )
    public static String spawnInTeleport;

    //fly
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu fly foi ativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour fly has been activated.", lang = LangTypes.EN_US)
            }
    )
    public static String flyActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSeu fly foi desativado com sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYour fly has been disabled.", lang = LangTypes.EN_US)
            }
    )
    public static String flyDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu fly.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your fly.", lang = LangTypes.EN_US)
            }
    )
    public static String flyOtherActive;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu fly.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone has disabled your fly.", lang = LangTypes.EN_US)
            }
    )
    public static String flyOtherDisable;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi ativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe %player% fly has been activated.", lang = LangTypes.EN_US)
            }
    )
    public static String flyActivatedOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi desativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe %player% fly has been disabled.", lang = LangTypes.EN_US)
            }
    )
    public static String flyDisabledOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cEste mundo está com fly desativado.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThis world is fly off.", lang = LangTypes.EN_US)
            }
    )
    public static String flyDisabledWorld;

    //online
    @MultiValue(
            {
                    @Value(value = "%prefix%&eExiste %amount% players online.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThere are %amount% players online.", lang = LangTypes.EN_US)
            }
    )
    public static String onlineMessage;

    //tphere
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso o(a) %player% para você.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported %player% to yourself.", lang = LangTypes.EN_US)
            }
    )
    public static String tphereTeleportedSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado do além.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou were teleported from beyond.", lang = LangTypes.EN_US)
            }
    )
    public static String tphereTeleportedOtherSuccess;

    //trash
    @MultiValue(
            {
                    @Value(value = "&cLixeira", lang = LangTypes.PT_BR),
                    @Value(value = "&cTrash", lang = LangTypes.EN_US)
            }
    )
    public static String trashMenuName;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eSua velocidade foi setado para %value%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eHis speed has been set to %value%.", lang = LangTypes.EN_US)
            }
    )
    public static String speedSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eSua velocidade voltou para o padrão.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eHis speed has returned to default.", lang = LangTypes.EN_US)
            }
    )
    public static String speedRemove;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém setou sua velocidade para %value%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone set your speed to %value%.", lang = LangTypes.EN_US)
            }
    )
    public static String speedOtherSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eAlguém setou sua velocidade para padrão.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSomeone set your speed to default.", lang = LangTypes.EN_US)
            }
    )
    public static String speedOtherRemove;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Velocidade do(a) %player% foi foi setado para %value%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player% Speed ​​has been set to %value%.", lang = LangTypes.EN_US)
            }
    )
    public static String speedSuccessOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Velocidade do(a) %player% voltou para o padrão.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Speed ​​has returned to default.", lang = LangTypes.EN_US)
            }
    )
    public static String speedRemoveOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eA Velocidade pode ser setado de 0 a 10.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eSpeed ​​can be set from 0 to 10.", lang = LangTypes.EN_US)
            }
    )
    public static String speedIncorrectValue;

    //hat
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê acaba de colocar um Chapéu.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou just put on a Hat.", lang = LangTypes.EN_US)
            }
    )
    public static String hatSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eFoi só encontrado ar em sua mão, coloque algum item!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eIt was only found air in your hand, put some item!", lang = LangTypes.EN_US)
            }
    )
    public static String hatNotFound;

    //antiafk

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê foi teleportado para o spawn por inatividade!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou were teleported to spawn due to inactivity!", lang = LangTypes.EN_US)
            }
    )
    public static String antiafkMessage;

//clearitems

    @MultiValue(
            {
                    @Value(value = "%prefix%&cLimpando o chão em %time%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cClearing ground in %time%", lang = LangTypes.EN_US)
            }
    )
    public static String ClearitemsMessage;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cChão Limpo!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cGround cleared!", lang = LangTypes.EN_US)
            }
    )
    public static String ClearitemsFinishMessage;

//material

    @MultiValue(
            {
                    @Value(value = "%prefix%&aNome do material: %material%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aMaterial name: %material%", lang = LangTypes.EN_US)
            }
    )
    public static String MaterialName;


    //money
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê tem %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have %unity% %money%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eO %player% tem %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eThe %player% has %unity% %money%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyMessageOther;
    @MultiValue(
            {
                    @Value(value = "$", lang = LangTypes.PT_BR),
                    @Value(value = "$", lang = LangTypes.EN_US)
            }
    )
    public static String moneySymbol;
    @MultiValue(
            {
                    @Value(value = "Real", lang = LangTypes.PT_BR),
                    @Value(value = "Dollar", lang = LangTypes.EN_US)
            }
    )
    public static String moneySuffixSingular;
    @MultiValue(
            {
                    @Value(value = "Reais", lang = LangTypes.PT_BR),
                    @Value(value = "Dollars", lang = LangTypes.EN_US)
            }
    )
    public static String moneySuffixPlural;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê pagou %unity% %money% para %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou paid %money% %unity% to %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyPay;
    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê recebeu %unity% %money% do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou received %money% %unity% from %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyPayOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não pode pagar você mesmo.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou can't pay yourself.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyPaySame;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê setou o dinheiro do %player% para %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou have set %player% money to %money% %unity%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneySet;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cAlguém do além setou seu dinheiro para %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cSomeone from beyond set their money to %money% %unity%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneySetOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê tirou %unity% %money% do %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou took %money% %unity% from %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyTake;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cAlguém do além tirou do seu dinheiro %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cSomeone from beyond took %money% %unity% of your money.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyTakeOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&aVocê deu %unity% %money% ao %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aYou gave %money% %unity% to %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyAdd;
    @MultiValue(
            {
                    @Value(value = "%prefix%&aAlguém do além adicionou ao seu dinheiro %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aSomeone from beyond added %money% %unity% to your money.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyAddOther;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem dinheiro suficiente, falta %unity% %money%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou don't have enough money, you lack %money% %unity%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyMissing;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eRank dos mais ricos - 10.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eRank of the richest - 10.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyTopMessage;
    @MultiValue(
            {
                    @Value(value = "&9> &e%position% - %player%, &e%unity% &a%money%.", lang = LangTypes.PT_BR),
                    @Value(value = "&9> &e%position% - %player%, &a%money% &e%unity%.", lang = LangTypes.EN_US)
            }
    )
    public static String moneyTop;

    //invsee
    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não pode ver o seu próprio inventário.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou cannot see your own inventory.", lang = LangTypes.EN_US)
            }
    )
    public static String invseeSameName;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO player Saiu e por isso foi fechado o inventário.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThe player left and so the inventory was closed.", lang = LangTypes.EN_US)
            }
    )
    public static String invseePlayerLeave;

    // day and night

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê definiu o tempo para DIA no mundo %world%.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String dayPlayerSet;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê definiu o tempo para DIA em todos mundos.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String dayConsoleSet;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê definiu o tempo para Noite no mundo %world%.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String nightPlayerSet;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê definiu o tempo para Noite em todos mundos.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String nightConsoleSet;

    //thor

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê lançou o poder de THOR onde está mirando.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String lightningMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO poder de THOR caiu sobre %player%!.", lang = LangTypes.PT_BR),
                    @Value(value = "", lang = LangTypes.EN_US)
            }
    )
    public static String lightningOtherMessage;


    //death messages
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %killer%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died for the %killer%.", lang = LangTypes.EN_US)
            }
    )
    public static String deathmessagesPlayerKillPlayer;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %entity%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died for the %entity%.", lang = LangTypes.EN_US)
            }
    )
    public static String deathmessagesEntityKillPlayer;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO %player% morreu.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died.", lang = LangTypes.EN_US)
            }
    )
    public static String deathmessagesNothingKillPlayer;
    @MultiValue(
            {
                    @Value(value = "&cCausa %cause% não está registrada em cause-replacer", lang = LangTypes.PT_BR),
                    @Value(value = "&cCause %cause% is not registered in cause-replacer", lang = LangTypes.EN_US)
            }
    )
    public static String deathmessagesCauseNotExist;
    @MultiValue(
            {
                    @Value(value = "AXOLOTL-Axolote|BEE-Abelha|BLAZE-Blaze|CAVE_SPIDER-Aranha da Caverna|COD-Bacalhau|CREEPER-Creeper|DRAGON_FIREBALL-Bola de Fogo|ENDER_DRAGON-Dragão do Fim|ENDERMAN-Enderman|ENDERMITE-Endermite|EVOKER-Invocador|GHAST-Ghast|GIANT-Zumbi Gigante|GUARDIAN-Guardião|HOGLIN-Hoglin|HUSK-Zumbi do Deserto|ILLUSIONER-Ilusionista|IRON_GOLEM-Golem de Ferro|MAGMA_CUBE-Cubo de Magma|PIGLIN-Piglin|PIGLIN_BRUTE-Piglin B|PIG_ZOMBIE-Porco Zumbi|PILLAGER-Saqueador|PUFFERFISH-Baiacu|RAVAGER-Devastador|SHULKER-Shulker|SHULKER_BULLET-Dardo de Shulker|SKELETON-Esqueleto|SLIME-Slime|SPIDER-Aranha|STRIDER-Lavagante|STRAY-Esqueleto Vagante|VEX-Fantasma|VINDICATOR-Vingador|WITCH-Bruxa|WITHER-Wither|WITHER_SKELETON-Esqueleto Wither|WITHER_SKULL-Cabeça do Wither|WOLF-Lobo|ZOGLIN-Zoglin|ZOMBIE-Zumbi|ZOMBIE_VILLAGER-AldeZumbi|ZOMBIFIED_PIGLIN-Piglin Zumbi", lang = LangTypes.PT_BR),
                    @Value(value = "AXOLOTL-Axolotl|BEE-Bee|BLAZE-Blaze|CAVE_SPIDER-SpiderCave|COD-Cod|CREEPER-Creeper|DRAGON_FIREBALL-Fireball|ENDER_DRAGON-EnderDragon|ENDERMAN-Enderman|ENDERMITE-Endermite|EVOKER-Evoker|GHAST-Ghast|GIANT-Giant|GUARDIAN-Guardian|HOGLIN-Hoglin|HUSK-Husk|ILLUSIONER-Illusioner|IRON_GOLEM-IronGolem|MAGMA_CUBE-MagmaCube|PIGLIN-Piglin|PIGLIN_BRUTE-Piglin B|PIG_ZOMBIE-ZumbiPig|PILLAGER-Pillager|PUFFERFISH-Pufferfish|RAVAGER-Ravager|SHULKER-Shulker|SHULKER_BULLET-ShulkerBullet|SKELETON-Esqueleto|SLIME-Slime|SPIDER-Spider|STRIDER-Strider|STRAY-Stray|VEX-Vex|VINDICATOR-Vindicator|WITCH-Witch|WITHER-Wither|WITHER_SKELETON-Wither skeleton|WITHER_SKULL-Skull Wither|WOLF-Wolf|ZOGLIN-Zoglin|ZOMBIE-Zumbie|ZOMBIE_VILLAGER-ZumbiVillager|ZOMBIFIED_PIGLIN-ZumbiePiglin", lang = LangTypes.EN_US)
            }
    )
    public static List<String> deathmessagesEntityReplacer;
    @MultiValue(
            {
                    @Value(value = "|SUICIDE-%prefix%&cO %player% se suicidiou-se.|POISON-%prefix%&cO %player% Morreu envenenado.|STARVATION-%prefix%&cO %player% Morreu de fome.|FALL-%prefix%&cO %player% caiu de um lugar alto.|DROWNING-%prefix%&cO %player% morreu afogado.|PROJECTILE-%prefix%&cO %player% Tomou uma flechada.|FIRE_TICK-%prefix%&cO %player% Pegou fogo.|FIRE-%prefix%&cO %player% Morreu Queimado.|ARROWS-%prefix%&cO %player% Tomou uma flechada.|CACTUS-%prefix%&cO %player% Tentou abraçar um cacto.|ENTITY_EXPLOSION-%prefix%&cO %player% Explodiu.|LIGHTNING-%prefix%&cO %player% Recebeu uma descarga elétrica e morreu.|SUFFOCATION-%prefix%&cO %player% Morreu sufocado.|LAVA-%prefix%&cO %player% Achou que lava era água e foi dar um mergulho.|MAGIC-%prefix%&cO %player% Foi vítima de bruxaria das brabas.|WITHER-%prefix%&cO %player% Foi morto por um WitherBoss.|BLOCK_EXPLOSION-%prefix%&cO %player% Explodiu.|VOID-%prefix%&cO %player% Morreu.", lang = LangTypes.PT_BR),
                    @Value(value = "|SUICIDE-%prefix%&c%player% committed suicide.|POISON-%prefix%&c%player% He died of poison.|STARVATION-%prefix%&c%player% Starved to death.|FALL-%prefix%&c%player% fell from a high place.|DROWNING-%prefix%&c%player% he drowned.|PROJECTILE-%prefix%&c%player% Took an arrow.|FIRE_TICK-%prefix%&c%player% Caught fire.|FIRE-%prefix%&c%player% He died burnt.|ARROWS-%prefix%&c%player% Took an arrow.|CACTUS-%prefix%&c%player% Tried to hug a cactus.|ENTITY_EXPLOSION-%prefix%&cO %player% Blow up.|LIGHTNING-%prefix%&c%player% He received an electrical shock and died.|SUFFOCATION-%prefix%&c%player% suffocated to death.|LAVA-%prefix%&c%player% He thought lava was water and went for a swim.|MAGIC-%prefix%&c%player% He was a victim of witchcraft from the brabas.|WITHER-%prefix%&c%player% He was killed by a WitherBoss.|BLOCK_EXPLOSION-%prefix%&c%player% blow up.|VOID-%prefix%&c%player% Dead.", lang = LangTypes.EN_US)
            }
    )
    public static List<String> deathmessagesCauseReplacer;


    @MultiValue(
            {
                    @Value(value = "%prefix%&e[+] %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e[+] %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String messagesEnterMessage;
    @MultiValue(
            {
                    @Value(value = "%prefix%&e[-] %player%.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&e[-] %player%.", lang = LangTypes.EN_US)
            }
    )
    public static String messagesLeaveMessage;

    @MultiValue(
            {
                    @Value(value = "&e[Anuncio] &9%name%&e: %message%", lang = LangTypes.PT_BR),
                    @Value(value = "&e[Announcement] &9%name%&e: %message%", lang = LangTypes.EN_US)
            }
    )
    public static String announceSendAnnounce;

    @MultiValue(
            {
                    @Value(value = "Token errado ou faltando, coloque certo para enviar ao discord, ou desative a opção de enviar ao chat do discord.", lang = LangTypes.PT_BR),
                    @Value(value = "Wrong or missing token, put it right to send to discord, or disable the option to send to discord chat.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatNoToken;
    @MultiValue(
            {
                    @Value(value = "Chat do discord especificado não existe, coloque um certo ou desative!", lang = LangTypes.PT_BR),
                    @Value(value = "Specified discord chat does not exist, set one right or disable!", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatNoChatId;
    @MultiValue(
            {
                    @Value(value = "%message%", lang = LangTypes.PT_BR),
                    @Value(value = "%message%", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatMessageToDiscordNewPattern;
    @MultiValue(
            {
                    @Value(value = "%group% • %player%", lang = LangTypes.PT_BR),
                    @Value(value = "%group% • %player%", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatMessageToDiscordNamePattern;
    @MultiValue(
            {
                    @Value(value = "&e[Disc] %player%: %message%", lang = LangTypes.PT_BR),
                    @Value(value = "&e[Disc] %player%: %message%", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatDiscordToServerPattern;
    @MultiValue(
            {
                    @Value(value = "%author%, Sua mensagem não foi enviada ao servidor pois tinha mais que %lenght% caracteres", lang = LangTypes.PT_BR),
                    @Value(value = "%author%, Your message was not sent to the server as it was longer than %length% characters", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatMessageNotSendToServer;
    @MultiValue(
            {
                    @Value(value = "✅ %player% Entrou no servidor.", lang = LangTypes.PT_BR),
                    @Value(value = "✅ %player% Joined the server.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatDiscordSendLoginMessage;
    @MultiValue(
            {
                    @Value(value = "⛔ %player% Saiu do servidor.", lang = LangTypes.PT_BR),
                    @Value(value = "⛔ %player% Left the server.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatDiscordSendLeaveMessage;
    @MultiValue(
            {
                    @Value(value = "Network • server • %time%.", lang = LangTypes.PT_BR),
                    @Value(value = "Network • server • %time%.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatFooter;
    @MultiValue(
            {
                    @Value(value = "👥 Online: %online% • ⏱️ Tempo total: %online_time% • 🕒 Atualizado: %time%", lang = LangTypes.PT_BR),
                    @Value(value = "👥 Online: %online% • ⏱️ Online time: %online_time% • 🕒 Updated: %time%", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatTopic;
    @MultiValue(
            {
                    @Value(value = "✅ Servidor Iniciado", lang = LangTypes.PT_BR),
                    @Value(value = "✅ Server started", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatServerStart;
    @MultiValue(
            {
                    @Value(value = "⛔ Servidor fechado", lang = LangTypes.PT_BR),
                    @Value(value = "⛔ Server closed", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatServerClose;

    @MultiValue(
            {
                    @Value(value = "Player: %player%, IP: %ip%, País: %country%, Estado: %state%, Cidade: %city%.", lang = LangTypes.PT_BR),
                    @Value(value = "Player: %player%, IP: %ip%, Country: %country%, State: %state%, City: %city%.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatSendPlayerLocale;

    @MultiValue(
            {
                    @Value(value = "Trocou ip -> Player: %player%, IP: %ip%, País: %country%, Estado: %state%, Cidade: %city%.", lang = LangTypes.PT_BR),
                    @Value(value = "Changed IP -> Player: %player%, IP: %ip%, Country: %country%, State: %state%, City: %city%.", lang = LangTypes.EN_US)
            }
    )
    public static String discordchatSendPlayerLocalAtt;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eLista de cores disponíveis: %colors%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eAvailable colors list: %colors%", lang = LangTypes.EN_US)
            }
    )
    public static String colorSendList;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eCor setada com sucesso! : %color%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eColor successfully set! : %color%", lang = LangTypes.EN_US)
            }
    )
    public static String colorSet;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para utilizar essa cor!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou do not have permission to use this color!", lang = LangTypes.EN_US)
            }
    )
    public static String colorNotSet;

    @MultiValue(
            {
                    @Value(value = "%prefix%&cVocê acabou de remover a sua cor do chat!", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cYou just removed your chat color!", lang = LangTypes.EN_US)
            }
    )
    public static String colorRemove;

    @MultiValue(
            {
                    @Value(value = "%prefix%&eTentando conectar ao servidor &f%server%&e...", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eTrying to connect to server &f%server%&e...", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectTrying;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cNome de servidor inválido.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cInvalid server name.", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectInvalidName;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cO servidor &f%server% &cnão existe ou não está registrado no Velocity.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cServer &f%server% &cdoes not exist or is not registered in Velocity.", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectServerNotFound;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eVocê já está conectado ao servidor &f%server%&e.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eYou are already connected to server &f%server%&e.", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectAlreadyConnected;
    @MultiValue(
            {
                    @Value(value = "%prefix%&eIniciando tentativa de conexão com &f%server%&e...", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&eStarting connection attempt to &f%server%&e...", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectConnectionAttempt;
    @MultiValue(
            {
                    @Value(value = "%prefix%&aConectado ao servidor &f%server% &acom sucesso.", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&aSuccessfully connected to server &f%server%&a.", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectSuccess;
    @MultiValue(
            {
                    @Value(value = "%prefix%&cNão foi possível conectar ao servidor &f%server%&c: %reason%", lang = LangTypes.PT_BR),
                    @Value(value = "%prefix%&cCould not connect to server &f%server%&c: %reason%", lang = LangTypes.EN_US)
            }
    )
    public static String totalconnectFailure;


    @Nullable
    @Override
    public String getGeneralCommandsUsage() {
        return generalCommandsUsage;
    }

    @Nullable
    @Override
    public String getGeneralCommandsUsageList() {
        return generalCommandsUsageList;
    }

    @Nullable
    @Override
    public String getGeneralCooldownMoreTime() {
        return generalCooldownMoreTime;
    }

    @Nullable
    @Override
    public String getGeneralNotPerm() {
        return generalNotPerm;
    }

    @Nullable
    @Override
    public String getGeneralNotPermAction() {
        return generalNotPermAction;
    }

    @Nullable
    @Override
    public String getGeneralOnlyConsoleCommand() {
        return generalOnlyConsoleCommand;
    }

    @Nullable
    @Override
    public String getGeneralOnlyPlayerCommand() {
        return generalOnlyPlayerCommand;
    }

    @Nullable
    @Override
    public String getGeneralPlayerNotExist() {
        return generalPlayerNotExist;
    }

    @Nullable
    @Override
    public String getGeneralPlayerNotOnline() {
        return generalPlayerNotOnline;
    }

    // Native chat
    @MultiValue({
            @Value(value = "%prefix%&cVocê não tem permissão para usar este canal.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cYou do not have permission to use this channel.", lang = LangTypes.EN_US)
    })
    public static String chatNoPermission;
    @MultiValue({
            @Value(value = "%prefix%&cVocê não tem permissão para falar neste canal.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cYou do not have permission to speak in this channel.", lang = LangTypes.EN_US)
    })
    public static String chatNoSpeakPermission;
    @MultiValue({
            @Value(value = "%prefix%&cCanal não encontrado. Use /chat para ver os canais.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cChannel not found. Use /chat to list channels.", lang = LangTypes.EN_US)
    })
    public static String chatChannelNotFound;
    @MultiValue({
            @Value(value = "%prefix%&aAgora você está falando no canal &f%channel%&a.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&aYou are now speaking in &f%channel%&a.", lang = LangTypes.EN_US)
    })
    public static String chatChannelSelected;
    @MultiValue({
            @Value(value = "%prefix%&aVocê entrou no canal &f%channel%&a.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&aYou joined &f%channel%&a.", lang = LangTypes.EN_US)
    })
    public static String chatChannelJoined;
    @MultiValue({
            @Value(value = "%prefix%&eVocê saiu do canal &f%channel%&e.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&eYou left &f%channel%&e.", lang = LangTypes.EN_US)
    })
    public static String chatChannelLeft;

    @MultiValue({
            @Value(value = "%prefix%&cEste canal não pode ser abandonado.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cThis channel cannot be left.", lang = LangTypes.EN_US)
    })
    public static String chatCannotLeave;
    @MultiValue({
            @Value(value = "%prefix%&cVocê não está ouvindo este canal.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cYou are not listening to this channel.", lang = LangTypes.EN_US)
    })
    public static String chatNotListening;
    @MultiValue({
            @Value(value = "%prefix%&cAguarde %seconds%s para falar novamente neste canal.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cWait %seconds%s before speaking in this channel again.", lang = LangTypes.EN_US)
    })
    public static String chatCooldown;
    @MultiValue({
            @Value(value = "%prefix%&eCanais disponíveis: &f%channels%", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&eAvailable channels: &f%channels%", lang = LangTypes.EN_US)
    })
    public static String chatListHeader;
    @MultiValue({
            @Value(value = "%prefix%&aConfiguração do chat recarregada.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&aChat configuration reloaded.", lang = LangTypes.EN_US)
    })
    public static String chatReload;

    // Private messages
    @MultiValue({
            @Value(value = "%prefix%&cEsse jogador não está conectado na rede.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cThat player is not connected to the network.", lang = LangTypes.EN_US)
    })
    public static String tellPlayerNotOnline;
    @MultiValue({
            @Value(value = "%prefix%&cVocê não pode enviar mensagem para si mesmo.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cYou cannot message yourself.", lang = LangTypes.EN_US)
    })
    public static String tellCannotSelf;
    @MultiValue({
            @Value(value = "%prefix%&cVocê não tem ninguém para responder.", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&cYou have nobody to reply to.", lang = LangTypes.EN_US)
    })
    public static String tellNoReply;
    @MultiValue({
            @Value(value = "&8[&dEu &8-> &d%target%&8] &f%message%", lang = LangTypes.PT_BR),
            @Value(value = "&8[&dMe &8-> &d%target%&8] &f%message%", lang = LangTypes.EN_US)
    })
    public static String tellSenderFormat;
    @MultiValue({
            @Value(value = "&8[&d%player% &8-> &dEu&8] &f%message%", lang = LangTypes.PT_BR),
            @Value(value = "&8[&d%player% &8-> &dMe&8] &f%message%", lang = LangTypes.EN_US)
    })
    public static String tellReceiverFormat;

    @MultiValue({
            @Value(value = "%prefix%&eUse: /tell <jogador> <mensagem>", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&eUse: /tell <player> <message>", lang = LangTypes.EN_US)
    })
    public static String tellUsage;
    @MultiValue({
            @Value(value = "%prefix%&eUse: /r <mensagem>", lang = LangTypes.PT_BR),
            @Value(value = "%prefix%&eUse: /r <message>", lang = LangTypes.EN_US)
    })
    public static String tellReplyUsage;

    @Nullable
    @Override
    public String getGeneralServerPrefix() {
        return generalServerPrefix;
    }

    @Nullable
    @Override
    public String getTimeDay() {
        return timeDay;
    }

    @Nullable
    @Override
    public String getTimeDayShort() {
        return timeDayShort;
    }

    @Nullable
    @Override
    public String getTimeDays() {
        return timeDays;
    }

    @Nullable
    @Override
    public String getTimeHour() {
        return timeHour;
    }

    @Nullable
    @Override
    public String getTimeHourShort() {
        return timeHourShort;
    }

    @Nullable
    @Override
    public String getTimeHours() {
        return timeHours;
    }

    @Nullable
    @Override
    public String getTimeMinute() {
        return timeMinute;
    }

    @Nullable
    @Override
    public String getTimeMinuteShort() {
        return timeMinuteShort;
    }

    @Nullable
    @Override
    public String getTimeMinutes() {
        return timeMinutes;
    }

    @Nullable
    @Override
    public String getTimeSecond() {
        return timeSecond;
    }

    @Nullable
    @Override
    public String getTimeSecondShort() {
        return timeSecondShort;
    }

    @Nullable
    @Override
    public String getTimeSeconds() {
        return timeSeconds;
    }
}
