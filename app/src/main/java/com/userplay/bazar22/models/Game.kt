package com.userplay.bazar22.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Game(
    @SerializedName("amount")
    var amount: Int?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("session")
    var session: String?,
    @SerializedName("game_type_id")
    val gameTypeId: Int?,
    @Transient
    var removedItemPosition : Int = -1 ,
    @Transient
    var itemItemPosition : Int = -1  ,
    @Transient
    var id: Int = 0,
    @SerializedName("pattiType")
    val pattiType: String="",
) : Serializable