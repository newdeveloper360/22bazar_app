package com.userplay.bazar22.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModels : ViewModel() {

    // for  Game type
    private var gameType: MutableLiveData<String> = MutableLiveData("open")
    var mGameType: LiveData<String>? = gameType

    //for balance
    private var balance: MutableLiveData<String> = MutableLiveData("0.00")
    var mBalance: LiveData<String>? = balance

    //update cancel
    private var canceld: MutableLiveData<Boolean> = MutableLiveData()
    var isCancel: LiveData<Boolean>? = canceld


    fun setGameType(game: String) {
        gameType.value = game
    }

    fun setBalance(amount: String?) {
        balance.value = amount!!
    }

    fun setCanceld(isCanceld: Boolean) {
        canceld.value = isCanceld
    }




}