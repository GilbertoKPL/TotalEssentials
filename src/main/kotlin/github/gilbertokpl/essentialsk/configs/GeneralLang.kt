package github.gilbertokpl.essentialsk.configs

import github.gilbertokpl.essentialsk.manager.IInstance

class GeneralLang {

    var generalOnlyPlayerCommand = ""

    var generalNotPerm = ""

    var generalNotPermAction = ""

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

    var homesSendTimeToTeleport = ""

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

    var warpsSendTimeToTeleport = ""

    var warpsTeleported = ""

    var warpsWarpList = ""

    var warpsWarpCreated = ""

    var warpsWarpRemoved = ""

    var tpTeleportedSuccess = ""

    var tpTeleportedOtherSuccess = ""

    var tpaSendSuccess = ""

    var tpaOtherReceived = ""

    var tpaAlreadySend = ""

    var tpaNotAnyRequest = ""

    var tpaRequestAccepted = ""

    var tpaRequestOtherAccepted = ""

    var tpaRequestOtherNoDelayAccepted = ""

    var tpaAlreadyInAccept = ""

    var tpaRequestDeny = ""

    var tpaRequestOtherDeny = ""

    var tpaSameName = ""

    var echestSendSuccess = ""

    var echestSendOtherSuccess = ""

    var gamemodeUseSuccess = ""

    var gamemodeUseOtherSuccess = ""

    var gamemodeSameGamemode = ""

    var gamemodeSameOtherGamemode = ""

    var gamemodeSendSuccessOtherMessage = ""

    var vanishSendActive = ""

    var vanishSendDisable = ""

    var vanishSendOtherActive = ""

    var vanishSendOtherDisable = ""

    var vanishSendActivatedOther = ""

    var vanishSendDisabledOther = ""

    var feedSendMessage = ""

    var feedSendFullMessage = ""

    var feedSendOtherMessage = ""

    var feedSendOtherFullMessage = ""

    var feedSendSuccessOtherMessage = ""

    var healSendMessage = ""

    var healSendFullMessage = ""

    var healSendOtherMessage = ""

    var healSendOtherFullMessage = ""

    var healSendSuccessOtherMessage = ""

    var lightSendActive = ""

    var lightSendDisable = ""

    var lightSendOtherActive = ""

    var lightSendOtherDisable = ""

    var lightSendActivatedOther = ""

    var lightSendDisabledOther = ""

    var backSendNotToBack = ""

    var backSendSuccess = ""

    var spawnSendMessage = ""

    var spawnSendOtherMessage = ""

    var spawnSendSucessOtherMessage = ""

    var spawnSendNotSet = ""

    var spawnSendSetMessage = ""

    var spawnSendTimeToTeleport = ""

    var spawnSendInTeleport = ""

    companion object : IInstance<GeneralLang> {
        private val instance = createInstance()
        override fun createInstance(): GeneralLang = GeneralLang()
        override fun getInstance(): GeneralLang {
            return instance
        }
    }
}