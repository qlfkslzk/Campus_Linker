package com.campuslinker.app.resister

import com.google.gson.annotations.SerializedName

data class Resister_Post
    (
    @SerializedName("user_name")
    var user_name : String? =null,
    @SerializedName("school_name")
    var school_name : String?=null,
    @SerializedName("student_id")
    var student_id : Int?=null,
    @SerializedName("gender")
    var gender : String?=null,
    @SerializedName("user_id")
    var user_id : String?=null,
    @SerializedName("user_pwd")
    var user_pwd : String?=null,
    @SerializedName("email")
    var email : String?=null
)
data class Resister_Post_result(
    @SerializedName("result")
    var result:Int? = null
)
