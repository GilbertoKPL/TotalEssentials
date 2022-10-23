package github.gilbertokpl.base.external.utils

import github.gilbertokpl.base.external.BasePlugin
import github.gilbertokpl.base.internal.utils.InternalTime

class Time(lf: BasePlugin) {
    private val timeInstance = InternalTime(lf)

    fun getOnlineTime(): Long {
        return timeInstance.getOnlineTime()
    }

    fun getCurrentDate(): String {
        return timeInstance.getCurrentDate()
    }

    fun convertStringToMillis(timeString: String): Long {
        return timeInstance.convertStringToMillis(timeString)
    }

    fun convertMillisToString(time: Long, short: Boolean): String {
        return timeInstance.convertMillisToString(time, short)
    }

}