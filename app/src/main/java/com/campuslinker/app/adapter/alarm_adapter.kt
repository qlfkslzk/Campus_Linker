package com.campuslinker.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.databinding.AlarmItemBinding
import java.util.ArrayList

class alarm_adapter (val profileList : ArrayList<alarm_list>): RecyclerView.Adapter<alarm_adapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): alarm_adapter.Holder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: alarm_adapter.Holder, position: Int) {
        when(profileList[position].purpose.toString()){
            "free" -> holder.alarm_type.text = "자유 게시물"
            "match" -> holder.alarm_type.text = "모집 게시물"
            "room" -> holder.alarm_type.text = "채팅"
        }
        holder.alarm_content.text = profileList[position].content.toString()
        holder.alarm_date.text = profileList[position].format_date.toString()
    }
    override fun getItemCount(): Int {
        return profileList.size
    }
    inner class Holder(val binding : AlarmItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val alarm_type = binding.alarmType
        val alarm_content = binding.alarmContent
        var alarm_date = binding.alarmDate
        var alarm_extra : String?=null
        init {
            binding.alarmView.setOnClickListener {
                val pos = adapterPosition
                if(pos != RecyclerView.NO_POSITION && itemClickListner != null){
                    itemClickListner.onItemClick(binding.alarmView,pos)
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
class alarm_list(
    var purpose : String?=null,
    var num : Int?=null,
    var purpose_num : Int?=null,
    var create_date : String?=null,
    var user : String?=null,
    var content : String?=null,
    var format_date : String?=null
)