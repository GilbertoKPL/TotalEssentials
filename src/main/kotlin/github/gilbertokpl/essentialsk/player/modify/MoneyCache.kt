package github.gilbertokpl.essentialsk.player.modify

import github.gilbertokpl.essentialsk.data.DataManager.put
import github.gilbertokpl.essentialsk.data.tables.PlayerDataSQL
import github.gilbertokpl.essentialsk.player.PlayerData

object MoneyCache {

    fun PlayerData.setMoney(money: Double) {
        moneyCache = money
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.moneyTable to moneyCache))
    }

    fun PlayerData.addMoney(money: Double) {
        moneyCache += money
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.moneyTable to moneyCache))
    }

    fun PlayerData.takeMoney(money: Double) {
        moneyCache -= money
        PlayerDataSQL.put(playerID, hashMapOf(PlayerDataSQL.moneyTable to moneyCache))
    }

    fun PlayerData.getMoney() : Double {
        return moneyCache
    }

}