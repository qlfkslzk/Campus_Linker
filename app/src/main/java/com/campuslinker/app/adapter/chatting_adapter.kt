package com.campuslinker.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.R
import com.campuslinker.app.token_management
import java.util.ArrayList

class chatting_adapter (val profileList : ArrayList<chatting_list>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
    {
        var name = token_management.prefs.getString("name","기본값")
        var accessToken = token_management.prefs.getString("access_token","기본값")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                1-> {
                    val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.send_message,
                        parent,
                        false
                    )
                    my_Holder(view)
                } else ->{
                    val view = LayoutInflater.from(parent.context).inflate(
                        R.layout.receive_message,
                        parent,
                        false
                    )
                    other_Holder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(profileList[position].user_id.equals(name)) {
                (holder as my_Holder).send_date.text = profileList[position].chat.toString()
                (holder as my_Holder).send_massage.text = profileList[position].format_date?.split(" ")?.get(0).toString()+"\n"+profileList[position].format_date?.split(" ")?.get(1).toString()
            } else {
                (holder as other_Holder).sender_name.text = profileList[position].user_id.toString()
                (holder as other_Holder).receive_message.text = profileList[position].chat.toString()
                (holder as other_Holder).receive_date.text = profileList[position].format_date?.split(" ")?.get(0).toString()+"\n"+profileList[position].format_date?.split(" ")?.get(1).toString()
            }
        }
        override fun getItemCount(): Int {
            return profileList.size
        }
        override fun getItemViewType(position: Int): Int {
            return if(profileList[position].user_id.equals(name))
                1 else 0
        }
        inner class my_Holder(view: View) : RecyclerView.ViewHolder(view) {
            var send_date = view.findViewById<TextView>(R.id.send_date)
            var send_massage = view.findViewById<TextView>(R.id.send_message)
        }
        inner class other_Holder(view: View) : RecyclerView.ViewHolder(view){
            var sender_name = view.findViewById<TextView>(R.id.sender)
            var receive_message = view.findViewById<TextView>(R.id.receive_message)
            var receive_date = view.findViewById<TextView>(R.id.receive_create_date)
        }
        fun addChat(chatItem: chatting_list) {
        profileList.add(chatItem)
        notifyItemInserted(profileList.size - 1)
    }
    }
    class chatting_list(
        var num : Int?=null,
        var room : Int?=null,
        var user_id : String?=null,
        var chat : String?=null,
        var create_date : String?=null,
        var format_date : String?=null
    )