package com.campuslinker.app.check_parameter

import com.google.gson.annotations.SerializedName
import java.util.*

data class check_data(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null
)