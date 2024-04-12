package com.campuslinker.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.databinding.CommnetItemBinding
import java.util.*

class comment_adapter (val profileList : ArrayList<comment_list>): RecyclerView.Adapter<comment_adapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): comment_adapter.Holder {
        val binding = CommnetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: comment_adapter.Holder, position: Int) {
        holder.comment_userid.text = profileList[position].user_id.toString()
        holder.comment_createdate.text = profileList[position].format_date.toString()
        holder.comment_hidden = profileList[position].hidden_name.toString()
        holder.comment_content.text = profileList[position].comment.toString()
        holder.comment_delete.visibility = View.VISIBLE

    }
    override fun getItemCount(): Int {
        return profileList.size
    }
    class Holder(val binding : CommnetItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val comment_userid = binding.commentName
        val comment_createdate = binding.commentCteateDate
        val comment_content = binding.commentContent
        var comment_hidden : String?=null
        var comment_delete = binding.deleteComment
    }

}
class comment_list(
    var hidden_name : String?=null,
    var ref : Int?=null,
    var user_id : String?=null,
    var num : Int?=null,
    var ref_comment :Int?=null,
    var comment : String?=null,
    var create_date : String?=null,
    var format_date : String ?=null
        )