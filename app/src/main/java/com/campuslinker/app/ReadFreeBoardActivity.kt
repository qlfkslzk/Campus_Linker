package com.campuslinker.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import com.campuslinker.app.adapter.comment_list
import com.campuslinker.app.comment.Delete_comment_result
import com.campuslinker.app.comment.comment_json_model
import com.campuslinker.app.comment.comment_model
import com.campuslinker.app.comment.comment_read_model
import com.campuslinker.app.comment.delete_comment_APIS
import com.campuslinker.app.comment.make_comment_APIS
import com.campuslinker.app.comment.resd_comment_APIS
import com.campuslinker.app.databinding.ReadFreeBoardBinding
import com.campuslinker.app.reaction.reaction_APIS
import com.campuslinker.app.reaction.reaction_json_model
import com.campuslinker.app.reaction.reaction_model
import com.campuslinker.app.read_board.read_free_board
import com.campuslinker.app.read_board.read_free_board_response_model
import com.campuslinker.app.rewrite_board.Delete_board_APIS
import com.campuslinker.app.rewrite_board.Delete_result
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ReadFreeBoardActivity : AppCompatActivity() {
    val read_free_board_APIS = read_free_board.create()
    val reactionAPIS = reaction_APIS.create()
    val comment_APIS = make_comment_APIS.create()
    val commnet_read_APIS = resd_comment_APIS.create()
    var checked_hidden = "N"
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var board_num = token_management.prefs.getString("board_num","기본값")
    var id = token_management.prefs.getString("id","기본값")
    var user_id : String ?= null
    var user_name : String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding : ReadFreeBoardBinding;
        binding = ReadFreeBoardBinding.inflate(layoutInflater)
        val view = binding.root
        super.onCreate(savedInstanceState)
        setContentView(view)

        Log.d("first_num", board_num)
        if(intent.getStringExtra("board_num")?.toInt()!=null){
            board_num = intent.getStringExtra("board_num").toString()
            Log.d("before_num", board_num)
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var refreshToken = token_management.prefs.getString("refresh_token","기본값")
        val refresh_token_data = re_token_model(
            refreshToken
        )
        Get_token_APIS.create().gettokenlInfo(accessToken)?.enqueue(object :
            Callback<Token_data> {
            override fun onResponse(
                call: Call<Token_data>,
                response: Response<Token_data>
            ) {
                if(response.body()?.result==200){
                    var comment_lists = ArrayList<comment_list>()
                    binding.checkBox2.setOnCheckedChangeListener{ _, isChecked ->
                        if(isChecked) {
                            checked_hidden = "Y"
                        }else{
                            checked_hidden = "N"
                        }
                    }

//        binding.scrollView2.viewTreeObserver.addOnScrollChangedListener {
//            // NestedScrollView의 스크롤 변화를 감지합니다.
//            val scrollY = binding.scrollView2.scrollY
//            val topWidgetBottom = binding.View.bottom
//
//            // 특정 조건을 확인하여 스크롤 가능 여부를 조절합니다.
//            if (scrollY >= topWidgetBottom) {
//                binding.recyclerComment.isNestedScrollingEnabled = true
//            } else {
//                binding.recyclerComment.isNestedScrollingEnabled = false
//            }
//        }

                    read_free_board_APIS.get_read_free_board_Info(board_num, accessToken)?.enqueue(object :
                        Callback<read_free_board_response_model> {
                        override fun onResponse(
                            call: Call<read_free_board_response_model>,
                            response: Response<read_free_board_response_model>
                        ) {

                            Log.d("after_num", board_num)
                            if(response.body()?.result == 200){
//                    var date_cu = response.body()?.board?.get(0)?.create_date
//                    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.KOREA)
//                    val outputFormat = SimpleDateFormat("MM dd", Locale.KOREA)
//                    val date = inputFormat.parse(date_cu)
//                    val format_date = outputFormat.format(date)
                                binding.userName.setText(response.body()?.board?.get(0)?.write_user?.split(",")?.get(1))
                                binding.category.setText(response.body()?.board?.get(0)?.category.toString())
                                binding.content.setText(response.body()?.board?.get(0)?.contents.toString())
                                binding.createDate.setText(response.body()?.board?.get(0)?.format_date.toString())
                                binding.titleFreeboard.setText(response.body()?.board?.get(0)?.title.toString())
                                binding.reaction.setText(response.body()?.board?.get(0)?.reaction_count.toString())
                                binding.comment.setText(response.body()?.board?.get(0)?.comment.toString())
                                if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                    user_id = response.body()?.board?.get(0)?.write_user!!.split(",").get(0)
                                }
                                else{
                                    user_id = response.body()?.board?.get(0)?.write_user.toString()
                                }
//                    Toast.makeText(context,user_id+" : "+ id,Toast.LENGTH_SHORT).show()
                                if(id.equals(user_id)){
                                    binding.freeboardMenu.setOnClickListener { view ->
                                        val popupMenu = PopupMenu(context, view)
                                        val inflater = popupMenu.menuInflater
                                        inflater.inflate(R.menu.freeboard_menu, popupMenu.menu)
                                        popupMenu.setOnMenuItemClickListener { item ->
                                            when (item.itemId) {
                                                R.id.exit -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    val intent = Intent(this@ReadFreeBoardActivity, ReadFreeBoardActivity::class.java)
                                                    finish()
                                                    startActivity(intent)
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                R.id.rewrite -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    var intent = Intent(this@ReadFreeBoardActivity, MakefreeboardActivity::class.java)
                                                    intent.putExtra("type", "rewrite") // 데이터 넣기
                                                    intent.putExtra("board_num", board_num)
                                                    intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                    intent.putExtra("contents", response.body()?.board?.get(0)?.contents.toString()) // 데이터 넣기
                                                    intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                                    intent.putExtra("comment", response.body()?.board?.get(0)?.comment.toString()) // 데이터 넣기
                                                    intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                    intent.putExtra("hiden", response.body()?.board?.get(0)?.hidden_name.toString()) // 데이터 넣기
                                                    startActivity(intent)
                                                    finish()
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                R.id.delete -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    Delete_board_APIS.create().Delete_Board_Info("free","$board_num", accessToken)?.enqueue(object :
                                                        Callback<Delete_result> {
                                                        override fun onResponse(
                                                            call: Call<Delete_result>,
                                                            response: Response<Delete_result>
                                                        ) {
                                                            Toast.makeText(context,response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                            if(response.body()?.result==200){
                                                                finish()
                                                            }
                                                        }
                                                        override fun onFailure(call: Call<Delete_result>, t: Throwable) {
                                                        }
                                                    })
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                else -> false
                                            }
                                        }

                                        popupMenu.show()
                                    }
                                }
                                else{
                                    binding.freeboardMenu.setOnClickListener { view ->
                                        val popupMenu = PopupMenu(context, view)
                                        val inflater = popupMenu.menuInflater
                                        inflater.inflate(R.menu.freeboard_menu2, popupMenu.menu)
                                        popupMenu.setOnMenuItemClickListener { item ->
                                            when (item.itemId) {
                                                R.id.exit -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    val intent = Intent(this@ReadFreeBoardActivity, ReadFreeBoardActivity::class.java)
                                                    finish()
                                                    startActivity(intent)
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                else -> false
                                            }
                                        }

                                        popupMenu.show()
                                    }
                                }

                            }
                            else {
                                Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                var intent = Intent(context, StartActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        }
                        override fun onFailure(call: Call<read_free_board_response_model>, t: Throwable) {

                        }

                    })

                    var data_reaction = reaction_json_model(
                        accessToken,
                        board_num.toInt()
                    )
                    binding.commentAdd.setOnClickListener {
                        reactionAPIS.reaction_post_users(data_reaction)?.enqueue(object :
                            Callback<reaction_model> {
                            override fun onResponse(
                                call: Call<reaction_model>,
                                response: Response<reaction_model>
                            ) {
                                if(response.body()?.result==200){
                                    read_free_board_APIS.get_read_free_board_Info(board_num, accessToken)?.enqueue(object :
                                        Callback<read_free_board_response_model> {
                                        override fun onResponse(
                                            call: Call<read_free_board_response_model>,
                                            response: Response<read_free_board_response_model>
                                        ) {
                                            if(response.body()?.result == 200){
                                                binding.reaction.setText(response.body()?.board?.get(0)?.reaction_count.toString())
                                                binding.comment.setText(response.body()?.board?.get(0)?.comment.toString())
                                            }
                                        }
                                        override fun onFailure(call: Call<read_free_board_response_model>, t: Throwable) {

                                        }

                                    })
                                    Toast.makeText(this@ReadFreeBoardActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()

                                }

                            }
                            override fun onFailure(call: Call<reaction_model>, t: Throwable) {

                            }

                        })
                    }
                    binding.buttonComment.setOnClickListener {
                        var data_comment = comment_json_model(
                            accessToken,
                            board_num.toInt(),
                            0,
                            binding.editComment.text.toString(),
                            checked_hidden
                        )
                        comment_APIS.comment_post_users(data_comment)?.enqueue(object :
                            Callback<comment_model> {
                            override fun onResponse(
                                call: Call<comment_model>,
                                response: Response<comment_model>
                            ) {
                                if(response.body()?.result==200){

                                }else{
                                    Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    var intent = Intent(context, StartActivity::class.java)
                                    startActivity(intent)
                                    finish()}

                                commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                                    Callback<comment_read_model> {
                                    override fun onResponse(
                                        call: Call<comment_read_model>,
                                        response: Response<comment_read_model>
                                    ) {
                                        if(response.body()?.result==200){
                                            comment_lists.clear()
                                            var comment_length = response.body()?.list?.size
                                            if(comment_length !=null) {
                                                for (i in 0..comment_length - 1) {
                                                    comment_lists.add(
                                                        comment_list(
                                                            response.body()?.revers_list?.get(i)?.hidden_name.toString(),
                                                            response.body()?.revers_list?.get(i)?.ref,
                                                            response.body()?.revers_list?.get(i)?.user_id.toString(),
                                                            response.body()?.revers_list?.get(i)?.num,
                                                            response.body()?.revers_list?.get(i)?.ref_comment,
                                                            response.body()?.revers_list?.get(i)?.comment.toString(),
                                                            response.body()?.revers_list?.get(i)?.create_date.toString(),
                                                            response.body()?.revers_list?.get(i)?.format_date.toString()
                                                        )
                                                    )
                                                    var adapter = CommentListAdapter(context, comment_lists) // 어댑터를 초기화하세요
                                                    binding.recyclerComment.adapter = adapter
                                                    var totalHeight = 0
                                                    for (i in 0 until adapter.count) {
                                                        val listItem = adapter.getView(i, null, binding.recyclerComment)
                                                        listItem.measure(
                                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                        )
                                                        totalHeight += listItem.measuredHeight
                                                    }
// 리스트뷰의 높이를 동적으로 설정
                                                    val params = binding.recyclerComment.layoutParams
                                                    params.height = totalHeight + (binding.recyclerComment.dividerHeight * (adapter.count - 1))
                                                    binding.recyclerComment.layoutParams = params
                                                    binding.recyclerComment.requestLayout()
//                                    var read_comment_adapter = comment_adapter(comment_lists)
//                                    binding.recyclerComment.adapter = read_comment_adapter
//                                    binding.recyclerComment.layoutManager = LinearLayoutManager(this@ReadFreeBoardActivity)

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }
                                            }
                                        }
                                        else{
                                            var intent = Intent(context, StartActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }

                                    }
                                    override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                    }

                                })
                            }
                            override fun onFailure(call: Call<comment_model>, t: Throwable) {

                            }

                        })
                        binding.editComment.setText("")
                        hideKeyboard(this@ReadFreeBoardActivity)
                    }
                    Log.d("board_num", board_num)
                    Log.d("accessToken", accessToken)
                    commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                        Callback<comment_read_model> {
                        override fun onResponse(
                            call: Call<comment_read_model>,
                            response: Response<comment_read_model>
                        ) {
                            var comment_length = response.body()?.list?.size
                            if(comment_length !=null) {
                                for (i in 0..comment_length - 1) {
                                    comment_lists.add(
                                        comment_list(
                                            response.body()?.revers_list?.get(i)?.hidden_name.toString(),
                                            response.body()?.revers_list?.get(i)?.ref,
                                            response.body()?.revers_list?.get(i)?.user_id.toString(),
                                            response.body()?.revers_list?.get(i)?.num,
                                            response.body()?.revers_list?.get(i)?.ref_comment,
                                            response.body()?.revers_list?.get(i)?.comment.toString(),
                                            response.body()?.revers_list?.get(i)?.create_date.toString(),
                                            response.body()?.revers_list?.get(i)?.format_date.toString()
                                        )
                                    )
//                                    Log.d("user_id", response.body()?.revers_list?.get(i)?.user_id.toString())

//                                    Log.d("hidden_name", response.body()?.revers_list?.get(i)?.hidden_name.toString())
//                                    Log.d("ref", response.body()?.revers_list?.get(i)?.ref.toString())
//                                    Log.d("user_id", response.body()?.revers_list?.get(i)?.user_id.toString())
//                                    Log.d("reverse_comment_num", response.body()?.revers_list?.get(i)?.num.toString() )
//                                    Log.d("comment_num", response.body()?.list?.get(i)?.num.toString() )
//                                    Log.d("ref_comment", response.body()?.revers_list?.get(i)?.ref_comment.toString())
//                                    Log.d("comment", response.body()?.revers_list?.get(i)?.comment.toString())
//                        var read_comment_adapter = comment_adapter(comment_lists)
//                        binding.recyclerComment.adapter = read_comment_adapter
//                        binding.recyclerComment.layoutManager = LinearLayoutManager(this@ReadFreeBoardActivity)
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                }

                            }
                            var adapter = CommentListAdapter(this@ReadFreeBoardActivity, comment_lists) // 어댑터를 초기화하세요
                            binding.recyclerComment.adapter = adapter
                            adapter.setOnCommentDeleteClickListener(object : CommentListAdapter.OnCommentDeleteClickListener {
                                override fun onCommentDeleteClick(comment: comment_list) {
                                    // 클릭 이벤트 처리 코드를 여기에 추가
                                    // 이때, comment 매개변수로 클릭한 아이템의 정보가 전달됩니다.
                                    // 원하는 동작을 수행할 수 있습니다.
                                    // 아이템 삭제
                                    if(comment.num!=null){
                                        delete_comment_APIS.create().Delete_Token_Info(comment.num.toString(), accessToken)?.enqueue(object :
                                            Callback<Delete_comment_result> {
                                            override fun onResponse(
                                                call: Call<Delete_comment_result>,
                                                response: Response<Delete_comment_result>
                                            ) {
                                                Toast.makeText(context,response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                comment_lists.remove(comment)
                                                // RecyclerView에서 아이템 제거
                                                adapter.notifyDataSetChanged() // 데이터 변경을 어댑터에 알림
                                            }
                                            override fun onFailure(call: Call<Delete_comment_result>, t: Throwable) {
                                            }
                                        })
                                    }

                                    // 뷰의 높이 조정 (필요하다면)
                                    var totalHeight = 0
                                    for (i in 0 until adapter.count) {
                                        val listItem = adapter.getView(i, null, binding.recyclerComment)
                                        listItem.measure(
                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                        )
                                        totalHeight += listItem.measuredHeight
                                    }
                                    val params = binding.recyclerComment.layoutParams
                                    params.height = totalHeight + (binding.recyclerComment.dividerHeight * (adapter.count - 1))
                                    binding.recyclerComment.layoutParams = params
                                    binding.recyclerComment.requestLayout()
                                }
                            })


                            /////////////////////////////////////////////////////////////////////////


//                                    delete_comment_APIS.create().Delete_Token_Info(comment.num.toString(),accessToken)?.enqueue(object :
//                                        Callback<Delete_comment_result> {
//                                        override fun onResponse(
//                                            call: Call<Delete_comment_result>,
//                                            response: Response<Delete_comment_result>
//                                        ) {
//                                            Toast.makeText(context,"message : " +response.body()?.message.toString()+"\nresult code : " + response.body()?.result.toString()
//                                                +"\ncomment.num : "+ comment.num.toString(),Toast.LENGTH_SHORT).show()
//                                            Log.d("message",response.body()?.message.toString())
//                                            Log.d("result",response.body()?.result.toString())
//                                            Log.d("comment.num",comment.num.toString())
//                                        }
//                                        override fun onFailure(call: Call<Delete_comment_result>, t: Throwable) {
//
//                                        }
//
//                                    })

                            ////////////////////////////////////////////////////////////////////////
                            var totalHeight = 0
                            for (i in 0 until adapter.count) {
                                val listItem = adapter.getView(i, null, binding.recyclerComment)
                                listItem.measure(
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                )
                                totalHeight += listItem.measuredHeight
                            }
// 리스트뷰의 높이를 동적으로 설정
                            val params = binding.recyclerComment.layoutParams
                            params.height = totalHeight + (binding.recyclerComment.dividerHeight * (adapter.count - 1))
                            binding.recyclerComment.layoutParams = params
                            binding.recyclerComment.requestLayout()
                        }
                        override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                        }

                    })
                    binding.freeboardExit.setOnClickListener {
                        finish()
                    }
                }
                else {
                    Re_Put_Token_APIS.create().post_re_token(refresh_token_data)?.enqueue(object :
                        Callback<re_token_data> {
                        override fun onResponse(
                            call: Call<re_token_data>,
                            refresh_response: Response<re_token_data>
                        ) {
                            if(refresh_response.body()?.result==200){
                                token_management.prefs.setString("access_token", refresh_response.body()?.accessToken.toString())
                                token_management.prefs.setString("refresh_token", refresh_response.body()?.refreshToken.toString())
                                accessToken = token_management.prefs.getString("access_token","기본값")
                                refreshToken = token_management.prefs.getString("refresh_token","기본값")

                                var comment_lists = ArrayList<comment_list>()
                                binding.checkBox2.setOnCheckedChangeListener{ _, isChecked ->
                                    if(isChecked) {
                                        checked_hidden = "Y"
                                    }else{
                                        checked_hidden = "N"
                                    }
                                }

//        binding.scrollView2.viewTreeObserver.addOnScrollChangedListener {
//            // NestedScrollView의 스크롤 변화를 감지합니다.
//            val scrollY = binding.scrollView2.scrollY
//            val topWidgetBottom = binding.View.bottom
//
//            // 특정 조건을 확인하여 스크롤 가능 여부를 조절합니다.
//            if (scrollY >= topWidgetBottom) {
//                binding.recyclerComment.isNestedScrollingEnabled = true
//            } else {
//                binding.recyclerComment.isNestedScrollingEnabled = false
//            }
//        }

                                read_free_board_APIS.get_read_free_board_Info(board_num, accessToken)?.enqueue(object :
                                    Callback<read_free_board_response_model> {
                                    override fun onResponse(
                                        call: Call<read_free_board_response_model>,
                                        response: Response<read_free_board_response_model>
                                    ) {

                                        if(response.body()?.result == 200){
//                    var date_cu = response.body()?.board?.get(0)?.create_date
//                    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.KOREA)
//                    val outputFormat = SimpleDateFormat("MM dd", Locale.KOREA)
//                    val date = inputFormat.parse(date_cu)
//                    val format_date = outputFormat.format(date)
                                            binding.userName.setText(response.body()?.board?.get(0)?.write_user?.split(",")?.get(1))
                                            binding.category.setText(response.body()?.board?.get(0)?.category.toString())
                                            binding.content.setText(response.body()?.board?.get(0)?.contents.toString())
                                            binding.createDate.setText(response.body()?.board?.get(0)?.format_date.toString())
                                            binding.titleFreeboard.setText(response.body()?.board?.get(0)?.title.toString())
                                            binding.reaction.setText(response.body()?.board?.get(0)?.reaction_count.toString())
                                            binding.comment.setText(response.body()?.board?.get(0)?.comment.toString())
                                            if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                                user_id = response.body()?.board?.get(0)?.write_user!!.split(",").get(0)
                                            }
                                            else{
                                                user_id = response.body()?.board?.get(0)?.write_user.toString()
                                            }
//                    Toast.makeText(context,user_id+" : "+ id,Toast.LENGTH_SHORT).show()
                                            if(id.equals(user_id)){
                                                binding.freeboardMenu.setOnClickListener { view ->
                                                    val popupMenu = PopupMenu(context, view)
                                                    val inflater = popupMenu.menuInflater
                                                    inflater.inflate(R.menu.freeboard_menu, popupMenu.menu)
                                                    popupMenu.setOnMenuItemClickListener { item ->
                                                        when (item.itemId) {
                                                            R.id.exit -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                val intent = Intent(this@ReadFreeBoardActivity, ReadFreeBoardActivity::class.java)
                                                                finish()
                                                                startActivity(intent)
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            R.id.rewrite -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                var intent = Intent(this@ReadFreeBoardActivity, MakefreeboardActivity::class.java)
                                                                intent.putExtra("type", "rewrite") // 데이터 넣기
                                                                intent.putExtra("board_num", board_num)
                                                                intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                                intent.putExtra("contents", response.body()?.board?.get(0)?.contents.toString()) // 데이터 넣기
                                                                intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                                                intent.putExtra("comment", response.body()?.board?.get(0)?.comment.toString()) // 데이터 넣기
                                                                intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                                intent.putExtra("hiden", response.body()?.board?.get(0)?.hidden_name.toString()) // 데이터 넣기
                                                                startActivity(intent)
                                                                finish()
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            R.id.delete -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                Delete_board_APIS.create().Delete_Board_Info("free","$board_num", accessToken)?.enqueue(object :
                                                                    Callback<Delete_result> {
                                                                    override fun onResponse(
                                                                        call: Call<Delete_result>,
                                                                        response: Response<Delete_result>
                                                                    ) {
                                                                        Toast.makeText(context,response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                                        if(response.body()?.result==200){
                                                                            finish()
                                                                        }
                                                                    }
                                                                    override fun onFailure(call: Call<Delete_result>, t: Throwable) {
                                                                    }
                                                                })
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            else -> false
                                                        }
                                                    }

                                                    popupMenu.show()
                                                }
                                            }
                                            else{
                                                binding.freeboardMenu.setOnClickListener { view ->
                                                    val popupMenu = PopupMenu(context, view)
                                                    val inflater = popupMenu.menuInflater
                                                    inflater.inflate(R.menu.freeboard_menu2, popupMenu.menu)
                                                    popupMenu.setOnMenuItemClickListener { item ->
                                                        when (item.itemId) {
                                                            R.id.exit -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                val intent = Intent(this@ReadFreeBoardActivity, ReadFreeBoardActivity::class.java)
                                                                finish()
                                                                startActivity(intent)
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            else -> false
                                                        }
                                                    }

                                                    popupMenu.show()
                                                }
                                            }

                                        }
                                        else {
                                            Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                            var intent = Intent(context, StartActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }

                                    }
                                    override fun onFailure(call: Call<read_free_board_response_model>, t: Throwable) {

                                    }

                                })

                                var data_reaction = reaction_json_model(
                                    accessToken,
                                    board_num.toInt()
                                )
                                binding.commentAdd.setOnClickListener {
                                    reactionAPIS.reaction_post_users(data_reaction)?.enqueue(object :
                                        Callback<reaction_model> {
                                        override fun onResponse(
                                            call: Call<reaction_model>,
                                            response: Response<reaction_model>
                                        ) {
                                            if(response.body()?.result==200){
                                                read_free_board_APIS.get_read_free_board_Info(board_num, accessToken)?.enqueue(object :
                                                    Callback<read_free_board_response_model> {
                                                    override fun onResponse(
                                                        call: Call<read_free_board_response_model>,
                                                        response: Response<read_free_board_response_model>
                                                    ) {
                                                        if(response.body()?.result == 200){
                                                            binding.reaction.setText(response.body()?.board?.get(0)?.reaction_count.toString())
                                                            binding.comment.setText(response.body()?.board?.get(0)?.comment.toString())
                                                        }
                                                    }
                                                    override fun onFailure(call: Call<read_free_board_response_model>, t: Throwable) {

                                                    }

                                                })
                                                Toast.makeText(this@ReadFreeBoardActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()

                                            }

                                        }
                                        override fun onFailure(call: Call<reaction_model>, t: Throwable) {

                                        }

                                    })
                                }
                                binding.buttonComment.setOnClickListener {
                                    var data_comment = comment_json_model(
                                        accessToken,
                                        board_num.toInt(),
                                        0,
                                        binding.editComment.text.toString(),
                                        checked_hidden
                                    )
                                    comment_APIS.comment_post_users(data_comment)?.enqueue(object :
                                        Callback<comment_model> {
                                        override fun onResponse(
                                            call: Call<comment_model>,
                                            response: Response<comment_model>
                                        ) {
                                            if(response.body()?.result==200){

                                            }else{
                                                Toast.makeText(context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                                finish()}

                                            commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                                                Callback<comment_read_model> {
                                                override fun onResponse(
                                                    call: Call<comment_read_model>,
                                                    response: Response<comment_read_model>
                                                ) {
                                                    if(response.body()?.result==200){
                                                        comment_lists.clear()
                                                        var comment_length = response.body()?.list?.size
                                                        if(comment_length !=null) {
                                                            for (i in 0..comment_length - 1) {
                                                                comment_lists.add(
                                                                    comment_list(
                                                                        response.body()?.revers_list?.get(i)?.hidden_name.toString(),
                                                                        response.body()?.revers_list?.get(i)?.ref,
                                                                        response.body()?.revers_list?.get(i)?.user_id.toString(),
                                                                        response.body()?.revers_list?.get(i)?.num,
                                                                        response.body()?.revers_list?.get(i)?.ref_comment,
                                                                        response.body()?.revers_list?.get(i)?.comment.toString(),
                                                                        response.body()?.revers_list?.get(i)?.create_date.toString(),
                                                                        response.body()?.revers_list?.get(i)?.format_date.toString()
                                                                    )
                                                                )
                                                                var adapter = CommentListAdapter(context, comment_lists) // 어댑터를 초기화하세요
                                                                binding.recyclerComment.adapter = adapter
                                                                var totalHeight = 0
                                                                for (i in 0 until adapter.count) {
                                                                    val listItem = adapter.getView(i, null, binding.recyclerComment)
                                                                    listItem.measure(
                                                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                                    )
                                                                    totalHeight += listItem.measuredHeight
                                                                }
// 리스트뷰의 높이를 동적으로 설정
                                                                val params = binding.recyclerComment.layoutParams
                                                                params.height = totalHeight + (binding.recyclerComment.dividerHeight * (adapter.count - 1))
                                                                binding.recyclerComment.layoutParams = params
                                                                binding.recyclerComment.requestLayout()
//                                    var read_comment_adapter = comment_adapter(comment_lists)
//                                    binding.recyclerComment.adapter = read_comment_adapter
//                                    binding.recyclerComment.layoutManager = LinearLayoutManager(this@ReadFreeBoardActivity)

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        var intent = Intent(context, StartActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }

                                                }
                                                override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                                }

                                            })
                                        }
                                        override fun onFailure(call: Call<comment_model>, t: Throwable) {

                                        }

                                    })
                                    binding.editComment.setText("")
                                    hideKeyboard(this@ReadFreeBoardActivity)
                                }
                                Log.d("board_num", board_num)
                                Log.d("accessToken", accessToken)
                                commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                                    Callback<comment_read_model> {
                                    override fun onResponse(
                                        call: Call<comment_read_model>,
                                        response: Response<comment_read_model>
                                    ) {
                                        var comment_length = response.body()?.list?.size
                                        if(comment_length !=null) {
                                            for (i in 0..comment_length - 1) {
                                                comment_lists.add(
                                                    comment_list(
                                                        response.body()?.revers_list?.get(i)?.hidden_name.toString(),
                                                        response.body()?.revers_list?.get(i)?.ref,
                                                        response.body()?.revers_list?.get(i)?.user_id.toString(),
                                                        response.body()?.revers_list?.get(i)?.num,
                                                        response.body()?.revers_list?.get(i)?.ref_comment,
                                                        response.body()?.revers_list?.get(i)?.comment.toString(),
                                                        response.body()?.revers_list?.get(i)?.create_date.toString(),
                                                        response.body()?.revers_list?.get(i)?.format_date.toString()
                                                    )
                                                )

//                        var read_comment_adapter = comment_adapter(comment_lists)
//                        binding.recyclerComment.adapter = read_comment_adapter
//                        binding.recyclerComment.layoutManager = LinearLayoutManager(this@ReadFreeBoardActivity)
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                            }

                                        }
                                        var adapter = CommentListAdapter(this@ReadFreeBoardActivity, comment_lists) // 어댑터를 초기화하세요
                                        binding.recyclerComment.adapter = adapter
                                        var totalHeight = 0
                                        for (i in 0 until adapter.count) {
                                            val listItem = adapter.getView(i, null, binding.recyclerComment)
                                            listItem.measure(
                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                            )
                                            totalHeight += listItem.measuredHeight
                                        }
// 리스트뷰의 높이를 동적으로 설정
                                        val params = binding.recyclerComment.layoutParams
                                        params.height = totalHeight + (binding.recyclerComment.dividerHeight * (adapter.count - 1))
                                        binding.recyclerComment.layoutParams = params
                                        binding.recyclerComment.requestLayout()
                                    }
                                    override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                    }

                                })
                                binding.freeboardExit.setOnClickListener {
                                    finish()
                                }

                            }
                            else {
                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                Toast.makeText(TedPermissionProvider.context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }
                        }
                        override fun onFailure(call: Call<re_token_data>, t: Throwable) {

                        }

                    })
                }
            }
            override fun onFailure(call: Call<Token_data>, t: Throwable) {

            }

        })


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    }
    fun hideKeyboard(activity: Activity){
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
    }
    override fun onBackPressed() {
        finish()
        }

}