package com.userplay.bazar22.utils

object GameType {

    private var gameType = "Open"

    fun setGame(game: String) {
        gameType = game
    }

    fun getGameType(): String {

        return gameType
    }
}