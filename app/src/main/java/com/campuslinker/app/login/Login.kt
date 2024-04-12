package com.campuslinker.app.login

import com.google.gson.annotations.SerializedName
import java.util.*

//로그인 output 만들기 (.post)
data class Login(
    var result : Int,
    var massage : String,
    var accessToken : String,
    var refreshToken : String
)
//data class Login_response(
//)
data class PostModel(
    var id : String? =null,
    var pwd : String?=null
)
data class PostResult(
    @SerializedName("result")
    var result:Int? = null,
    @SerializedName("message")
    var massage : String? =null,
    @SerializedName("accessToken")
    var accessToken : String? =null,
    @SerializedName("refreshToken")
    var refreshToken : String? =null
)
