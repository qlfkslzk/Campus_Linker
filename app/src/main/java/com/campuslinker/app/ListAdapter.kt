package com.campuslinker.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.adapter.MatchRecycleAdapter
import com.campuslinker.app.adapter.comment_list
import com.campuslinker.app.databinding.BoardListItemBinding
import java.util.*

class CommentListAdapter(val context: Context, val comment_list: ArrayList<comment_list>) : BaseAdapter() {
    var id = token_management.prefs.getString("id","기본값")
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var user_id : String?=null
    var set_id : String?=null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.commnet_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val comment_name = view.findViewById<TextView>(R.id.comment_name)
        val comment_date = view.findViewById<TextView>(R.id.comment_cteate_date)
        val comment_content = view.findViewById<TextView>(R.id.comment_content)
        var comment_num : String?=null
        val comment_delete = view.findViewById<TextView>(R.id.delete_comment)
        /* ArrayList<board>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */

        comment_date.text = comment_list[position].format_date
        comment_content.text = comment_list[position].comment
        comment_num = comment_list[position].num.toString()
        comment_name.text = comment_list[position].user_id.toString()
        if(comment_list[position].user_id.toString().contains(",")){
            set_id = comment_list[position].user_id.toString().split(",").get(1)
            user_id = comment_list[position].user_id.toString().split(",").get(0)
            comment_name.text = set_id
        }
        else{
            set_id = comment_list[position].user_id.toString()
            comment_name.text = set_id
        }
        if(comment_list[position].user_id.toString().equals("(알수없음)")){
            comment_delete.visibility = View.GONE
        }
        else if(user_id.equals(id)){
            comment_delete.visibility = View.VISIBLE
        }
        else{
            comment_delete.visibility = View.GONE
        }

        comment_delete.setOnClickListener {
            // 클릭한 아이템을 얻기
            val clickedItem = comment_list[position]

            // 클릭 이벤트 리스너가 설정되어 있다면 호출
            commentDeleteClickListener?.onCommentDeleteClick(clickedItem)
        }
//        comment_delete.setOnClickListener {
//            // 클릭 이벤트 처리 코드를 여기에 추가
//            // 예를 들어, 클릭 시 어떤 동작을 수행하고 싶다면 여기에 코드를 추가하세요.
//            // 이때, position을 이용하여 클릭한 아이템의 위치를 파악할 수 있습니다.
//            // comment_list[position]을 통해 클릭한 아이템의 데이터에 접근할 수 있습니다.
//            // 여기에서 클릭 이벤트를 처리할 수 있습니다.
//            delete_comment_APIS.create().Delete_Token_Info(comment_list[position].num.toString(),accessToken)?.enqueue(object :
//                Callback<Delete_comment_result> {
//                override fun onResponse(
//                    call: Call<Delete_comment_result>,
//                    response: Response<Delete_comment_result>
//                ) {
//                    Toast.makeText(context,"message : " +response.body()?.message.toString()+"\nresult code : " + response.body()?.result.toString(),Toast.LENGTH_SHORT).show()
//                    comment_name.text = comment_list[position].num.toString()
//                }
//                override fun onFailure(call: Call<Delete_comment_result>, t: Throwable) {
//
//                }
//
//            })
//        }

        return view
    }
    interface OnCommentDeleteClickListener {
        fun onCommentDeleteClick(comment: comment_list)
    }

    // 클릭 리스너 인스턴스
    private var commentDeleteClickListener: OnCommentDeleteClickListener? = null

    // 클릭 리스너 설정 메서드
    fun setOnCommentDeleteClickListener(listener: OnCommentDeleteClickListener) {
        this.commentDeleteClickListener = listener
    }
    override fun getItem(position: Int): Any {
        return comment_list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return comment_list.size
    }
}
class ListAdapter (val context: Context, val boardList: ArrayList<Board_class>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
    /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
    val view: View = LayoutInflater.from(context).inflate(R.layout.board_list_item, null)

    /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
    val board_theme = view.findViewById<TextView>(R.id.board_theme)
        val board_date = view.findViewById<TextView>(R.id.board_date)
        val board_title = view.findViewById<TextView>(R.id.board_title)
        val board_favorite = view.findViewById<TextView>(R.id.board_favorite)
        val board_comment = view.findViewById<TextView>(R.id.board_comment)
        var board_num : String?=null

        /* ArrayList<board>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
    val board = boardList[position]
        board_theme.text = board.theme
        board_date.text = board.date
        board_title.text = board.title
        board_favorite.text = board.favorite
        board_comment.text = board.comment
        board_num = board.num.toString()

    return view
}

override fun getItem(position: Int): Any {
    return boardList[position]
}

override fun getItemId(position: Int): Long {
    return position.toLong()
}

override fun getCount(): Int {
    return boardList.size
}
}

class RecycleAdapter (val profileList : ArrayList<Board_class>): RecyclerView.Adapter<RecycleAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleAdapter.Holder {
        val binding = BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: RecycleAdapter.Holder, position: Int) {
        holder.board_theme.text = profileList[position].theme.toString()
        holder.board_date.text = profileList[position].date.toString()
        holder.board_title.text = profileList[position].title.toString()
        holder.board_favorite.text = profileList[position].favorite.toString()
        holder.board_comment.text = profileList[position].comment.toString()
        holder.board_num = profileList[position].num.toString()
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
//        holder.board_theme.setText(profileList[position].theme)
//        holder.board_date.setText(profileList[position].date)
//        holder.board_title.setText(profileList[position].title)
//        holder.board_favorite.setText(profileList[position].favorite)
//        holder.board_comment.setText(profileList[position].comment)
    }
    interface OnItemClickListener : MatchRecycleAdapter.OnItemClickListener {
        override fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    override fun getItemCount(): Int {
        return profileList.size
    }
    class Holder(val binding : BoardListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val board_theme = binding.boardTheme
        val board_date = binding.boardDate
        val board_title = binding.boardTitle
        val board_favorite = binding.boardFavorite
        val board_comment = binding.boardComment
        var board_num : String?=null
    }

}




class CustomAdapter(val profileList : ArrayList<Profile>) : RecyclerView.Adapter<CustomAdapter.Holder>() {
    override fun getItemCount(): Int {
        return profileList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.Holder {
        val binding = BoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: CustomAdapter.Holder, position: Int) {
        holder.name.text = profileList[position].name
        holder.age.text = profileList[position].age.toString()
    }

    inner class Holder(val binding: BoardListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.boardComment
        val age = binding.boardDate
    }
}
data class Profile(val name : String, val age : Int)