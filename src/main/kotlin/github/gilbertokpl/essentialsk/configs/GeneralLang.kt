package github.gilbertokpl.essentialsk.configs

import github.gilbertokpl.essentialsk.manager.IInstance

class GeneralLang {

    var generalOnlyPlayerCommand = ""

    var generalNotPerm = ""

    var generalPlayerNotOnline = ""

    var generalCommandsUsage = ""

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

    companion object : IInstance<GeneralLang> {
        private val instance = createInstance()
        override fun createInstance(): GeneralLang = GeneralLang()
        override fun getInstance(): GeneralLang {
            return instance
        }
    }
}