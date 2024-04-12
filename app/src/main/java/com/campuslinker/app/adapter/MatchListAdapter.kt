package com.campuslinker.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.campuslinker.app.Board_class
import com.campuslinker.app.R
import com.campuslinker.app.RecycleAdapter
import com.campuslinker.app.databinding.MatchBoardListItemBinding
import java.util.ArrayList

class MatchListAdapter (val context: Context, val boardList: ArrayList<Board_class>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.match_board_list_item, null)

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
class MatchRecycleAdapter (val profileList : ArrayList<Board_class>): RecyclerView.Adapter<MatchRecycleAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchRecycleAdapter.Holder {
        val binding = MatchBoardListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    override fun onBindViewHolder(holder: MatchRecycleAdapter.Holder, position: Int) {
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
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: RecycleAdapter.OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    override fun getItemCount(): Int {
        return profileList.size
    }
    class Holder(val binding: MatchBoardListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val board_theme = binding.boardTheme
        val board_date = binding.boardDate
        val board_title = binding.boardTitle
        val board_favorite = binding.boardFavorite
        val board_comment = binding.boardComment
        var board_num : String?=null
    }

}