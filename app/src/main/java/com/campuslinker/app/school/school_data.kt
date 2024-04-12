package com.campuslinker.app.school

import com.google.gson.annotations.SerializedName
import java.util.*

data class school_data(
    @SerializedName("result")
    var result : String?=null,
    @SerializedName("school_list")
    var school_list : ArrayList<SchoolModel>? =null
)
data class SchoolModel(
    @SerializedName("address")
    var address : String?=null,
    @SerializedName("mail")
    var mail : String?=null,
    @SerializedName("school_name")
    var school_name : String?=null,
    @SerializedName("id")
    var id : Int?=null
)