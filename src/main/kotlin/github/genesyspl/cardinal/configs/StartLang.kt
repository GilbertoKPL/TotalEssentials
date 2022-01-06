package github.genesyspl.cardinal.configs

import github.genesyspl.cardinal.manager.EColor
import github.genesyspl.cardinal.manager.IInstance


class StartLang {
    val startVerification = "${EColor.YELLOW.color}Starting %to% verification...${EColor.RESET.color}"

    val completeVerification = "${EColor.YELLOW.color}Verification completed!${EColor.RESET.color}"

    val updatePlugin = "${EColor.YELLOW.color}Updating plugin to version %version%...${EColor.RESET.color}"

    val updatePluginMessage = "${EColor.YELLOW.color}Update finished, rebooting!${EColor.RESET.color}"

    val langSelectedMessage = "${EColor.YELLOW.color}Selected lang -> %lang%.${EColor.RESET.color}"

    val langError = "${EColor.RED.color}Using default lang by error!${EColor.RESET.color}"

    val createMessage = "${EColor.YELLOW.color}Created file from %to%, %file%.${EColor.RESET.color}"

    val addMessage = "${EColor.YELLOW.color}Added path %path% to file %file%.${EColor.RESET.color}"

    val removeMessage = "${EColor.RED.color}Removed path %path% in file %file%.${EColor.RESET.color}"

    val connectDatabase = "${EColor.YELLOW.color}Starting the connection to the database!${EColor.RESET.color}"

    val connectDatabaseSuccess =
        "${EColor.YELLOW.color}Successfully connected to the database %db%!${EColor.RESET.color}"

    val databaseValid =
        "${EColor.RED.color}Please select a valid database, valid -> h2 and mysql, h2 was used to start.${EColor.RESET.color}"

    companion object : IInstance<StartLang> {
        private val instance = createInstance()
        override fun createInstance(): StartLang = StartLang()
        override fun getInstance(): StartLang {
            return instance
        }
    }
}