package com.userplay.bazar22.ui.callbacks

interface OnGameTypeListener {

    fun removeGameType(number : String, gameType : String?, position : Int, totalPoints: Int)

    fun updateSubmitResult(totalPoints: Int)

    fun onCrossingInserted(amount : String, number : String, position : Int)
}