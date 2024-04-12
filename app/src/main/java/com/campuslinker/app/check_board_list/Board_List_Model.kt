package com.campuslinker.app.check_board_list

import com.google.gson.annotations.SerializedName
import java.util.*

data class Board_List_Model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("list")
    var list : ArrayList<board_list>? =null
)
data class board_list(
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("category")
    var category : String?=null,
    @SerializedName("title")
    var title : String?=null,
    @SerializedName("contents")
    var contents : String?=null,
    @SerializedName("reaction_count")
    var reaction_count : Int?=null,
    @SerializedName("comment")
    var comment : Int?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("format_date")
    var format_date : String?=null
)
data class Match_Board_List_Model(
    @SerializedName("result")
    var result : Int?=null,
    @SerializedName("message")
    var message : String?=null,
    @SerializedName("list")
    var list : ArrayList<match_board_list>? =null
)
data class match_board_list(
    @SerializedName("room_num")
    var room_num : String?=null,
    @SerializedName("num")
    var num : Int?=null,
    @SerializedName("title")
    var title : String?=null,
    @SerializedName("category")
    var category : String?=null,
    @SerializedName("contents")
    var contents : String?=null,
    @SerializedName("gender")
    var gender : String?=null,
    @SerializedName("users")
    var users : String?=null,
    @SerializedName("create_date")
    var create_date : String?=null,
    @SerializedName("delete_date")
    var delete_date : String?=null,
    @SerializedName("format_create_date")
    var format_create_date : String?=null,
    @SerializedName("format_delete_date")
    var format_delete_date : String?=null
)