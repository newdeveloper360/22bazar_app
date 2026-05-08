package com.userplay.bazar22.ui.fragments.home.models

import android.widget.CompoundButton

data class NumberDataModel(
    var number:String="",
    var point:String="",
    var isChecked:Boolean=false,
){
    fun onCheckedChangeListener(buttonView: CompoundButton, isChecked: Boolean) {
        buttonView.isChecked=isChecked
    }
}
