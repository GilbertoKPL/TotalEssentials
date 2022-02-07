package github.gilbertokpl.essentialsk.config.files;

import github.gilbertokpl.essentialsk.config.annotations.Value;
import github.gilbertokpl.essentialsk.config.annotations.Values;
import github.gilbertokpl.essentialsk.config.lang.Lang;

import java.util.List;

public class LangConfig {

    @Values(
            {
                    @Value(value = "&a[Server]&r", lang = Lang.PT_BR),
                    @Value(value = "&a[Server]&r", lang = Lang.EN_US)
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
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalNotPerm;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem permissão para fazer isso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalNotPermAction;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse player não está online.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPlayerNotOnline;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse player não existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
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
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalSpecialCaracteresDisabled;
    @Values(
            {
                    @Value(value = "%prefix%&cFalta %time% para executar novamente esse comando.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalCooldownMoreTime;
    @Values(
            {
                    @Value(value = "%prefix%&cAdicione Vault para funcionar tudo do plugin!", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalVaultNotExist;
    @Values(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, spawn desativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalWorldNotExistSpawn;
    @Values(
            {
                    @Value(value = "%prefix%&cMundo %world% não existe, warp %warp% desativada.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalWorldNotExistWarp;
    @Values(
            {
                    @Value(value = "%prefix%&eConfig reninciada com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalConfigReload;
    @Values(
            {
                    @Value(value = "&aIP: &f%ip%|&aOS: &f%os%, &aversão &f%os_version%|&aNome CPU: &f%cpu_name%|&aNúcleos: &f%cores% &anúcleos|&aNúcleos Liberados: &f%cores_server% &anúcleos|&aUso do CPU : &f%cpu_usage% %|&aClock CPU: &f%cpu_clock_min% / %cpu_clock_max% mhz|&aMemória: &f%used_mem% / %max_mem% mb|&aMemória Liberada: &f%used_server_mem% / %max_server_mem% mb|&aHD Usado: &f%used_hd%/%max_hd% mb|&aGPU: &f%gpu%", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> generalHostConfigInfo;
    @Values(
            {
                    @Value(value = "%prefix%&ePegando informações da host, Aguarde!.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalHostWait;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin não encontrado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPluginNotFound;
    @Values(
            {
                    @Value(value = "%prefix%&ePlugin descarregado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPluginUnload;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin descarregado com problemas.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPluginUnloadProblems;
    @Values(
            {
                    @Value(value = "%prefix%&ePlugin carregado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPluginLoad;
    @Values(
            {
                    @Value(value = "%prefix%&cPlugin carregado com problemas.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalPluginLoadProblems;
    @Values(
            {
                    @Value(value = "Salvando informações.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalSaveDataMessage;
    @Values(
            {
                    @Value(value = "Salvo com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String generalSaveDataSuccess;

    //kits
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além deu o kit `%kit%&e` para você.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsNotExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse kit já existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsExist;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi criado com sucesso, para edita-lo utilize /editarkit %kit%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCreateKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &edeletado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsDelKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eKit %kit% &efoi editado com sucesso!", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eTempo do kit setado para %time%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitTime;
    @Values(
            {
                    @Value(value = "%prefix%&eEditKit - Items", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryItemsName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar todos os items", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryItemsLore;
    @Values(
            {
                    @Value(value = "&eEditKit - Tempo", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o tempo de espera|&fPara pegar novamente", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryTimeLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o tempo de espera para pegar novamente o kit, formato tempo/unidade -> Exemplo 30s, unidades -> [s,m,h,d]", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryTimeMessage;
    @Values(
            {
                    @Value(value = "&eEditKit - Nome", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o nome do kit", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryNameLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo nome fictício para seu kit pode utilizar cores, caso queira mudar o nome criado terá que criar um novo kit...", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryNameMessage;
    @Values(
            {
                    @Value(value = "&eEditKit - Peso", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryWeightName;
    @Values(
            {
                    @Value(value = "&fClique para editar|&fAo clicar você poderá|&fEditar o peso do kit", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsEditKitInventoryWeightLore;
    @Values(
            {
                    @Value(value = "%prefix%&eDigite no chat qual será o novo peso, lembre-se quanto maior o peso mais na frente o kit aparecerá na GUI.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsEditKitInventoryWeightMessage;
    @Values(
            {
                    @Value(value = "&eVoltar página", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconBackName;
    @Values(
            {
                    @Value(value = "&ePróxima página", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconNextName;
    @Values(
            {
                    @Value(value = "&eKit &f%kitrealname%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryItemsName;
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome do kit 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsNameLength;
    @Values(
            {
                    @Value(value = "&fNome do kit -> %realname%|&eClique para ver o kit", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsInventoryItemsLore;
    @Values(
            {
                    @Value(value = "&cFalta %time% para pegar seu kit novamente", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCatchMessage;
    @Values(
            {
                    @Value(value = "&cClique aqui para pegar o seu kit", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCatchIcon;
    @Values(
            {
                    @Value(value = "&cVocê não consegue pegar esse kit", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCatchIconNotCatch;
    @Values(
            {
                    @Value(value = "&eClique aqui para editar o kit", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsInventoryIconEditKitName;
    @Values(
            {
                    @Value(value = "%prefix%&eCrie seu kit com /criarkit (nome do kit).", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsNotExistKits;

    @Values(
            {
                    @Value(value = "&cVocê não tem permissão|", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsCatchIconLoreNotPerm;

    @Values(
            {
                    @Value(value = "&fFalta|&f%time%|&fPara pegar seu kit", lang = Lang.PT_BR),
                    @Value(value = "a|b", lang = Lang.EN_US)
            }
    )
    public static List<String> kitsCatchIconLoreTime;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de kits %kits%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsList;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê pegou o kit `%kit%&e` com sucesso", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCatchSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cLibere mais %slots% slots para pegar esse kit!", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsCatchNoSpace;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê deu o %kit% &epara o(a) %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsGiveKitMessageOther;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além deu o kit `%kit%&e` para você.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String kitsGiveKitMessage;

    //time
    @Values(
            {
                    @Value(value = "segundos", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeSeconds;
    @Values(
            {
                    @Value(value = "segundo", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeSecond;
    @Values(
            {
                    @Value(value = "minutos", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeMinutes;
    @Values(
            {
                    @Value(value = "minuto", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeMinute;
    @Values(
            {
                    @Value(value = "horas", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeHours;
    @Values(
            {
                    @Value(value = "hora", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeHour;
    @Values(
            {
                    @Value(value = "dias", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeDays;
    @Values(
            {
                    @Value(value = "dia", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeDay;
    @Values(
            {
                    @Value(value = "s", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeSecondShort;
    @Values(
            {
                    @Value(value = "m", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeMinuteShort;
    @Values(
            {
                    @Value(value = "h", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeHourShort;
    @Values(
            {
                    @Value(value = "d", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String timeDayShort;

    //nicks
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse nick está proibido de utilizar.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksBlocked;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse nick já existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksExist;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar seu nick de %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar seu nick.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickRemovedSuccess;
    @Values(
            {
                    @Value(value = "prefix%&eVocê acaba de setar o nick do player para %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de setar o seu nick de %nick%&e.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickOtherPlayerSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar o nick do player.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickRemovedOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além acaba de apagar seu nick.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickRemovedOtherPlayerSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nick para remover.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickAlreadyOriginal;
    @Values(
            {
                    @Value(value = "%prefix%&cEle não tem nick para remover.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String nicksNickAlreadyOriginalOther;

    //home
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da home é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa home já existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesNameAlreadyExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa home não existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesNameDontExist;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para %home%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesSendTimeToTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar sua home %home%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de apagar uma home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeOtherRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar sua home %home%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeOtherCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê não pode criar mais homes você já alcançou seu limite de %limit%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeLimitCreated;
    @Values(
            {
                    @Value(value = "%prefix%&cEsse mundo está bloqueado de criar homes", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeWorldBlocked;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de homes -> %list%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeList;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de homes do %player% -> %list%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesHomeOtherList;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para sua home %home%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesTeleported;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para home %home% do %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesTeleportedOther;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String homesInTeleport;

    //warps
    @Values(
            {
                    @Value(value = "%prefix%&cTamanho maximo do nome da warp é de 16 caracteres.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsNameLength;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa warp já existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsNameAlreadyExist;
    @Values(
            {
                    @Value(value = "%prefix%&cEssa warp não existe.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsNameDontExist;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para warp %warp%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsSendTimeToTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso para warp %warp%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleported;
    @Values(
            {
                    @Value(value = "%prefix%&eLista de warps -> %list%", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsWarpList;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de criar uma warp chamada %warp%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsWarpCreated;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de deleter uma warp chamada %warp%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsWarpRemoved;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsInTeleport;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado para o warp %warp% do além.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleportedOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% foi teleportado para warp %warp% com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String warpsTeleportedOtherSuccess;

    //tp
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado com sucesso", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpTeleportedSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê foi teleportado do além", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )

    public static String tpTeleportedOtherSuccess;

    //tpa
    @Values(
            {
                    @Value(value = "%prefix%&eVocê mandou com sucesso o pedido de teleporte para %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaSendSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de receber um pedido de teleporte de %player%, você tem %time% segundos para aceita-lo com /tpaccept ou rejeita-lo com /tpdeny.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaOtherReceived;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê acabou de enviar um TPA aguarde para enviar novamente.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaAlreadySend;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para aceitar.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaNotAnyRequest;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhum pedido para cancelar.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaNotAnyRequestToDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de aceitar o pedido de %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando em %time% segundos.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&eO %player% acaba de aceitar seu pedido de teleporte, teleportando.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherNoDelayAccepted;
    @Values(
            {
                    @Value(value = "%prefix%&cO player já tem o pedido ativo de outra pessoa aguarde.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaAlreadyInAccept;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de cancelar o pedido de %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu pedido acaba de ser cancelado pelo(a) %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherDeny;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu pedido de TPA foi cancelado porque o %player% não aceitou.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaRequestOtherDenyTime;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não pode enviar tpa para você mesmo.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tpaSameName;

    //echest
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir seu enderChest.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String echestSendSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de abrir o enderChest de %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String echestSendOtherSuccess;

    //gamemode
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acabou de entrar no gameMode %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String gamemodeUseSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguem do além setou seu gameMode de %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String gamemodeUseOtherSuccess;
    @Values(
            {
                    @Value(value = "%prefix%&cVocê já está nesse gamemode.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSameGamemode;
    @Values(
            {
                    @Value(value = "%prefix%&cEle já está nesse gamemode.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSameOtherGamemode;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê acaba de setar o gamemode do(a) %player% para %gamemode%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String gamemodeSendSuccessOtherMessage;

    //vanish
    @Values(
            {
                    @Value(value = "%prefix%&eSeu vanish foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendActive;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu vanish foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu vanish.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu vanish.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eO Vanish do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String vanishSendDisabledOther;

    //feed
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de comida.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String feedSendMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê já está cheio seu guloso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String feedSendFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de comida.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String feedSendOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO player já está com a barra de comida cheia.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String feedSendOtherFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de comida do %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String feedSendSuccessOtherMessage;

    //heal
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso sua barra de vida.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String healSendMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê já está com vida cheia.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String healSendFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém do além encheu sua barra de vida.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String healSendOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eO player já está com vida cheio.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String healSendOtherFullMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê encheu com sucesso a barra de vida do %player%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String healSendSuccessOtherMessage;

    //light
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendActive;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou sua Luz.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou sua Luz.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eA Luz do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String lightSendDisabledOther;

    //back
    @Values(
            {
                    @Value(value = "%prefix%&cVocê não tem nenhuma localização salva para voltar.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String backSendNotToBack;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê voltou a sua ultima localização.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String backSendSuccess;

    //spawn
    @Values(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém teleportou você para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eVocê teleportou com sucesso o(a) %player% para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendSuccessOtherMessage;
    @Values(
            {
                    @Value(value = "%prefix%&cO Spawn ainda não está setado, utilize /setspawn para setar.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendNotSet;
    @Values(
            {
                    @Value(value = "%prefix%&eSpawn setado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendSetMessage;
    @Values(
            {
                    @Value(value = "%prefix%&eEm %time% segundos você será teleportado para o spawn.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendTimeToTeleport;
    @Values(
            {
                    @Value(value = "prefix%&cVocê já está esperando um teleporte.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String spawnSendInTeleport;

    //fly
    @Values(
            {
                    @Value(value = "%prefix%&eSeu fly foi ativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendActive;
    @Values(
            {
                    @Value(value = "%prefix%&eSeu fly foi desativado com sucesso.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém ativou seu fly.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendOtherActive;
    @Values(
            {
                    @Value(value = "%prefix%&eAlguém desativou seu fly.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendOtherDisable;
    @Values(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi ativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendActivatedOther;
    @Values(
            {
                    @Value(value = "%prefix%&eO fly do(a) %player% foi desativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendDisabledOther;
    @Values(
            {
                    @Value(value = "%prefix%&cEste mundo está com fly desativado.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String flySendDisabledWorld;

    //online
    @Values(
            {
                    @Value(value = "%prefix%&eExiste %amount% players online.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String onlineSendOnline;

    //death messages
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %killer%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesPlayerKillPlayer;
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu para o(a) %entity%.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesEntityKillPlayer;
    @Values(
            {
                    @Value(value = "%prefix%&cO %player% morreu.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesNothingKillPlayer;
    @Values(
            {
                    @Value(value = "&cCausa %cause% não está registrada em cause-replacer", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String deathmessagesCauseNotExist;
    @Values(
            {
                    @Value(value = "AXOLOTL-Axolote|BEE-Abelha|BLAZE-Blaze|CAVE_SPIDER-Aranha da Caverna|COD-Bacalhau|CREEPER-Creeper|DRAGON_FIREBALL-Bola de Fogo|ENDER_DRAGON-Dragão do Fim|ENDERMAN-Enderman|ENDERMITE-Endermite|EVOKER-Invocador|GHAST-Ghast|GIANT-Zumbi Gigante|GUARDIAN-Guardião|HOGLIN-Hoglin|HUSK-Zumbi do Deserto|ILLUSIONER-Ilusionista|IRON_GOLEM-Golem de Ferro|MAGMA_CUBE-Cubo de Magma|PIGLIN-Piglin|PIGLIN_BRUTE-Piglin B|PIG_ZOMBIE-Porco Zumbi|PILLAGER-Saqueador|PUFFERFISH-Baiacu|RAVAGER-Devastador|SHULKER-Shulker|SHULKER_BULLET-Dardo de Shulker|SKELETON-Esqueleto|SLIME-Slime|SPIDER-Aranha|STRIDER-Lavagante|STRAY-Esqueleto Vagante|VEX-Fantasma|VINDICATOR-Vingador|WITCH-Bruxa|WITHER-Wither|WITHER_SKELETON-Esqueleto Wither|WITHER_SKULL-Cabeça do Wither|WOLF-Lobo|ZOGLIN-Zoglin|ZOMBIE-Zumbi|ZOMBIE_VILLAGER-AldeZumbi|ZOMBIFIED_PIGLIN-Piglin Zumbi", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static List<String> deathmessagesEntityReplacer;
    @Values(
            {
                    @Value(value = "|SUICIDE-%prefix%&cO %player% se suicidiou-se.|POISON-%prefix%&cO %player% Morreu envenenado.|STARVATION-%prefix%&cO %player% Morreu de fome.|FALL-%prefix%&cO %player% caiu de um lugar alto.|DROWNING-%prefix%&cO %player% morreu afogado.|PROJECTILE-%prefix%&cO %player% Tomou uma flechada.|FIRE_TICK-%prefix%&cO %player% Pegou fogo.|FIRE-%prefix%&cO %player% Morreu Queimado.|ARROWS-%prefix%&cO %player% Tomou uma flechada.|CACTUS-%prefix%&cO %player% Tentou abraçar um cacto.|ENTITY_EXPLOSION-%prefix%&cO %player% Explodiu.|LIGHTNING-%prefix%&cO %player% Recebeu uma descarga elétrica e morreu.|SUFFOCATION-%prefix%&cO %player% Pegou CoronaVairus e morreu.|LAVA-%prefix%&cO %player% Achou que lava era água e foi dar um mergulho.|MAGIC-%prefix%&cO %player% Foi vítima de bruxaria das brabas.|WITHER-%prefix%&cO %player% Foi morto por um WitherBoss.|BLOCK_EXPLOSION-%prefix%&cO %player% Explodiu.|VOID-%prefix%&cO %player% Morreu.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
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
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String announceSendAnnounce;

    @Values(
            {
                    @Value(value = "Token errado ou faltando, coloque certo para enviar ao discord, ou desative a opção de enviar ao chat do discord.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatNoToken;
    @Values(
            {
                    @Value(value = "Chat do discord especificado não existe, coloque um certo ou desative!.", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatNoChatId;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatMessageToDiscordPattern;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordToServerPattern;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatMessageNotSendToServer;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordSendLoginMessage;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordSendLeaveMessage;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String discordchatDiscordTopic;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tphereTeleportedSuccess;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String tphereTeleportedOtherSuccess;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String trashMenuName;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendSuccess;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendRemove;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendOtherSuccess;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendOtherRemove;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendSuccessOther;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendRemoveOther;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String speedSendIncorrectValue;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String hatSendSuccess;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String hatSendNotFound;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String invseeSameName;
    @Values(
            {
                    @Value(value = "TESTE", lang = Lang.PT_BR),
                    @Value(value = "TEST", lang = Lang.EN_US)
            }
    )
    public static String invseePlayerLeave;
}
