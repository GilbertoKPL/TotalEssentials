package github.gilbertokpl.total.util

import java.util.logging.Filter
import java.util.logging.LogRecord


class Filter : Filter {

    val list = listOf(
        "issued server command: /mudarsenha",
        "issued server command: /changepass",
        "issued server command: /login",
        "issued server command: /logar",
        "issued server command: /register",
        "issued server command: /registrar"
    )

    override fun isLoggable(record: LogRecord): Boolean {
        var recordable = true

        for (i in list) {
            if (record.message.contains(i)) {
                recordable = false
                break
            }
        }

        return recordable
    }
}