package github.gilbertokpl.total.util

import java.util.logging.Filter
import java.util.logging.LogRecord


class Filter : Filter {
    override fun isLoggable(record: LogRecord): Boolean {
        return !(record.message.contains("issued server command: /login") || !record.message.contains("issued server command: /register") || !record.message.contains("issued server command: /registrar") || !record.message.contains("issued server command: /logar"))
    }
}