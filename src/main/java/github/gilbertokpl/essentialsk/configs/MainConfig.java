package github.gilbertokpl.essentialsk.configs;

import java.util.List;

public class MainConfig {
    private static final MainConfig instance = createInstance();
    public static MainConfig createInstance() {
        return new MainConfig();
    }
    public static MainConfig getInstance() {
        return instance;
    }

    public String generalSelectedLang = "en_US";

    public String generalServerMame;

    public String databaseType;

    public String databaseSqlIp;

    public String databaseSqlPort;

    public String databaseSqlUsername;

    public String databaseSqlDatabase;

    public String databaseSqlPassword;

    public Boolean kitsActivated;

    public Boolean kitsUseShortTime;

    public Boolean kitsDropItemsInCatch;

    public Boolean kitsEquipArmorInCatch;

    public Boolean nicksActivated;

    public Boolean nicksCanPlayerHaveSameNick;

    public List<String> nicksBlockedNicks;

    public Boolean homesActivated;

    public int homesDefaultLimitHomes;

    public int homesTimeToTeleport;

    public List<String> homesBlockWorlds;

    public Boolean warpsActivated;

    public int warpsTimeToTeleport;

    public Boolean tpActivated;

    public Boolean tpaActivated;

    public int tpaTimeToAccept;

    public int tpaTimeToTeleport;

    public Boolean echestActivated;

    public Boolean gamemodeActivated;

    public Boolean vanishActivated;

    public Boolean feedActivated;

    public Boolean feedNeedEatBelow;

    public Boolean healActivated;

    public Boolean healNeedHealBelow;

    public Boolean lightActivated;

    public Boolean backActivated;

    public List<String> backDisabledWorlds;

    public Boolean flyActivated;

    public List<String> flyDisabledWorlds;

    public Boolean spawnActivated;

    public Boolean spawnSendToSpawnOnLogin;

    public int spawnTimeToTeleport;

    public Boolean antibugsBlockBed;

    public Boolean antibugsBlockClimbingOnVehicles;

    public Boolean antibugsBlockNametag;

    public Boolean antibugsBlockPlayerGoToNetherCeiling;

    public Boolean antibugsBlockPlayerTeleportPortal;

    public Boolean antibugsBlockCreatePortal;

    public Boolean antibugsBlockMobCatch;

    public Boolean antibugsBlockGoingEdgeEnderpearl;

    public List<String> antibugsBlockCmds;

    public Boolean containersBlockShiftEnable;

    public List<String> containersBlockShift;

    public Boolean containersBlockOpenEnable;

    public List<String> containersBlockOpen;

    public Boolean addonsInfinityAnvil;

    public Boolean addonsBlockPlayerGoToVoid;

    public Boolean addonsBlockExplodeItems;

    public Boolean addonsBlockPlayerBreakPlantationFall;

    public Boolean addonsBlockPropagationFire;

    public Boolean addonsColorInAnvil;

    public Boolean addonsColorInSign;

    public Boolean addonsColorInChat;

    public Boolean onlineActivated;

    public Boolean onlineCountRemoveVanish;

    public Boolean announceActivated;

    public int announceCooldown;

    public Boolean motdEnabled;

    public List<String> motdListMotd;

    public Boolean announcementsEnabled;

    public int announcementsTime;

    public Boolean deathmessagesEnabled;

    public Boolean addonsPlayerPreventLoseXp;

    public Boolean addonsDisableRain;

}
