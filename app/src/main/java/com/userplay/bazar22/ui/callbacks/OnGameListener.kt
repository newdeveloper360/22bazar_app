package com.userplay.bazar22.ui.callbacks

interface OnGameListener {

    fun onGameClosedClick(
        position: Int?,
        openTime: String?,
        closeTime: String?,
        openResultTime: String?,
        closeResultTime: String?,
        bidName: String?
    )

    fun onGameStartClick(position: Int, marketID: Int, mGameName: String, openStatus: Boolean)
}