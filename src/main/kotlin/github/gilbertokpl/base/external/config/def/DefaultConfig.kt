package github.gilbertokpl.base.external.config.def

interface DefaultConfig {
    val generalSelectedLang: String
    val generalServerName: String
    val generalAutoUpdate: Boolean
    val databaseType: String
    val databaseSqlIp: String
    val databaseSqlPort: String
    val databaseSqlUsername: String
    val databaseSqlDatabase: String
    val databaseSqlPassword: String
}