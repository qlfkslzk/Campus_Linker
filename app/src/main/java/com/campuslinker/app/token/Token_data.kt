package com.campuslinker.app.token

import com.google.gson.annotations.SerializedName
import java.util.*

data class Token_data(
    @SerializedName("result")
    var result : Int? =null,
    @SerializedName("massage")
    var massage : String? =null,
    @SerializedName("user")
    var user: ArrayList<TokenModel>? =null
)
data class TokenModel(
    @SerializedName("univ")
    var univ : String?=null,
    @SerializedName("gender")
    var gender : String?=null,
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("name")
    var name : String?=null,
    @SerializedName("student_id")
    var student_id : Int?=null,
    @SerializedName("cert")
    var cert : String?=null,
    @SerializedName("id")
    var id : String?=null,
    @SerializedName("email")
    var email : String?=null,
)
