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
import com.campuslinker.app.chatting.chatting_user_APIS
import com.campuslinker.app.chatting.chatting_user_read_model
import com.campuslinker.app.chatting.into_chatting_result_model
import com.campuslinker.app.chatting.into_chatting_room_APIS
import com.campuslinker.app.chatting.into_chatting_room_model
import com.campuslinker.app.comment.Delete_comment_result
import com.campuslinker.app.comment.comment_json_model
import com.campuslinker.app.comment.comment_model
import com.campuslinker.app.comment.comment_read_model
import com.campuslinker.app.comment.delete_comment_APIS
import com.campuslinker.app.comment.make_comment_APIS
import com.campuslinker.app.comment.resd_comment_APIS

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.campuslinker.app.databinding.ReadMatchBoard2Binding
import com.campuslinker.app.read_board.read_match_board
import com.campuslinker.app.read_board.read_match_board_response_model
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.*
import kotlin.text.toDouble

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ReadMatchBoard2Binding
    val read_match_board_APIS = read_match_board.create()
    val comment_APIS = make_comment_APIS.create()
    val commnet_read_APIS = resd_comment_APIS.create()
    val into_chat_room_APIS = into_chatting_room_APIS.create()
    var board_num = token_management.prefs.getString("board_num","기본값")
    var id = token_management.prefs.getString("id","기본값")
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var room_num :String?=null
    var room_name :String?=null
    var checked_hidden = "N"
    var user_name : String ?= null
    var user_id : String ?= null
    private lateinit var map: GoogleMap
    private var lat : Double?=null
    private var lon : Double?=null
    private var location : String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReadMatchBoard2Binding.inflate(layoutInflater)
        setContentView(binding.root)

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
                if(intent.getStringExtra("board_num")?.toInt()!=null){
                board_num = intent.getStringExtra("board_num").toString()
            }
                if(response.body()?.result==200){
                    var comment_lists = ArrayList<comment_list>()
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.mapView) as SupportMapFragment
                    binding.checkBox2.setOnCheckedChangeListener{ _, isChecked ->
                        if(isChecked) {
                            checked_hidden = "Y"
                        }else{
                            checked_hidden = "N"
                        }
                    }
                    binding.freeboardExit.setOnClickListener {
                        finish()
                    }
                    read_match_board_APIS.get_read_match_board_Info(board_num, accessToken)?.enqueue(object :
                        Callback<read_match_board_response_model> {
                        override fun onResponse(
                            call: Call<read_match_board_response_model>,
                            response: Response<read_match_board_response_model>
                        ) {
                            if(response.body()?.result == 200){
                                if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                    binding.userName.setText(response.body()?.board?.get(0)?.write_user!!.split(",").get(1).toString())
                                }
                                else{
                                    binding.userName.setText(response.body()?.board?.get(0)?.write_user.toString())
                                }
                                chatting_user_APIS.create().get_chatting_room_list_info(response.body()?.board?.get(0)?.room_num.toString(),accessToken)?.enqueue(object :
                                    Callback<chatting_user_read_model> {
                                    override fun onResponse(
                                        call: Call<chatting_user_read_model>,
                                        response1: Response<chatting_user_read_model>
                                    ) {

                                        binding.userInfo.setText("모집인원 : "+response1.body()?.roomInfo?.get(0)?.userCount.toString()+" / "+response1.body()?.roomInfo?.get(0)?.maxUser.toString())

                                    }
                                    override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                    }

                                })
//                    Toast.makeText(context,response.body()?.board?.get(0)?.write_user.toString(),Toast.LENGTH_SHORT).show()
//                    Log.d("write_user", response.body()?.board?.get(0)?.write_user.toString())
                                binding.category.setText(response.body()?.board?.get(0)?.category.toString())
                                binding.content.setText(response.body()?.board?.get(0)?.contents.toString())
                                binding.deleteDate.setText("모집기한 : "+response.body()?.board?.get(0)?.format_delete_date.toString())
                                binding.stdid.setText("학번 : "+response.body()?.board?.get(0)?.student_id.toString())
                                binding.genderMatchBoard.setText("성별 : "+response.body()?.board?.get(0)?.gender.toString())
                                binding.createDate.setText(response.body()?.board?.get(0)?.format_create_date.toString())
                                binding.titleFreeboard.setText(response.body()?.board?.get(0)?.title.toString())
                                location = response.body()?.board?.get(0)?.location
                                room_num=response.body()?.board?.get(0)?.room_num.toString()
                                room_name=response.body()?.board?.get(0)?.title.toString()
                                if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                    user_id = response.body()?.board?.get(0)?.write_user!!.split(",").get(0)
                                }
                                else{
                                    user_id = response.body()?.board?.get(0)?.write_user.toString()
                                }

//                    Toast.makeText(context,user_id+" : "+ id,Toast.LENGTH_SHORT).show()
                                if(location!!.contains(",")){
                                    var loc = location!!.split(",")
                                    lat = loc.get(0).toDouble()
                                    lon = loc.get(1).toDouble()
                                    mapFragment.getMapAsync(this@MapsActivity)
                                }
                                binding.plusMap.setOnClickListener {
                                    if(location!!.contains(",")){
                                        var loc = location!!.split(",")
                                        lat = loc.get(0).toDouble()
                                        lon = loc.get(1).toDouble()
                                        var intent = Intent(this@MapsActivity, Read_matchboard_maps::class.java)
                                        intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                        intent.putExtra("lat", lat.toString()) // 데이터 넣기
                                        intent.putExtra("lon", lon.toString()) // 데이터 넣기
                                        startActivity(intent)
                                    }
                                }
                                if(id.equals(user_id)){
                                    binding.freeboardMenu.setOnClickListener { view ->
                                        val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                                        val inflater = popupMenu.menuInflater
                                        inflater.inflate(R.menu.freeboard_menu, popupMenu.menu)
                                        popupMenu.setOnMenuItemClickListener { item ->
                                            when (item.itemId) {
                                                R.id.exit -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    val intent = Intent(this@MapsActivity, MapsActivity::class.java)
                                                    finish()
                                                    startActivity(intent)
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                R.id.rewrite -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    var intent = Intent(this@MapsActivity, GoogleMaps::class.java)
                                                    intent.putExtra("type", "rewrite") // 데이터 넣기
                                                    intent.putExtra("board_num", board_num)
                                                    intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                    intent.putExtra("contents", response.body()?.board?.get(0)?.contents.toString()) // 데이터 넣기
                                                    intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                                    intent.putExtra("hiden", response.body()?.board?.get(0)?.hidden_name.toString()) // 데이터 넣기
                                                    intent.putExtra("gender", response.body()?.board?.get(0)?.gender.toString()) // 데이터 넣기
                                                    intent.putExtra("lat", lat.toString()) // 데이터 넣기
                                                    intent.putExtra("lon", lon.toString()) // 데이터 넣기
                                                    startActivity(intent)
                                                    finish()
                                                    // 원하는 동작을 수행
                                                    true
                                                }
                                                R.id.delete -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    Delete_board_APIS.create().Delete_Board_Info("match","$board_num", accessToken)?.enqueue(object :
                                                        Callback<Delete_result> {
                                                        override fun onResponse(
                                                            call: Call<Delete_result>,
                                                            response: Response<Delete_result>
                                                        ) {
                                                            Toast.makeText(TedPermissionProvider.context,response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
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
                                        val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                                        val inflater = popupMenu.menuInflater
                                        inflater.inflate(R.menu.freeboard_menu2, popupMenu.menu)
                                        popupMenu.setOnMenuItemClickListener { item ->
                                            when (item.itemId) {
                                                R.id.exit -> {
                                                    // 메뉴 항목 1이 선택된 경우
                                                    val intent = Intent(this@MapsActivity, MapsActivity::class.java)
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
                            else{
                                Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                        }
                        override fun onFailure(call: Call<read_match_board_response_model>, t: Throwable) {

                        }

                    })
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
                                if(response.body()?.result==200)
                                {
                                    Toast.makeText(this@MapsActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                                        Callback<comment_read_model> {
                                        override fun onResponse(
                                            call: Call<comment_read_model>,
                                            response: Response<comment_read_model>
                                        ) {
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
                                                    var adapter = CommentListAdapter(TedPermissionProvider.context, comment_lists) // 어댑터를 초기화하세요
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
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }
                                            }
                                        }
                                        override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                        }

                                    })
                                }else{
                                    Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                    var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }


                            }
                            override fun onFailure(call: Call<comment_model>, t: Throwable) {

                            }

                        })
                        binding.editComment.setText("")
                        hideKeyboard(this@MapsActivity)

                    }
                    Log.d("board_num", board_num)
                    Log.d("accessToken", accessToken)
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

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        var adapter = CommentListAdapter(TedPermissionProvider.context, comment_lists) // 어댑터를 초기화하세요
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
                                }


                            }else{
                                Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                startActivity(intent)
                                finish()
                            }


                        }
                        override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                        }

                    })
                    binding.buttonComment2.setOnClickListener {
                        val chatting_into_model = into_chatting_room_model(
                            accessToken,
                            "visite",
                            board_num.toInt()
                        )
                        into_chat_room_APIS.into_chatting_room_info(chatting_into_model)?.enqueue(object :
                            Callback<into_chatting_result_model> {
                            override fun onResponse(
                                call: Call<into_chatting_result_model>,
                                response: Response<into_chatting_result_model>
                            ) {
                                if(response.body()?.result==200){
                                    var intent = Intent(this@MapsActivity, ChattingActivity::class.java)
                                    intent.putExtra("chatting_room_num", room_num) // 데이터 넣기
                                    intent.putExtra("chatting_room_name", room_name) // 데이터 넣기
                                    startActivity(intent)
                                }
                                Toast.makeText(this@MapsActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()



                            }
                            override fun onFailure(call: Call<into_chatting_result_model>, t: Throwable) {

                            }

                        })
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
                                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                                val mapFragment = supportFragmentManager
                                    .findFragmentById(R.id.mapView) as SupportMapFragment
                                binding.checkBox2.setOnCheckedChangeListener{ _, isChecked ->
                                    if(isChecked) {
                                        checked_hidden = "Y"
                                    }else{
                                        checked_hidden = "N"
                                    }
                                }
                                binding.freeboardExit.setOnClickListener {
                                    finish()
                                }
                                read_match_board_APIS.get_read_match_board_Info(board_num, accessToken)?.enqueue(object :
                                    Callback<read_match_board_response_model> {
                                    override fun onResponse(
                                        call: Call<read_match_board_response_model>,
                                        response: Response<read_match_board_response_model>
                                    ) {
                                        if(response.body()?.result == 200){
                                            if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                                binding.userName.setText(response.body()?.board?.get(0)?.write_user!!.split(",").get(1).toString())
                                            }
                                            else{
                                                binding.userName.setText(response.body()?.board?.get(0)?.write_user.toString())
                                            }
                                            chatting_user_APIS.create().get_chatting_room_list_info(response.body()?.board?.get(0)?.room_num.toString(),accessToken)?.enqueue(object :
                                                Callback<chatting_user_read_model> {
                                                override fun onResponse(
                                                    call: Call<chatting_user_read_model>,
                                                    response1: Response<chatting_user_read_model>
                                                ) {

                                                    binding.userInfo.setText("모집인원 : "+response1.body()?.roomInfo?.get(0)?.userCount.toString()+" / "+response1.body()?.roomInfo?.get(0)?.maxUser.toString())

                                                }
                                                override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                                }

                                            })
//                    Toast.makeText(context,response.body()?.board?.get(0)?.write_user.toString(),Toast.LENGTH_SHORT).show()
//                    Log.d("write_user", response.body()?.board?.get(0)?.write_user.toString())
                                            binding.category.setText(response.body()?.board?.get(0)?.category.toString())
                                            binding.content.setText(response.body()?.board?.get(0)?.contents.toString())
                                            binding.deleteDate.setText("모집기한 : "+response.body()?.board?.get(0)?.format_delete_date.toString())
                                            binding.stdid.setText("학번 : "+response.body()?.board?.get(0)?.student_id.toString())
                                            binding.genderMatchBoard.setText("성별 : "+response.body()?.board?.get(0)?.gender.toString())
                                            binding.createDate.setText(response.body()?.board?.get(0)?.format_create_date.toString())
                                            binding.titleFreeboard.setText(response.body()?.board?.get(0)?.title.toString())
                                            location = response.body()?.board?.get(0)?.location
                                            room_num=response.body()?.board?.get(0)?.room_num.toString()
                                            room_name=response.body()?.board?.get(0)?.title.toString()
                                            if(response.body()?.board?.get(0)?.write_user!!.contains(",")){
                                                user_id = response.body()?.board?.get(0)?.write_user!!.split(",").get(0)
                                            }
                                            else{
                                                user_id = response.body()?.board?.get(0)?.write_user.toString()
                                            }

//                    Toast.makeText(context,user_id+" : "+ id,Toast.LENGTH_SHORT).show()
                                            if(location!!.contains(",")){
                                                var loc = location!!.split(",")
                                                lat = loc.get(0).toDouble()
                                                lon = loc.get(1).toDouble()
                                                mapFragment.getMapAsync(this@MapsActivity)
                                            }
                                            binding.plusMap.setOnClickListener {
                                                if(location!!.contains(",")){
                                                    var loc = location!!.split(",")
                                                    lat = loc.get(0).toDouble()
                                                    lon = loc.get(1).toDouble()
                                                    var intent = Intent(this@MapsActivity, Read_matchboard_maps::class.java)
                                                    intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                                    intent.putExtra("lat", lat.toString()) // 데이터 넣기
                                                    intent.putExtra("lon", lon.toString()) // 데이터 넣기
                                                    startActivity(intent)
                                                }
                                            }
                                            if(id.equals(user_id)){
                                                binding.freeboardMenu.setOnClickListener { view ->
                                                    val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                                                    val inflater = popupMenu.menuInflater
                                                    inflater.inflate(R.menu.freeboard_menu, popupMenu.menu)
                                                    popupMenu.setOnMenuItemClickListener { item ->
                                                        when (item.itemId) {
                                                            R.id.exit -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                val intent = Intent(this@MapsActivity, MapsActivity::class.java)
                                                                finish()
                                                                startActivity(intent)
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            R.id.rewrite -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                var intent = Intent(this@MapsActivity, GoogleMaps::class.java)
                                                                intent.putExtra("type", "rewrite") // 데이터 넣기
                                                                intent.putExtra("board_num", board_num)
                                                                intent.putExtra("category", response.body()?.board?.get(0)?.category.toString()) // 데이터 넣기
                                                                intent.putExtra("contents", response.body()?.board?.get(0)?.contents.toString()) // 데이터 넣기
                                                                intent.putExtra("title", response.body()?.board?.get(0)?.title.toString()) // 데이터 넣기
                                                                intent.putExtra("hiden", response.body()?.board?.get(0)?.hidden_name.toString()) // 데이터 넣기
                                                                intent.putExtra("gender", response.body()?.board?.get(0)?.gender.toString()) // 데이터 넣기
                                                                intent.putExtra("lat", lat.toString()) // 데이터 넣기
                                                                intent.putExtra("lon", lon.toString()) // 데이터 넣기
                                                                startActivity(intent)
                                                                finish()
                                                                // 원하는 동작을 수행
                                                                true
                                                            }
                                                            R.id.delete -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                Delete_board_APIS.create().Delete_Board_Info("match","$board_num", accessToken)?.enqueue(object :
                                                                    Callback<Delete_result> {
                                                                    override fun onResponse(
                                                                        call: Call<Delete_result>,
                                                                        response: Response<Delete_result>
                                                                    ) {
                                                                        Toast.makeText(TedPermissionProvider.context,response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
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
                                                    val popupMenu = PopupMenu(TedPermissionProvider.context, view)
                                                    val inflater = popupMenu.menuInflater
                                                    inflater.inflate(R.menu.freeboard_menu2, popupMenu.menu)
                                                    popupMenu.setOnMenuItemClickListener { item ->
                                                        when (item.itemId) {
                                                            R.id.exit -> {
                                                                // 메뉴 항목 1이 선택된 경우
                                                                val intent = Intent(this@MapsActivity, MapsActivity::class.java)
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
                                        else{
                                            Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                            var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }

                                    }
                                    override fun onFailure(call: Call<read_match_board_response_model>, t: Throwable) {

                                    }

                                })
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
                                            if(response.body()?.result==200)
                                            {
                                                Toast.makeText(this@MapsActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                commnet_read_APIS.get_comment_read(board_num,accessToken)?.enqueue(object :
                                                    Callback<comment_read_model> {
                                                    override fun onResponse(
                                                        call: Call<comment_read_model>,
                                                        response: Response<comment_read_model>
                                                    ) {
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
                                                                var adapter = CommentListAdapter(TedPermissionProvider.context, comment_lists) // 어댑터를 초기화하세요
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
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                            }
                                                        }
                                                    }
                                                    override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                                    }

                                                })
                                            }else{
                                                Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }


                                        }
                                        override fun onFailure(call: Call<comment_model>, t: Throwable) {

                                        }

                                    })
                                    binding.editComment.setText("")
                                    hideKeyboard(this@MapsActivity)

                                }
                                Log.d("board_num", board_num)
                                Log.d("accessToken", accessToken)
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

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    var adapter = CommentListAdapter(TedPermissionProvider.context, comment_lists) // 어댑터를 초기화하세요
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
                                            }


                                        }else{
                                            Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                            var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }


                                    }
                                    override fun onFailure(call: Call<comment_read_model>, t: Throwable) {

                                    }

                                })
                                binding.buttonComment2.setOnClickListener {
                                    val chatting_into_model = into_chatting_room_model(
                                        accessToken,
                                        "visite",
                                        board_num.toInt()
                                    )
                                    into_chat_room_APIS.into_chatting_room_info(chatting_into_model)?.enqueue(object :
                                        Callback<into_chatting_result_model> {
                                        override fun onResponse(
                                            call: Call<into_chatting_result_model>,
                                            response: Response<into_chatting_result_model>
                                        ) {
                                            if(response.body()?.result==200){
                                                var intent = Intent(this@MapsActivity, ChattingActivity::class.java)
                                                intent.putExtra("chatting_room_num", room_num) // 데이터 넣기
                                                intent.putExtra("chatting_room_name", room_name) // 데이터 넣기
                                                startActivity(intent)
                                            }
                                            Toast.makeText(this@MapsActivity, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()



                                        }
                                        override fun onFailure(call: Call<into_chatting_result_model>, t: Throwable) {

                                        }

                                    })
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the came
        mMap.addMarker(MarkerOptions().position(LatLng(lat!!, lon!!)).draggable(true).title("지정 위치"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat!!, lon!!)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lon!!), 15f))
    }
    fun hideKeyboard(activity: Activity){
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.window.decorView.applicationWindowToken, 0)
    }
    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("MM/dd", Locale.ENGLISH)
        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}