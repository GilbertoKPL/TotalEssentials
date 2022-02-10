package github.gilbertokpl.essentialsk.config.files;

import github.gilbertokpl.essentialsk.config.annotations.Value;
import github.gilbertokpl.essentialsk.config.annotations.Values;
import github.gilbertokpl.essentialsk.config.lang.Lang;

import java.util.List;

public class LangConfig {

    @Values(
            {
                    @Value(value = "&a[Server]&r ", lang = Lang.PT_BR),
                    @Value(value = "&a[Server]&r ", lang = Lang.EN_US)
            }
    )
    public static String generalServerPrefix;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse comando é somente para player!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis command is for player only!", lang = Lang.EN_US)
            }
    )
    public static String generalOnlyPlayerCommand;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para executar esse comando.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou do not have permissions to run this command.", lang = Lang.EN_US)
            }
    )
    public static String generalNotPerm;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para fazer isso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou don't have permissions.", lang = Lang.EN_US)
            }
    )
    public static String generalNotPermAction;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse player não está online.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis player is not online.", lang = Lang.EN_US)
            }
    )
    public static String generalPlayerNotOnline;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse player não existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis player does not exist.", lang = Lang.EN_US)
            }
    )
    public static String generalPlayerNotExist;
    @Values(
            {
                    @Value(value = "&9[EssentialsK Ajuda]", lang = Lang.PT_BR),
                    @Value(value = "&9[EssentialsK Help]", lang = Lang.EN_US)
            }
    )
    public static String generalCommandsUsage;
    @Values(
            {
                    @Value(value = "'&9> &e%command%'", lang = Lang.PT_BR),
                    @Value(value = "'&9> &e%command%'", lang = Lang.EN_US)
            }
    )
    public static String generalCommandsUsageList;
    @Values(
            {
                    @Value(value = "%prefix%&cCaracteres proibidos", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cForbidden simbols", lang = Lang.EN_US)
            }
    )
    public static String generalSpecialCaracteresDisabled;
    @Values(
            {
                    @Value(value = "%prefix%&cFalta %time% para executar novamente esse comando.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThere are %time% left to run this command again.", lang = Lang.EN_US)
            }
    )
    public static String generalCooldownMoreTime;
    @Values(
            {
                    @Value(value = "%prefix%&cAdicione o Vault para o funcionamento do plugin!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cAdd Vault for plugin working!", lang = Lang.EN_US)
            }
    )
    public static String generalVaultNotExist;
    @Values(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, spawn desativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cWorld %world% no existing, spawn disabled.", lang = Lang.EN_US)
            }
    )
    public static String generalWorldNotExistSpawn;
    @Values(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, warp %warp% desativada.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cWorld %world% no existing, warp %warp% disabled.", lang = Lang.EN_US)
            }
    )
    public static String generalWorldNotExistWarp;
    @Values(
            {
                    @Value(value = "%prefix%&eConfig reninciada com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eReloaded config successfully.", lang = Lang.EN_US)
            }
    )
    public static String generalConfigReload;
    @Values(
            {
                    @Value(value = "&aIP: &f%ip%|&aOS: &f%os%, &aversão &f%os_version%|&aNome CPU: &f%cpu_name%|&aNúcleos: &f%cores% &anúcleos|&aNúcleos Liberados: &f%cores_server% &anúcleos|&aUso do CPU : &f%cpu_usage% %|&aClock CPU: &f%cpu_clock_min% / %cpu_clock_max% mhz|&aMemória: &f%used_mem% / %max_mem% mb|&aMemória Liberada: &f%used_server_mem% / %max_server_mem% mb|&aHD Usado: &f%used_hd%/%max_hd% mb|&aGPU: &f%gpu%", lang = Lang.PT_BR),
                    @Value(value = "&aIP: &f%ip%|&aOS: &f%os%, &aversion &f%os_version%|&aCPU Name: &f%cpu_name%|&aCores: &f%cores% &anúcleos|&aReleased Cores: &f%cores_server% &anúcleos|&aCPU Usage : &f%cpu_usage% %|&aClock CPU: &f%cpu_clock_min% / %cpu_clock_max% mhz|&aMemory: &f%used_mem% / %max_mem% mb|&aFree memory: &f%used_server_mem% / %max_server_mem% mb|&aHD: &f%used_hd%/%max_hd% mb|&aGPU: &f%gpu%", lang = Lang.EN_US)
            }
    )
    public static List<String> generalHostConfigInfo;
    @Values(
            {
                    @Value(value = "%prefix%&ePegando informações da host... Aguarde!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&ecollecting information from host... Please wait!", lang = Lang.EN_US)
            }
    )
    public static String generalHostWait;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin não encontrado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cPlugin not found.", lang = Lang.EN_US)
            }
    )
    public static String generalPluginNotFound;
    @Values(
            {
                    @Value(value = "%prefix%&ePlugin descarregado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&ePlugin unloaded successfully.", lang = Lang.EN_US)
            }
    )
    public static String generalPluginUnload;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin descarregado com problemas.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cPlugin unloaded with problems.", lang = Lang.EN_US)
            }
    )
    public static String generalPluginUnloadProblems;
    @Values(
            {
                    @Value(value = "%prefix%&ePlugin carregado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&ePlugin loaded successfully.", lang = Lang.EN_US)
            }
    )
    public static String generalPluginLoad;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin carregado com problemas.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cPlugin loaded with problems.", lang = Lang.EN_US)
            }
    )
    public static String generalPluginLoadProblems;
    @Values(
            {
                    @Value(value = "Salvando informações.", lang = Lang.PT_BR),
                    @Value(value = "Saving information.", lang = Lang.EN_US)
            }
    )
    public static String generalSaveDataMessage;
    @Values(
            {
                    @Value(value = "Salvo com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "Saved successfully.", lang = Lang.EN_US)
            }
    )
    public static String generalSaveDataSuccess;

    //time
    @Values(
            {
                    @Value(value = "segundos", lang = Lang.PT_BR),
                    @Value(value = "seconds", lang = Lang.EN_US)
            }
    )
    public static String timeSeconds;
    @Values(
            {
                    @Value(value = "segundo", lang = Lang.PT_BR),
                    @Value(value = "second", lang = Lang.EN_US)
            }
    )
    public static String timeSecond;
    @Values(
            {
                    @Value(value = "s", lang = Lang.PT_BR),
                    @Value(value = "s", lang = Lang.EN_US)
            }
    )
    public static String timeSecondShort;
    @Values(
            {
                    @Value(value = "minutos", lang = Lang.PT_BR),
                    @Value(value = "minutes", lang = Lang.EN_US)
            }
    )
    public static String timeMinutes;
    @Values(
            {
                    @Value(value = "minuto", lang = Lang.PT_BR),
                    @Value(value = "minute", lang = Lang.EN_US)
            }
    )
    public static String timeMinute;
    @Values(
            {
                    @Value(value = "m", lang = Lang.PT_BR),
                    @Value(value = "m", lang = Lang.EN_US)
            }
    )
    public static String timeMinuteShort;
    @Values(
            {
                    @Value(value = "horas", lang = Lang.PT_BR),
                    @Value(value = "hours", lang = Lang.EN_US)
            }
    )
    public static String timeHours;
    @Values(
            {
                    @Value(value = "hora", lang = Lang.PT_BR),
                    @Value(value = "hour", lang = Lang.EN_US)
            }
    )
    public static String timeHour;
    @Values(
            {
                    @Value(value = "h", lang = Lang.PT_BR),
                    @Value(value = "h", lang = Lang.EN_US)
            }
    )
    public static String timeHourShort;
    @Values(
            {
                    @Value(value = "dias", lang = Lang.PT_BR),
                    @Value(value = "days", lang = Lang.EN_US)
            }
    )
    public static String timeDays;
    @Values(
            {
                    @Value(value = "dia", lang = Lang.PT_BR),
                    @Value(value = "day", lang = Lang.EN_US)
            }
    )
    public static String timeDay;
    @Values(
            {
                    @Value(value = "d", lang = Lang.PT_BR),
                    @Value(value = "d", lang = Lang.EN_US)
            }
    )
    public static String timeDayShort;

    //kits
    @Values(
            {
                    @Value(value = "%prefix%&cEsse kit não existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis kit don't exist", lang = Lang.EN_US)
            }
    )
    public static String kitsNotExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse kit já existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis kit already exists.", lang = Lang.EN_US)
            }
    )
    public static String kitsExist;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi criado com sucesso, para edita-lo utilize /editarkit %kit%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &ewas created successfully, to edit it use /editkit %kit%", lang = Lang.EN_US)
            }
    )
    public static String kitsCreateKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &edeletado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &esuccessfully deleted.", lang = Lang.EN_US)
            }
    )
    public static String kitsDelKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi editado com sucesso!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eKit %kit% &ehas been edited successfully!", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome do kit 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cMaximum length of the kit name 16 characters.", lang = Lang.EN_US)
            }
    )
    public static String kitsNameLength;
    @Values(
            {
                    @Value(value = "&fNome do kit -> %realname%|&eClique para ver o kit", lang = Lang.PT_BR),
                    @Value(value = "&fKit name -> %realname%|&eClick to see the kit", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsInventoryItemsLore;
    @Values(
            {
                    @Value(value = "&eKit &f%kitrealname%", lang = Lang.PT_BR),
                    @Value(value = "&eKit &f%kitrealname%", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryItemsName;
    @Values(
            {
                    @Value(value = "&eClique aqui para editar o kit", lang = Lang.PT_BR),
                    @Value(value = "&eClick here to edit the kit", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconEditKitName;
    @Values(
            {
                    @Value(value = "&eVoltar página", lang = Lang.PT_BR),
                    @Value(value = "&eBack page", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconBackName;
    @Values(
            {
                    @Value(value = "&ePróxima página", lang = Lang.PT_BR),
                    @Value(value = "&eNext page", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconNextName;
    @Values(
            {
                    @Value(value = "%prefix%&eEditKit - Items", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eEditKit - Items", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryItemsName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar todos os items", lang = Lang.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit all items", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryItemsLore;
    @Values(
            {
                    @Value(value = "%prefix%&eTempo do kit setado para %time%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eKit cooldown is %time%.", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitTime;
    @Values(
            {
                    @Value(value = "&eEditKit - Tempo", lang = Lang.PT_BR),
                    @Value(value = "&eEditKit - Cooldown", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o tempo de espera|&fPara pegar novamente", lang = Lang.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit cooldown time|&fTo pick up again", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryTimeLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o tempo de espera para pegar novamente o kit, formato tempo/unidade -> Exemplo 30s, unidades -> [s,m,h,d]", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what the cooldown time will be to pick up the kit again, format time/unity -> Ex. 30s, unity -> [s,m,h,d]", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeMessage;
    @Values(
            {
                    @Value(value = "&eEditKit - Nome", lang = Lang.PT_BR),
                    @Value(value = "&eEditKit - Name", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o nome do kit", lang = Lang.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit kit name", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryNameLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo nome fictício para seu kit pode utilizar cores, caso queira mudar o nome criado terá que criar um novo kit...", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what will be the name for your kit, you can use colors, if you want to change the name created, you will have to create a new kit...", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameMessage;
    @Values(
            {
                    @Value(value = "&eEditKit - Peso", lang = Lang.PT_BR),
                    @Value(value = "&eEditKit - Weight", lang = Lang.EN_US)
            }
    )


    public static String kitsEditKitInventoryWeightName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o peso do kit", lang = Lang.PT_BR),
                    @Value(value = "&fClick to edit|&fBy clicking you can|&fEdit kit weight", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryWeightLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo peso, lembre-se quanto maior o peso mais na frente o kit aparecerá na GUI.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eType in the chat what the new weight will be, remember the higher the weight the further the kit will appear in the GUI.", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryWeightMessage;
    @Values(
            {
                    @Value(value = "&cFalta %time% para pegar seu kit novamente", lang = Lang.PT_BR),
                    @Value(value = "&c%time% left to get your kit again", lang = Lang.EN_US)
            }
    )
    public static String kitsGetMessage;
    @Values(
            {
                    @Value(value = "&cClique aqui para pegar o seu kit", lang = Lang.PT_BR),
                    @Value(value = "&cClick here to get your kit", lang = Lang.EN_US)
            }
    )
    public static String kitsGetIcon;
    @Values(
            {
                    @Value(value = "&cVocê não consegue pegar esse kit", lang = Lang.PT_BR),
                    @Value(value = "&cYou can't get this kit", lang = Lang.EN_US)
            }
    )
    public static String kitsGetIconNotCatch;
    @Values(
            {
                    @Value(value = "&cVocê não tem permissão|", lang = Lang.PT_BR),
                    @Value(value = "&cYou don't have permissions|", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsGetIconLoreNotPerm;
    @Values(
            {
                    @Value(value = "&fFalta|&f%time%|&fPara pegar seu kit", lang = Lang.PT_BR),
                    @Value(value = "&fWait %time%|&fTo get your kit", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsGetIconLoreTime;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê pegou o kit `%kit%&e` com sucesso", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eyou get the kit `%kit%&e` successfully", lang = Lang.EN_US)
            }
    )
    public static String kitsGetSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cLibere mais %slots% slots para pegar esse kit!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&crelease %slots% slots to get this kit!", lang = Lang.EN_US)
            }
    )
    public static String kitsGetNoSpace;

    @Values(
            {
                    @Value(value = "%prefix%&eVocê deu o %kit% &epara o(a) %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou gived %kit% &efor %player%.", lang = Lang.EN_US)
            }
    )
    public static String kitsGiveKitMessageOther;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além deu o kit `%kit%&e` para você.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond gave the kit `%kit%&e` for you.", lang = Lang.EN_US)
            }
    )
    public static String kitsGiveKitMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de kits %kits%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eKit list %kits%", lang = Lang.EN_US)
            }
    )
    public static String kitsList;
    @Values(
            {
                    @Value(value = "%prefix%&eCrie seu kit com /criarkit (nome do kit).", lang = Lang.PT_BR),
                    @Value(value = "Create your kit with /createkit (kit name).", lang = Lang.EN_US)
            }
    )
    public static String kitsNotExistKits;

    //nicks
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cMaximum name length is 16 characters.", lang = Lang.EN_US)
            }
    )
    public static String nicksNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse nick está proibido de utilizar.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis nick is prohibited from using.", lang = Lang.EN_US)
            }
    )
    public static String nicksBlocked;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse nick já existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThat nickname already exists.", lang = Lang.EN_US)
            }
    )
    public static String nicksExist;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar seu nick de %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just set your nick from %nick%&e.", lang = Lang.EN_US)
            }
    )
    public static String nicksSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar seu nick.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just deleted your nick.", lang = Lang.EN_US)
            }
    )
    public static String nicksRemovedSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar o nick do player para %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just set the player's nick to %nick%&e.", lang = Lang.EN_US)
            }
    )
    public static String nickOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de setar o seu nick de %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond just set your nick of %nick%&e.", lang = Lang.EN_US)
            }
    )
    public static String nicksOtherPlayerSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar o nick do player.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted the player's nick.", lang = Lang.EN_US)
            }
    )
    public static String nicksRemovedOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de apagar seu nick.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond just deleted your nick.", lang = Lang.EN_US)
            }
    )
    public static String nicksRemovedOtherPlayerSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nick para remover.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou have no nick to remove.", lang = Lang.EN_US)
            }
    )
    public static String nicksAlreadyOriginal;
    @Values(
            {
                    @Value(value = "%prefix%&cEle não tem nick para remover.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cHe has no nick to remove.", lang = Lang.EN_US)
            }
    )
    public static String nicksAlreadyOriginalOther;

    //home
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da home é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cMaximum length of home name is 16 characters.", lang = Lang.EN_US)
            }
    )
    public static String homesNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa home já existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis home already exists.", lang = Lang.EN_US)
            }
    )
    public static String homesNameAlreadyExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa home não existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis home does not exist.", lang = Lang.EN_US)
            }
    )
    public static String homesNameDontExist;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para %home%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eIn %time% seconds you will be teleported to %home%.", lang = Lang.EN_US)
            }
    )
    public static String homesTimeToTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar sua home %home%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted your home %home%.", lang = Lang.EN_US)
            }
    )
    public static String homesRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar uma home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just deleted a home %home% from %player%.", lang = Lang.EN_US)
            }
    )
    public static String homesOtherRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar sua home %home%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just created your home %home%.", lang = Lang.EN_US)
            }
    )
    public static String homesCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just created a %player% home %home%.", lang = Lang.EN_US)
            }
    )
    public static String homesOtherCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê não pode criar mais homes você já alcançou seu limite de %limit%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou cannot create more homes you have already reached your %limit% limit", lang = Lang.EN_US)
            }
    )
    public static String homesLimitMessage;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse mundo está bloqueado de criar homes", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis world is locked from creating men", lang = Lang.EN_US)
            }
    )
    public static String homesBlockedWorld;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de homes -> %list%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eHomes -> %list%", lang = Lang.EN_US)
            }
    )
    public static String homesList;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de homes do %player% -> %list%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eHomes from %player% -> %list%", lang = Lang.EN_US)
            }
    )
    public static String homesOtherList;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para sua home %home%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to your home %home%", lang = Lang.EN_US)
            }
    )
    public static String homesTeleported;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to %player%'s home %home%.", lang = Lang.EN_US)
            }
    )
    public static String homesTeleportedOther;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou are already expecting a teleport.", lang = Lang.EN_US)
            }
    )
    public static String homesInTeleport;

    //warps
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da warp é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cMaximum warp name length is 16 characters.", lang = Lang.EN_US)
            }
    )
    public static String warpsNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa warp já existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis warp already exists.", lang = Lang.EN_US)
            }
    )
    public static String warpsNameAlreadyExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa warp não existe.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis warp does not exist.", lang = Lang.EN_US)
            }
    )
    public static String warpsNameDontExist;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para warp %warp%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eIn %time% seconds you will be teleported to warp %warp%.", lang = Lang.EN_US)
            }
    )
    public static String warpsTimeToTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para warp %warp%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSuccessfully teleported to warp %warp%.", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleported;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de warps -> %list%", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eWarps -> %list%", lang = Lang.EN_US)
            }
    )
    public static String warpsList;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma warp chamada %warp%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just created a warp %warp%.", lang = Lang.EN_US)
            }
    )
    public static String warpsCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de deletar um warp %warp%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just deleted a %warp% warp.", lang = Lang.EN_US)
            }
    )
    public static String warpsRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&c	You are already expecting a teleport.", lang = Lang.EN_US)
            }
    )
    public static String warpsInTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado para o warp %warp% do além.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have been teleported to the %warp% warp from beyond.", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleportedOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% foi teleportado para warp %warp% com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player% has been teleported to warp %warp% successfully.", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleportedOtherSuccess;

    //tp
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou were successfully teleported", lang = Lang.EN_US)
            }
    )
    public static String tpTeleportedSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado do além", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou were teleported from beyond", lang = Lang.EN_US)
            }
    )
    public static String tpTeleportedOtherSuccess;

    //tpa
    @Values(
            {
                    @Value(value = "%prefix%&eVocê mandou com sucesso o pedido de teleporte para %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully sent the teleport request to %player%.", lang = Lang.EN_US)
            }
    )
    public static String tpaSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de receber um pedido de teleporte de %player%, você tem %time% segundos para aceita-lo com /tpaccept ou rejeita-lo com /tpdeny.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just received a teleport request from %player%, you have %time% seconds to accept /tpaccept or reject it with /tpdeny.", lang = Lang.EN_US)
            }
    )
    public static String tpaOtherReceived;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê acabou de enviar um TPA aguarde para enviar novamente.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou have just submitted a TPA, please wait to submit again.", lang = Lang.EN_US)
            }
    )
    public static String tpaAlreadySend;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para aceitar.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou have no requests to accept.", lang = Lang.EN_US)
            }
    )
    public static String tpaNotAnyRequest;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para cancelar.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou have no requests to cancel.", lang = Lang.EN_US)
            }
    )
    public static String tpaNotAnyRequestToDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de aceitar o pedido de %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just accepted %player%'s request.", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando em %time% segundos.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player% just accepted your teleport request, teleporting in %time% seconds", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player% just accepted your teleport request, teleporting.", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherNoDelayAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&cO player já tem o pedido ativo de outra pessoa aguarde.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThe player already has someone else's active request, please wait.", lang = Lang.EN_US)
            }
    )
    public static String tpaAlreadyInAccept;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de cancelar o pedido de %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just canceled the request for %player%.", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu pedido acaba de ser cancelado pelo(a) %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour request has just been canceled by %player%.", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu pedido de TPA foi cancelado porque o %player% não aceitou.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour TPA request was canceled because %player% did not accept it.", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherDenyTime;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não pode enviar tpa para você mesmo.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou cannot send tpa to yourself.", lang = Lang.EN_US)
            }
    )
    public static String tpaSameName;

    //echest
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir seu enderChest.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just opened your enderChest.", lang = Lang.EN_US)
            }
    )
    public static String echestSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir o enderChest de %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just opened the %player% enderChest.", lang = Lang.EN_US)
            }
    )
    public static String echestOtherSuccess;

    //gamemode
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de entrar no gameMode %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just entered gameMode %gamemode%.", lang = Lang.EN_US)
            }
    )
    public static String gamemodeUseSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguem do além setou seu gameMode de %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond set your gameMode to %gamemode%.", lang = Lang.EN_US)
            }
    )
    public static String gamemodeUseOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está nesse gamemode.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou are already in this gamemode.", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSameGamemode;
    @Values(
            {
                    @Value(value = "%prefix%&cEle já está nesse gamemode.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cHe is already in this gamemode.", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSameOtherGamemode;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar o gamemode do(a) %player% para %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have just set %player%'s gamemode to %gamemode%.", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSuccessOtherMessage;

    //vanish
    @Values(
            {
                    @Value(value = "%prefix%&eSeu vanish foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour vanish has been activated successfully.", lang = Lang.EN_US)
            }
    )
    public static String vanishActive;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu vanish foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour vanish has been successfully deactivated.", lang = Lang.EN_US)
            }
    )
    public static String vanishDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu vanish.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your vanish.", lang = Lang.EN_US)
            }
    )
    public static String vanishOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu vanish.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone deactivated their vanish.", lang = Lang.EN_US)
            }
    )
    public static String vanishOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Vanish has been activated.", lang = Lang.EN_US)
            }
    )
    public static String vanishActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Vanish has been disabled.", lang = Lang.EN_US)
            }
    )
    public static String vanishDisabledOther;

    //feed
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de comida.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled your foodbar.", lang = Lang.EN_US)
            }
    )
    public static String feedMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê já está cheio seu guloso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou're already full your sweet tooth.", lang = Lang.EN_US)
            }
    )
    public static String feedFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de comida.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond filled your food bar.", lang = Lang.EN_US)
            }
    )
    public static String feedOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO player já está com a barra de comida cheia.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe player already has a full food bar.", lang = Lang.EN_US)
            }
    )
    public static String feedOtherFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de comida do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled the %player% food bar.", lang = Lang.EN_US)
            }
    )
    public static String feedSuccessOtherMessage;

    //heal
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de vida.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled your life bar.", lang = Lang.EN_US)
            }
    )
    public static String healMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê já está com vida cheia.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou are already full of life.", lang = Lang.EN_US)
            }
    )
    public static String healFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de vida.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone from beyond filled your life bar.", lang = Lang.EN_US)
            }
    )
    public static String healOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO player já está com vida cheio.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe player is already at full health.", lang = Lang.EN_US)
            }
    )
    public static String healOtherFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de vida do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully filled %player%'s health bar.", lang = Lang.EN_US)
            }
    )
    public static String healSuccessOtherMessage;

    //light
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe Light has been activated.", lang = Lang.EN_US)
            }
    )
    public static String lightActive;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe Light has been deactivated.", lang = Lang.EN_US)
            }
    )
    public static String lightDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou sua Luz.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your Light.", lang = Lang.EN_US)
            }
    )
    public static String lightOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou sua Luz.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone turned off your Light.", lang = Lang.EN_US)
            }
    )
    public static String lightOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe %player% Light has been activated.", lang = Lang.EN_US)
            }
    )
    public static String lightActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe %player% light has been turned off.", lang = Lang.EN_US)
            }
    )
    public static String lightDisabledOther;

    //back
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhuma localização salva para voltar.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou don't have any saved locations to go back to.", lang = Lang.EN_US)
            }
    )
    public static String backNotToBack;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê voltou a sua ultima localização.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have returned to your last location.", lang = Lang.EN_US)
            }
    )
    public static String backSuccess;

    //spawn
    @Values(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported to spawn.", lang = Lang.EN_US)
            }
    )
    public static String spawnMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém teleportou você para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone teleported you to spawn.", lang = Lang.EN_US)
            }
    )
    public static String spawnOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso o(a) %player% para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported %player% to spawn.", lang = Lang.EN_US)
            }
    )
    public static String spawnSuccessOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&cO Spawn ainda não está setado, utilize /setspawn para setar.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cSpawn is not set yet, use /setspawn to set it.", lang = Lang.EN_US)
            }
    )
    public static String spawnNotSet;
    @Values(
            {
                    @Value(value = "%prefix%&eSpawn setado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSpawn successfully set.", lang = Lang.EN_US)
            }
    )
    public static String spawnSetMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eIn %time% seconds you will be teleported to spawn.", lang = Lang.EN_US)
            }
    )
    public static String spawnTimeToTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou are already expecting a teleport.", lang = Lang.EN_US)
            }
    )
    public static String spawnInTeleport;

    //fly
    @Values(
            {
                    @Value(value = "%prefix%&eSeu fly foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour fly has been activated.", lang = Lang.EN_US)
            }
    )
    public static String flyActive;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu fly foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYour fly has been disabled.", lang = Lang.EN_US)
            }
    )
    public static String flyDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu fly.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone activated your fly.", lang = Lang.EN_US)
            }
    )
    public static String flyOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu fly.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone has disabled your fly.", lang = Lang.EN_US)
            }
    )
    public static String flyOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe %player% fly has been activated.", lang = Lang.EN_US)
            }
    )
    public static String flyActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe %player% fly has been disabled.", lang = Lang.EN_US)
            }
    )
    public static String flyDisabledOther;
    @Values(
            {
                    @Value(value = "%prefix%&cEste mundo está com fly desativado.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThis world is fly off.", lang = Lang.EN_US)
            }
    )
    public static String flyDisabledWorld;

    //online
    @Values(
            {
                    @Value(value = "%prefix%&eExiste %amount% players online.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThere are %amount% players online.", lang = Lang.EN_US)
            }
    )
    public static String onlineMessage;

    //tphere
    @Values(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso o(a) %player% para você.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have successfully teleported %player% to yourself.", lang = Lang.EN_US)
            }
    )
    public static String tphereTeleportedSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado do além.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou were teleported from beyond.", lang = Lang.EN_US)
            }
    )
    public static String tphereTeleportedOtherSuccess;

    //trash
    @Values(
            {
                    @Value(value = "&cLixeira", lang = Lang.PT_BR),
                    @Value(value = "&cTrash", lang = Lang.EN_US)
            }
    )
    public static String trashMenuName;

    @Values(
            {
                    @Value(value = "%prefix%&eSua velocidade foi setado para %value%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eHis speed has been set to %value%.", lang = Lang.EN_US)
            }
    )
    public static String speedSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eSua velocidade voltou para o padrão.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eHis speed has returned to default.", lang = Lang.EN_US)
            }
    )
    public static String speedRemove;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém setou sua velocidade para %value%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone set your speed to %value%.", lang = Lang.EN_US)
            }
    )
    public static String speedOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém setou sua velocidade para padrão.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSomeone set your speed to default.", lang = Lang.EN_US)
            }
    )
    public static String speedOtherRemove;
    @Values(
            {
                    @Value(value = "%prefix%&eA Velocidade do(a) %player% foi foi setado para %value%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player% Speed ​​has been set to %value%.", lang = Lang.EN_US)
            }
    )
    public static String speedSuccessOther;
    @Values(
            {
                    @Value(value = "%prefix%&eA Velocidade do(a) %player% voltou para o padrão.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e%player%'s Speed ​​has returned to default.", lang = Lang.EN_US)
            }
    )
    public static String speedRemoveOther;
    @Values(
            {
                    @Value(value = "%prefix%&eA Velocidade pode ser setado de 0 a 10.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eSpeed ​​can be set from 0 to 10.", lang = Lang.EN_US)
            }
    )
    public static String speedIncorrectValue;

    //hat
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de colocar um Chapéu.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou just put on a Hat.", lang = Lang.EN_US)
            }
    )
    public static String hatSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eFoi só encontrado ar em sua mão, coloque algum item!", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eIt was only found air in your hand, put some item!", lang = Lang.EN_US)
            }
    )
    public static String hatNotFound;

    //money
    @Values(
            {
                    @Value(value = "%prefix%&eVocê tem %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have %money% %unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneyMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% tem %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eThe %player% has %money% %unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneyMessageOther;
    @Values(
            {
                    @Value(value = "real", lang = Lang.PT_BR),
                    @Value(value = "bal", lang = Lang.EN_US)
            }
    )
    public static String moneySingular;
    @Values(
            {
                    @Value(value = "reais", lang = Lang.PT_BR),
                    @Value(value = "balance", lang = Lang.EN_US)
            }
    )
    public static String moneyPlural;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê pagou %money% %unity% para %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou paid %money% %unity% to %player%.", lang = Lang.EN_US)
            }
    )
    public static String moneyPay;
    @Values(
            {
                    @Value(value = "%prefix%&aVocê recebeu %money% %unity% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&aYou received %money% %unity% from %player%.", lang = Lang.EN_US)
            }
    )
    public static String moneyPayOther;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não pode pagar você mesmo.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou can't pay yourself.", lang = Lang.EN_US)
            }
    )
    public static String moneyPaySame;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê setou o dinheiro do %player% para %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eYou have set %player% money to %money% %unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneySet;
    @Values(
            {
                    @Value(value = "%prefix%&cAlguém do além setou seu dinheiro para %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cSomeone from beyond set their money to %money% %unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneySetOther;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê tirou %money% %unity% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou took %money% %unity% from %player%.", lang = Lang.EN_US)
            }
    )
    public static String moneyTake;
    @Values(
            {
                    @Value(value = "%prefix%&cAlguém do além tirou do seu dinheiro %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cSomeone from beyond took %money% %unity% of your money.", lang = Lang.EN_US)
            }
    )
    public static String moneyTakeOther;
    @Values(
            {
                    @Value(value = "%prefix%&aVocê deu %money% %unity% ao %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&aYou gave %money% %unity% to %player%.", lang = Lang.EN_US)
            }
    )
    public static String moneyAdd;
    @Values(
            {
                    @Value(value = "%prefix%&aAlguém do além adicionou ao seu dinheiro %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&aSomeone from beyond added %money% %unity% to your money.", lang = Lang.EN_US)
            }
    )
    public static String moneyAddOther;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem dinheiro suficiente, falta %money% %unity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou don't have enough money, you lack %money% %unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneyMissing;
    @Values(
            {
                    @Value(value = "%prefix%&eRank dos mais ricos - 10.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&eRank of the richest - 10.", lang = Lang.EN_US)
            }
    )
    public static String moneyTopMessage;
    @Values(
            {
                    @Value(value = "&9> &e%position% - %player%, &a%money% &e%unity%.", lang = Lang.PT_BR),
                    @Value(value = "&9> &e%position% - %player%, &a%money% &e%unity%.", lang = Lang.EN_US)
            }
    )
    public static String moneyTop;

    //invsee
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não pode ver o seu próprio inventário.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cYou cannot see your own inventory.", lang = Lang.EN_US)
            }
    )
    public static String invseeSameName;
    @Values(
            {
                    @Value(value = "%prefix%&cO player Saiu e por isso foi fechado o inventário.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThe player left and so the inventory was closed.", lang = Lang.EN_US)
            }
    )
    public static String invseePlayerLeave;

    //death messages
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %killer%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died for the %killer%.", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesPlayerKillPlayer;
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %entity%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died for the %entity%.", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesEntityKillPlayer;
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&cThe %player% died.", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesNothingKillPlayer;
    @Values(
            {
                    @Value(value = "&cCausa %cause% não está registrada em cause-replacer", lang = Lang.PT_BR),
                    @Value(value = "&cCause %cause% is not registered in cause-replacer", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesCauseNotExist;
    @Values(
            {
                    @Value(value = "AXOLOTL-Axolote|BEE-Abelha|BLAZE-Blaze|CAVE_SPIDER-Aranha da Caverna|COD-Bacalhau|CREEPER-Creeper|DRAGON_FIREBALL-Bola de Fogo|ENDER_DRAGON-Dragão do Fim|ENDERMAN-Enderman|ENDERMITE-Endermite|EVOKER-Invocador|GHAST-Ghast|GIANT-Zumbi Gigante|GUARDIAN-Guardião|HOGLIN-Hoglin|HUSK-Zumbi do Deserto|ILLUSIONER-Ilusionista|IRON_GOLEM-Golem de Ferro|MAGMA_CUBE-Cubo de Magma|PIGLIN-Piglin|PIGLIN_BRUTE-Piglin B|PIG_ZOMBIE-Porco Zumbi|PILLAGER-Saqueador|PUFFERFISH-Baiacu|RAVAGER-Devastador|SHULKER-Shulker|SHULKER_BULLET-Dardo de Shulker|SKELETON-Esqueleto|SLIME-Slime|SPIDER-Aranha|STRIDER-Lavagante|STRAY-Esqueleto Vagante|VEX-Fantasma|VINDICATOR-Vingador|WITCH-Bruxa|WITHER-Wither|WITHER_SKELETON-Esqueleto Wither|WITHER_SKULL-Cabeça do Wither|WOLF-Lobo|ZOGLIN-Zoglin|ZOMBIE-Zumbi|ZOMBIE_VILLAGER-AldeZumbi|ZOMBIFIED_PIGLIN-Piglin Zumbi", lang = Lang.PT_BR),
                    @Value(value = "AXOLOTL-Axolotl|BEE-Bee|BLAZE-Blaze|CAVE_SPIDER-SpiderCave|COD-Cod|CREEPER-Creeper|DRAGON_FIREBALL-Fireball|ENDER_DRAGON-EnderDragon|ENDERMAN-Enderman|ENDERMITE-Endermite|EVOKER-Evoker|GHAST-Ghast|GIANT-Giant|GUARDIAN-Guardian|HOGLIN-Hoglin|HUSK-Husk|ILLUSIONER-Illusioner|IRON_GOLEM-IronGolem|MAGMA_CUBE-MagmaCube|PIGLIN-Piglin|PIGLIN_BRUTE-Piglin B|PIG_ZOMBIE-ZumbiPig|PILLAGER-Pillager|PUFFERFISH-Pufferfish|RAVAGER-Ravager|SHULKER-Shulker|SHULKER_BULLET-ShulkerBullet|SKELETON-Esqueleto|SLIME-Slime|SPIDER-Spider|STRIDER-Strider|STRAY-Stray|VEX-Vex|VINDICATOR-Vindicator|WITCH-Witch|WITHER-Wither|WITHER_SKELETON-Wither skeleton|WITHER_SKULL-Skull Wither|WOLF-Wolf|ZOGLIN-Zoglin|ZOMBIE-Zumbie|ZOMBIE_VILLAGER-ZumbiVillager|ZOMBIFIED_PIGLIN-ZumbiePiglin", lang = Lang.EN_US)
            }
    )
    public static List<String> deathmessagesEntityReplacer;
    @Values(
            {
                    @Value(value = "|SUICIDE-%prefix%&cO %player% se suicidiou-se.|POISON-%prefix%&cO %player% Morreu envenenado.|STARVATION-%prefix%&cO %player% Morreu de fome.|FALL-%prefix%&cO %player% caiu de um lugar alto.|DROWNING-%prefix%&cO %player% morreu afogado.|PROJECTILE-%prefix%&cO %player% Tomou uma flechada.|FIRE_TICK-%prefix%&cO %player% Pegou fogo.|FIRE-%prefix%&cO %player% Morreu Queimado.|ARROWS-%prefix%&cO %player% Tomou uma flechada.|CACTUS-%prefix%&cO %player% Tentou abraçar um cacto.|ENTITY_EXPLOSION-%prefix%&cO %player% Explodiu.|LIGHTNING-%prefix%&cO %player% Recebeu uma descarga elétrica e morreu.|SUFFOCATION-%prefix%&cO %player% Morreu sufocado.|LAVA-%prefix%&cO %player% Achou que lava era água e foi dar um mergulho.|MAGIC-%prefix%&cO %player% Foi vítima de bruxaria das brabas.|WITHER-%prefix%&cO %player% Foi morto por um WitherBoss.|BLOCK_EXPLOSION-%prefix%&cO %player% Explodiu.|VOID-%prefix%&cO %player% Morreu.", lang = Lang.PT_BR),
                    @Value(value = "|SUICIDE-%prefix%&c%player% committed suicide.|POISON-%prefix%&c%player% He died of poison.|STARVATION-%prefix%&c%player% Starved to death.|FALL-%prefix%&c%player% fell from a high place.|DROWNING-%prefix%&c%player% he drowned.|PROJECTILE-%prefix%&c%player% Took an arrow.|FIRE_TICK-%prefix%&c%player% Caught fire.|FIRE-%prefix%&c%player% He died burnt.|ARROWS-%prefix%&c%player% Took an arrow.|CACTUS-%prefix%&c%player% Tried to hug a cactus.|ENTITY_EXPLOSION-%prefix%&cO %player% Blow up.|LIGHTNING-%prefix%&c%player% He received an electrical shock and died.|SUFFOCATION-%prefix%&c%player% suffocated to death.|LAVA-%prefix%&c%player% He thought lava was water and went for a swim.|MAGIC-%prefix%&c%player% He was a victim of witchcraft from the brabas.|WITHER-%prefix%&c%player% He was killed by a WitherBoss.|BLOCK_EXPLOSION-%prefix%&c%player% blow up.|VOID-%prefix%&c%player% Dead.", lang = Lang.EN_US)
            }
    )
    public static List<String> deathmessagesCauseReplacer;


    @Values(
            {
                    @Value(value = "%prefix%&e[+] %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e[+] %player%.", lang = Lang.EN_US)
            }
    )
    public static String messagesEnterMessage;
    @Values(
            {
                    @Value(value = "%prefix%&e[-] %player%.", lang = Lang.PT_BR),
                    @Value(value = "%prefix%&e[-] %player%.", lang = Lang.EN_US)
            }
    )
    public static String messagesLeaveMessage;

    @Values(
            {
                    @Value(value = "&e[Anuncio] &9%name%&e: %message%", lang = Lang.PT_BR),
                    @Value(value = "&e[Announcement] &9%name%&e: %message%", lang = Lang.EN_US)
            }
    )
    public static String announceSendAnnounce;

    @Values(
            {
                    @Value(value = "Token errado ou faltando, coloque certo para enviar ao discord, ou desative a opção de enviar ao chat do discord.", lang = Lang.PT_BR),
                    @Value(value = "Wrong or missing token, put it right to send to discord, or disable the option to send to discord chat.", lang = Lang.EN_US)
            }
    )
    public static String discordchatNoToken;
    @Values(
            {
                    @Value(value = "Chat do discord especificado não existe, coloque um certo ou desative!", lang = Lang.PT_BR),
                    @Value(value = "Specified discord chat does not exist, set one right or disable!", lang = Lang.EN_US)
            }
    )
    public static String discordchatNoChatId;
    @Values(
            {
                    @Value(value = "**`%group% %player%`**: %message%", lang = Lang.PT_BR),
                    @Value(value = "**`%group% %player%`**: %message%", lang = Lang.EN_US)
            }
    )
    public static String discordchatMessageToDiscordPattern;
    @Values(
            {
                    @Value(value = "&e[Disc] %player%: %message%", lang = Lang.PT_BR),
                    @Value(value = "&e[Disc] %player%: %message%", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordToServerPattern;
    @Values(
            {
                    @Value(value = "%author%, Sua mensagem não foi enviada ao servidor pois tinha mais que %lenght% caracteres", lang = Lang.PT_BR),
                    @Value(value = "%author%, Your message was not sent to the server as it was longer than %length% characters", lang = Lang.EN_US)
            }
    )
    public static String discordchatMessageNotSendToServer;
    @Values(
            {
                    @Value(value = "%player% Entrou no servidor.", lang = Lang.PT_BR),
                    @Value(value = "%player% Joined the server.", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordSendLoginMessage;
    @Values(
            {
                    @Value(value = "%player% Saiu do servidor.", lang = Lang.PT_BR),
                    @Value(value = "%player% Left the server.", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordSendLeaveMessage;
    @Values(
            {
                    @Value(value = "Online: %online% players, Tempo Online: %online_time%, Atualizado: %time%", lang = Lang.PT_BR),
                    @Value(value = "Online: %online% players, Online time: %online_time%, Updated: %time%", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordTopic;
}
