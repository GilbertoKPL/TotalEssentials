package github.gilbertokpl.core.external.utils

import github.gilbertokpl.core.external.CorePlugin
import github.gilbertokpl.core.internal.utils.InternalTime

class Time(lf: CorePlugin) {
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