package com.campuslinker.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.databinding.ChattingListItemBinding
import java.util.ArrayList

class chatting_room_list_adapter (val profileList : ArrayList<chatting_room_list>): RecyclerView.Adapter<chatting_room_list_adapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chatting_room_list_adapter.Holder {
        val binding = ChattingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: chatting_room_list_adapter.Holder, position: Int) {
        holder.chat_title.text = profileList[position].room_name.toString()
        holder.chat_content.text = profileList[position].last_chat.toString()
        holder.chat_date.text = profileList[position].create_date.toString()
        holder.chat_nonread_count.text = profileList[position].not_read.toString()
        holder.chat_room_num = profileList[position].room_num.toString()
    }
    override fun getItemCount(): Int {
        return profileList.size
    }
    inner class Holder(val binding : ChattingListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val chat_title = binding.chatTitle
        val chat_content = binding.chatContent
        var chat_date = binding.chatDate
        var chat_nonread_count =binding.chatNonreadCount
        var chat_room_num : String?=null
        init {
            binding.chattingView.setOnClickListener {

                val pos = adapterPosition
                if(pos != RecyclerView.NO_POSITION && itemClickListner != null){
                    itemClickListner.onItemClick(binding.chattingView,pos)
                }
            }
    }
        }
        interface OnItemClickListner{
    fun onItemClick(view: View, position: Int)
}
        //객체 저장 변수
        private lateinit var itemClickListner: OnItemClickListner

        //객체 전달 메서드
        fun setOnItemclickListner(onItemClickListner: OnItemClickListner){
            itemClickListner = onItemClickListner
        }

}
class chatting_room_list(
    var room_num : Int?=null,
    var room_name : String?=null,
    var last_chat : String?=null,
    var not_read : Int?=null,
    var create_date : String?=null
)