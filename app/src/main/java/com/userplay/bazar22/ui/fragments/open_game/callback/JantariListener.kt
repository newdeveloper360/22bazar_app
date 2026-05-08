package com.userplay.bazar22.ui.fragments.open_game.callback

interface JantariListener {


    fun onTextAddListener(amount : String, number : String, position : Int, itemId: Int,gameTypeId : Int)
    fun onTextRemoveListener(amount : String, number : String, position : Int, itemId: Int)
}