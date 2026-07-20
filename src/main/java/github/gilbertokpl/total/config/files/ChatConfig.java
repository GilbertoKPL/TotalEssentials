package github.gilbertokpl.total.config.files;

import github.gilbertokpl.core.config.annotations.Comment;
import github.gilbertokpl.core.config.annotations.ConfigPattern;
import github.gilbertokpl.core.config.annotations.MultiComment;
import github.gilbertokpl.core.config.annotations.RootComment;
import github.gilbertokpl.core.config.annotations.RootComments;
import github.gilbertokpl.core.config.types.LangTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ConfigPattern(name = "ChatConfig")
public class ChatConfig {

    @RootComments({
            @RootComment(primaryAnnotations = "Configurações gerais do chat nativo.", lang = LangTypes.PT_BR),
            @RootComment(primaryAnnotations = "Native chat general settings.", lang = LangTypes.EN_US)
    })
    public static Boolean generalActivated = true;
    public static String generalDefaultChannel = "Local";
    public static List<String> generalFilterWords = Collections.singletonList("exemplo");

    @RootComments({
            @RootComment(primaryAnnotations = "Canal global compartilhado pelo Velocity/BungeeCord.", lang = LangTypes.PT_BR),
            @RootComment(primaryAnnotations = "Global channel shared through Velocity/BungeeCord.", lang = LangTypes.EN_US)
    })
    public static Boolean networkActivated = true;
    public static String networkColor = "&6";
    public static String networkChatColor = "&f";
    public static Boolean networkMutable = true;
    public static Boolean networkFilter = true;
    public static Boolean networkAutoJoin = true;
    public static Boolean networkDefault = false;
    public static Integer networkDistance = 0;
    public static Integer networkCooldown = 3;
    public static Boolean networkBungeecord = true;
    public static List<String> networkAliases = Arrays.asList("g", "global");
    public static String networkPermission = "";
    public static String networkSpeakPermission = "";
    public static String networkChannelPrefix = "&f[&6Rede&f]";
    @MultiComment({
            @Comment(annotations = "Aceita placeholders internos, Vault e PlaceholderAPI.", lang = LangTypes.PT_BR),
            @Comment(annotations = "Supports internal, Vault and PlaceholderAPI placeholders.", lang = LangTypes.EN_US)
    })
    public static String networkFormat = "{channel_prefix} {magnata} {vault_prefix}{player_displayname}{vault_suffix}&7: {chat_color}{message}";

    @RootComments({
            @RootComment(primaryAnnotations = "Canal local limitado por distância.", lang = LangTypes.PT_BR),
            @RootComment(primaryAnnotations = "Local distance-limited channel.", lang = LangTypes.EN_US)
    })
    public static Boolean localActivated = true;
    public static String localColor = "&e";
    public static String localChatColor = "&f";
    public static Boolean localMutable = true;
    public static Boolean localFilter = true;
    public static Boolean localAutoJoin = true;
    public static Boolean localDefault = true;
    public static Integer localDistance = 230;
    public static Integer localCooldown = 0;
    public static Boolean localBungeecord = false;
    public static List<String> localAliases = Arrays.asList("l", "local");
    public static String localPermission = "";
    public static String localSpeakPermission = "";
    public static String localChannelPrefix = "&f[&eLocal&f]";
    public static String localFormat = "{channel_prefix} {magnata} {vault_prefix}{player_displayname}{vault_suffix}&7: {chat_color}{message}";

    @RootComments({
            @RootComment(primaryAnnotations = "Mensagens privadas locais e entre servidores.", lang = LangTypes.PT_BR),
            @RootComment(primaryAnnotations = "Local and cross-server private messages.", lang = LangTypes.EN_US)
    })
    public static Boolean tellActivated = true;
    public static Boolean tellFilter = true;
    public static List<String> tellAliases = Arrays.asList("msg", "w", "whisper", "sussurrar");
    public static List<String> tellReplyAliases = Collections.singletonList("r");
    public static String tellPermission = "totalessentials.commands.tell";
    public static String tellReplyPermission = "totalessentials.commands.tell";
}
