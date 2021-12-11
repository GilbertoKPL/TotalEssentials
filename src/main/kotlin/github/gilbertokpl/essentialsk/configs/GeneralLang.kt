package github.gilbertokpl.essentialsk.configs

import github.gilbertokpl.essentialsk.manager.IInstance

class GeneralLang {

    var generalOnlyPlayerCommand = ""

    var generalNotPerm = ""

    var generalPlayerNotOnline = ""

    var generalCommandsUsage = ""

    var generalCommandsUsageList = ""

    var generalSpecialCaracteresDisabled = ""

    var generalPlayerNotExist = ""

    var generalSendingInfoToDb = ""

    var kitsNotExist = ""

    var kitsExist = ""

    var kitsCreateKitSuccess = ""

    var kitsDelKitSuccess = ""

    var kitsEditKitSuccess = ""

    var kitsEditKitTime = ""

    var kitsEditKitInventoryItemsName = ""

    var kitsEditKitInventoryItemsLore: List<String> = emptyList()

    var kitsEditKitInventoryTimeName = ""

    var kitsEditKitInventoryTimeLore: List<String> = emptyList()

    var kitsEditKitInventoryNameName = ""

    var kitsEditKitInventoryNameLore: List<String> = emptyList()

    var kitsEditKitInventoryTimeMessage = ""

    var kitsEditKitInventoryNameMessage = ""

    var kitsInventoryIconBackName = ""

    var kitsInventoryIconNextName = ""

    var kitsInventoryItemsName = ""

    var kitsNameLength = ""

    var kitsInventoryItemsLore: List<String> = emptyList()

    var kitsCatchMessage = ""

    var kitsCatchIcon = ""

    var kitsCatchIconNotCatch = ""

    var kitsInventoryIconEditKitName = ""

    var kitsNotExistKits = ""

    var kitsCatchIconLoreNotPerm: List<String> = emptyList()

    var kitsCatchIconLoreTime: List<String> = emptyList()

    var kitsList = ""

    var kitsCatchSuccess = ""

    var kitsCatchNoSpace = ""

    var kitsGiveKitMessage = ""

    var timeSeconds = ""

    var timeSecond = ""

    var timeMinutes = ""

    var timeMinute = ""

    var timeHours = ""

    var timeHour = ""

    var timeDays = ""

    var timeDay = ""

    var timeSecondShort = ""

    var timeMinuteShort = ""

    var timeHourShort = ""

    var timeDayShort = ""

    var nicksNameLength = ""

    var nicksBlocked = ""

    var nicksExist = ""

    var nicksNickSuccess = ""

    var nicksNickRemovedSuccess = ""

    var nicksNickOtherSuccess = ""

    var nicksNickOtherPlayerSuccess = ""

    var nicksNickRemovedOtherSuccess = ""

    var nicksNickRemovedOtherPlayerSuccess = ""

    var nicksNickAlreadyOriginal = ""

    var nicksNickAlreadyOriginalOther = ""

    var homesNameLength = ""

    var homesNameAlreadyExist = ""

    var homesNameDontExist = ""

    var homesTimeToTeleport = ""

    var homesHomeRemoved = ""

    var homesHomeOtherRemoved = ""

    var homesHomeCreated = ""

    var homesHomeOtherCreated = ""

    var homesHomeLimitCreated = ""

    var homesHomeWorldBlocked = ""

    var homesTeleported = ""

    var homesTeleportedOther = ""

    var homesInTeleport = ""

    var homesHomeList = ""

    var homesHomeOtherList = ""

    var warpsNameLength = ""

    var warpsNameAlreadyExist = ""

    var warpsNameDontExist = ""

    var warpsTimeToTeleport = ""

    var warpsTeleported = ""

    var warpsWarpList = ""

    var warpsWarpCreated = ""

    var warpsWarpRemoved = ""

    companion object : IInstance<GeneralLang> {
        private val instance = createInstance()
        override fun createInstance(): GeneralLang = GeneralLang()
        override fun getInstance(): GeneralLang {
            return instance
        }
    }
}