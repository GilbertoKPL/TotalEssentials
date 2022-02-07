package github.gilbertokpl.essentialsk.config.otherConfigs

import github.gilbertokpl.essentialsk.manager.EColor

internal object StartLang {
    val startVerification = EColor.YELLOW.color +
            "Iniciciando %to% verificação..." +
            EColor.RESET.color

    val completeVerification = EColor.YELLOW.color +
            "Verificação completa!" +
            EColor.RESET.color

    val updatePlugin = EColor.YELLOW.color +
            "Atualizando o plugin para versão %version%..." +
            EColor.RESET.color

    val updatePluginMessage = EColor.YELLOW.color +
            "Atualizado com sucesso, reninciando!" +
            EColor.RESET.color

    val langSelectedMessage = EColor.YELLOW.color +
            "lang selecionada -> %lang%." +
            EColor.RESET.color

    val langError = EColor.RED.color +
            "usando lang padrão por erro!" +
            EColor.RESET.color

    val createMessage = EColor.YELLOW.color +
            "Arquivo criado de %to%, %file%." +
            EColor.RESET.color

    val addMessage = EColor.YELLOW.color +
            "Adicionado path %path% do arquivo %file%." +
            EColor.RESET.color

    val removeMessage = EColor.RED.color +
            "Removido path %path% do arquivo %file%." +
            EColor.RESET.color

    val connectDatabase = EColor.YELLOW.color +
            "Iniciando conexão com a database!" +
            EColor.RESET.color

    val connectDatabaseSuccess =
        EColor.YELLOW.color +
                "Conexão criada com sucesso, %db%!" +
                EColor.RESET.color

    val databaseValid =
        EColor.RED.color +
                "Porfavor selecione uma database valida, " +
                "validas -> H2 e MYSQL, " +
                "H2 foi utilizado para iniciar." +
                EColor.RESET.color
    val startLoadData = EColor.YELLOW.color +
            "Iniciado Carregamento dos players" +
            EColor.RESET.color
    val finishLoadData = EColor.YELLOW.color +
            "Carregado %quant% players no cache" +
            EColor.RESET.color
}
