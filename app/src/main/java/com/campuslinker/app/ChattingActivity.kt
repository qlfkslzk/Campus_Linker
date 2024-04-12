package com.campuslinker.app

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.campuslinker.app.adapter.chatting_adapter
import com.campuslinker.app.adapter.chatting_list
import com.campuslinker.app.chatting.chatting_clear_APIS
import com.campuslinker.app.chatting.chatting_clear_model
import com.campuslinker.app.chatting.chatting_list_APIS
import com.campuslinker.app.chatting.chatting_room_read_model
import com.campuslinker.app.chatting.chatting_user_APIS
import com.campuslinker.app.chatting.chatting_user_read_model
import com.campuslinker.app.chatting.clear_chatting_result_model
import com.campuslinker.app.chatting.exit_chatting_result_model
import com.campuslinker.app.chatting.exit_chatting_room_APIS
import com.campuslinker.app.chatting.send_chatting_APIS
import com.campuslinker.app.chatting.send_chatting_model
import com.campuslinker.app.chatting.send_result_model
import com.campuslinker.app.databinding.ChattingBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChattingActivity : AppCompatActivity() {
    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable
    val Get_user_info_APIS = Get_token_APIS.create()
    private lateinit var binding: ChattingBinding
    val send_chat_APIS = send_chatting_APIS.create()
    var room_name : String ?=null
    val exit_chatting_APIS = exit_chatting_room_APIS.create()
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var refreshToken = token_management.prefs.getString("refresh_token","기본값")
    val url2 = "wss://sirobako.co.kr/campus-linker/socket/websocket"
    val intervalMillis = 5000L
    val client = OkHttpClient()
    private var isOpen = false
    var my_id :String?=null
    var id_info:Int=0
    var chatting_listarray =arrayListOf<chatting_list>()
    val chatting_APIS = chatting_list_APIS.create()
    var a : Int?=null
    var b : Int?=null
    var c : String?=null
    var d : String?=null
    var e : String?=null
    var f : String?=null
    var clear_APIS = chatting_clear_APIS.create()
    var page = 0
    var room_num = token_management.prefs.getString("chatting_room_num","기본값")
//    val stomp = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
    val stomp = StompClient(client, intervalMillis).apply{this@apply.url=url2}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        val animator = binding.chattingRecycleview?.itemAnimator     //리사이클러뷰 애니메이터 get
        if (animator is SimpleItemAnimator){          //아이템 애니메이커 기본 하위클래스
            animator.supportsChangeAnimations = false  //애니메이션 값 false (리사이클러뷰가 화면을 다시 갱신 했을때 뷰들의 깜빡임 방지)
        }
        val title = intent.getStringExtra("title").toString()

        Log.d("before_num", token_management.prefs.getString("chatting_room_num","기본값"))
        room_name = intent.getStringExtra("chatting_room_name").toString()
        Log.d("after_num", token_management.prefs.getString("chatting_room_num","기본값"))
        token_management.prefs.removeString("chatting_room_num")
        binding.chattingTitle.setText(room_name)
        Log.i("roomnum", room_num!!)
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        Log.d("access_token", accessToken)
        Log.d("refreshToken", refreshToken)
        val refresh_token_data = re_token_model(
            refreshToken
        )
        if(intent.getStringExtra("chatting_room_num")?.toInt()!=null){
            room_num = intent.getStringExtra("chatting_room_num").toString()
        }
        Log.d("chatting_room_num", intent.getStringExtra("chatting_room_num").toString())
        Get_token_APIS.create().gettokenlInfo(accessToken)?.enqueue(object :
            Callback<Token_data> {
            override fun onResponse(
                call: Call<Token_data>,
                response: Response<Token_data>
            ) {
                if(response.body()?.result==200){
                    var chatting_list_adapter = chatting_adapter(chatting_listarray)
                    binding.chattingRecycleview.adapter = chatting_list_adapter
                    binding.chattingRecycleview.addOnLayoutChangeListener(onLayoutChangeListener)
                    binding.chattingRecycleview.viewTreeObserver.addOnScrollChangedListener {
                        if (binding.chattingRecycleview.isScrollable() && !isOpen) { // 스크롤이 가능하면서 키보드가 닫힌 상태일 떄만
                            binding.chattingRecycleview.setStackFromEnd()
                            binding.chattingRecycleview.removeOnLayoutChangeListener(onLayoutChangeListener)
                        }
                    }

                    var clear_model = chatting_clear_model(
                        room_num.toInt(),
                        accessToken
                    )
                    clear_APIS.chatting_clear_info(clear_model)?.enqueue(object :
                        Callback<clear_chatting_result_model> {
                        override fun onResponse(
                            call: Call<clear_chatting_result_model>,
                            response: Response<clear_chatting_result_model>
                        ) {
//                Toast.makeText(context, response.body()?.result.toString(), Toast.LENGTH_SHORT).show()
                        }
                        override fun onFailure(call: Call<clear_chatting_result_model>, t: Throwable) {

                        }

                    })

                    chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"$page")?.enqueue(object :
                        Callback<chatting_room_read_model> {
                        override fun onResponse(
                            call: Call<chatting_room_read_model>,
                            response: Response<chatting_room_read_model>
                        ) {
                            chatting_user_APIS.create().get_chatting_room_list_info(room_num.toString(),accessToken)?.enqueue(object :
                                Callback<chatting_user_read_model> {
                                override fun onResponse(
                                    call: Call<chatting_user_read_model>,
                                    response1: Response<chatting_user_read_model>
                                ) {

                                    binding.chattingTitle.setText(response.body()?.room_name.toString()+" ("+response1.body()?.roomInfo?.get(0)?.userCount.toString()+"/"+response1.body()?.roomInfo?.get(0)?.maxUser.toString()+")")

                                }
                                override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                }

                            })

                            chatting_listarray.clear()
                            var free_length = response.body()?.list?.size
                            if(free_length !=null) {
                                for (i in 0..free_length - 1) {
                                    chatting_listarray.add(
                                        chatting_list(
                                            response.body()?.list?.get(i)?.num,
                                            response.body()?.list?.get(i)?.room,
                                            response.body()?.list?.get(i)?.user_id.toString(),
                                            response.body()?.list?.get(i)?.chat.toString(),
                                            response.body()?.list?.get(i)?.create_date.toString(),
                                            response.body()?.list?.get(i)?.format_date.toString()
                                        )
                                    )

                                }
                                chatting_listarray.reverse()
                                binding.chattingRecycleview.layoutManager = LinearLayoutManager(this@ChattingActivity)
                                binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
                                binding.chattingRecycleview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)
                                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                        if (firstVisibleItemPosition == 0) {
                                            // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                            // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                                            val firstVisibleView = layoutManager.findViewByPosition(firstVisibleItemPosition)
                                            val offset = firstVisibleView?.top ?: 0
                                            Log.d("before page ","$page")
                                            page = page + 1
                                            Log.d("after page ","$page")
                                            chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"$page")?.enqueue(object :
                                                Callback<chatting_room_read_model> {
                                                override fun onResponse(
                                                    call: Call<chatting_room_read_model>,
                                                    response: Response<chatting_room_read_model>
                                                ) {
                                                    if(response.body()?.result==200){
                                                        Log.d("page 이후",response.body()?.message.toString())
                                                        chatting_user_APIS.create().get_chatting_room_list_info(room_num.toString(),accessToken)?.enqueue(object :
                                                            Callback<chatting_user_read_model> {
                                                            override fun onResponse(
                                                                call: Call<chatting_user_read_model>,
                                                                response1: Response<chatting_user_read_model>
                                                            ) {

                                                                binding.chattingTitle.setText(response.body()?.room_name.toString()+" ("+response1.body()?.roomInfo?.get(0)?.userCount.toString()+"/"+response1.body()?.roomInfo?.get(0)?.maxUser.toString()+")")

                                                            }
                                                            override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                                            }

                                                        })
                                                        Log.d("position", layoutManager.findLastVisibleItemPosition().toString())
                                                        var free_length = response.body()?.list?.size
                                                        if(free_length !=null) {
                                                            for (i in 0..free_length - 1) {
                                                                chatting_listarray.add(0,
                                                                    chatting_list(
                                                                        response.body()?.list?.get(i)?.num,
                                                                        response.body()?.list?.get(i)?.room,
                                                                        response.body()?.list?.get(i)?.user_id.toString(),
                                                                        response.body()?.list?.get(i)?.chat.toString(),
                                                                        response.body()?.list?.get(i)?.create_date.toString(),
                                                                        response.body()?.list?.get(i)?.format_date.toString()
                                                                    )
                                                                )



                                                            }
                                                            chatting_list_adapter.notifyItemRangeInserted(0, free_length)
                                                            Log.d("change_position", layoutManager.findLastVisibleItemPosition().toString())

                                                            binding.chattingRecycleview.scrollToPosition(lastVisibleItemPosition+free_length-1)
                                                            Log.d("change_position2", layoutManager.findLastVisibleItemPosition().toString())
                                                        }

                                                    }
                                                }
                                                override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {

                                                }

                                            })
                                        }
                                    }
                                })
                            }}
                        override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {

                        }

                    })

//        Toast.makeText(this@ChattingActivity, room_num, Toast.LENGTH_SHORT).show()
                    stompConnection = stomp.connect().subscribe {
                        when (it.type) {
                            Event.Type.OPENED -> {
                                Log.i("OPEND", "!!")
                            }
                            Event.Type.CLOSED -> {
                                Log.i("CLOSED", "!!")
                            }
                            Event.Type.ERROR -> {
                                Log.i("ERROR", "!!")
                                Log.e("CONNECT ERROR", it.exception.toString())
                            }

                            else -> {

                            }
                        }
                    }
                    topic = stomp.join("/sub/chat/room/$room_num").subscribe {
            try {
                val a = JSONObject(it).getString("num").toInt()
                val b = JSONObject(it).getString("room").toInt()
                val c = JSONObject(it).getString("user_id").toString()
                val d = JSONObject(it).getString("chat").toString()
                val e = JSONObject(it).getString("create_date").toString()
                val f = JSONObject(it).getString("format_date").toString()
                Log.d("jsonobject", "${JSONObject(it)}")
                val chatting_data = chatting_list(a, b, c, d, e, f)
                val layoutManager = binding.chattingRecycleview.layoutManager as LinearLayoutManager

// 아이템 추가 이전의 스크롤 위치
                val previousScrollPosition = layoutManager.findLastVisibleItemPosition()
                chatting_listarray.add(chatting_data)
                runOnUiThread {
                    // UI 업데이트 코드
                    chatting_list_adapter.notifyItemInserted(chatting_listarray.size - 1)
                    val totalItemCount = chatting_listarray.size - 1
                    val newScrollPosition = if (previousScrollPosition == totalItemCount - 1) {
                        totalItemCount
                    } else {
                        previousScrollPosition - 1
                    }

// 스크롤 위치를 변경하려면 스크롤뷰를 post 및 scrollToPosition 메서드를 사용합니다.
                    binding.chattingRecycleview.post {
                        binding.chattingRecycleview.scrollToPosition(newScrollPosition)
                    }
                }
            } catch (e: JSONException) {
                // JSON 파싱 예외 처리
                e.printStackTrace()
            }
                        clear_APIS.chatting_clear_info(clear_model)?.enqueue(object :
                            Callback<clear_chatting_result_model> {
                            override fun onResponse(
                                call: Call<clear_chatting_result_model>,
                                response: Response<clear_chatting_result_model>
                            ) {

                            }
                            override fun onFailure(call: Call<clear_chatting_result_model>, t: Throwable) {

                            }

                        })
                    }
//        topic = stomp.join("/sub/chat/room/$room_num").subscribe {
//            chatting_listarray.clear()
//            chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"0")?.enqueue(object :
//                Callback<chatting_room_read_model> {
//                override fun onResponse(
//                    call: Call<chatting_room_read_model>,
//                    response: Response<chatting_room_read_model>
//                ) {
//                    chatting_listarray.clear()
//                    var free_length = response.body()?.list?.size
//                    if(free_length !=null) {
//                        for (i in 0..free_length - 1) {
//
//                            Log.i("receive_id", response.body()?.list?.get(i)?.user_id.toString())
//                            chatting_listarray.add(
//                                chatting_list(
//                                    response.body()?.list?.get(i)?.num,
//                                    response.body()?.list?.get(i)?.room,
//                                    response.body()?.list?.get(i)?.user_id.toString(),
//                                    response.body()?.list?.get(i)?.chat.toString(),
//                                    response.body()?.list?.get(i)?.create_date.toString(),
//                                    response.body()?.list?.get(i)?.format_date.toString()
//                                )
//                            )
//
//                        }
//                        chatting_listarray.reverse()
//                        binding.chattingRecycleview.layoutManager = LinearLayoutManager(this@ChattingActivity)
//                        binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
//                    }}
//                override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {
//
//                }
//
//            })
//        }



//        stomp.lifecycle().subscribe { lifecycleEvent ->
//            when (lifecycleEvent.type) {
//                LifecycleEvent.Type.OPENED -> {
//                    Log.i("OPEND", "!!")
//                }
//                LifecycleEvent.Type.CLOSED -> {
//                    Log.i("CLOSED", "!!")
//                }
//                LifecycleEvent.Type.ERROR -> {
//                    Log.i("ERROR", "!!")
//                    Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
//                }
//                else ->{
//                    Log.i("ELSE", lifecycleEvent.message)
//                }
//            }
//        }
//        stomp.connect()
//        stomp.topic("/sub/chat/room/$room_num").subscribe { topicMessage ->
//            Log.i("message Recieve", topicMessage.payload)
//        }
                    binding.chattingExit.setOnClickListener {
                        stompConnection.dispose()
                        finish()
                    }
                    binding.chattingMenu.setOnClickListener { view ->
                        val popupMenu = PopupMenu(this@ChattingActivity, view)
                        val inflater = popupMenu.menuInflater
                        inflater.inflate(R.menu.chating_menu, popupMenu.menu)

                        popupMenu.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.exit -> {
                                    // 메뉴 항목 1이 선택된 경우
                                    exit_chatting_APIS.chatting_clear_info(room_num.toString(), accessToken)?.enqueue(object :
                                        Callback<exit_chatting_result_model> {
                                        override fun onResponse(
                                            call: Call<exit_chatting_result_model>,
                                            response: Response<exit_chatting_result_model>
                                        ) {
                                            Toast.makeText(this@ChattingActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                                            stompConnection.dispose()
                                            finish()
                                        }
                                        override fun onFailure(call: Call<exit_chatting_result_model>, t: Throwable) {

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
                    binding.sendChat.setOnClickListener {
                        var text = binding.editChat.text.toString()
                        if(binding.editChat.text.toString().equals("")) {
                            Toast.makeText(this@ChattingActivity, "텍스트를 입력해주세요",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            binding.editChat.setText("")
                            var send_chat_model = send_chatting_model(
                                accessToken,
                                text,
                                room_num!!.toInt()
                            )
                            send_chat_APIS.send_chatting_info(send_chat_model)?.enqueue(object :
                                Callback<send_result_model> {
                                override fun onResponse(
                                    call: Call<send_result_model>,
                                    response: Response<send_result_model>
                                ) {
                                    if(response.body()?.result==200){
                                        binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
                                    }else{
                                        Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                        var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                        startActivity(intent)
                                        stompConnection.dispose()
                                        finish()
                                    }

//                        Toast.makeText(this@ChattingActivity, room_num.toString(), Toast.LENGTH_SHORT).show()

//                        Toast.makeText(this@ChattingActivity, room_num, Toast.LENGTH_SHORT).show()
                                }
                                override fun onFailure(call: Call<send_result_model>, t: Throwable) {

                                }
                            })
                        }
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
                                var chatting_list_adapter = chatting_adapter(chatting_listarray)
                                binding.chattingRecycleview.adapter = chatting_list_adapter
                                binding.chattingRecycleview.addOnLayoutChangeListener(onLayoutChangeListener)
                                binding.chattingRecycleview.viewTreeObserver.addOnScrollChangedListener {
                                    if (binding.chattingRecycleview.isScrollable() && !isOpen) { // 스크롤이 가능하면서 키보드가 닫힌 상태일 떄만
                                        binding.chattingRecycleview.setStackFromEnd()
                                        binding.chattingRecycleview.removeOnLayoutChangeListener(onLayoutChangeListener)
                                    }
                                }
                                var clear_model = chatting_clear_model(
                                    room_num!!.toInt(),
                                    accessToken
                                )
                                clear_APIS.chatting_clear_info(clear_model)?.enqueue(object :
                                    Callback<clear_chatting_result_model> {
                                    override fun onResponse(
                                        call: Call<clear_chatting_result_model>,
                                        response: Response<clear_chatting_result_model>
                                    ) {
//                Toast.makeText(context, response.body()?.result.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                    override fun onFailure(call: Call<clear_chatting_result_model>, t: Throwable) {

                                    }

                                })

                                chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"0")?.enqueue(object :
                                    Callback<chatting_room_read_model> {
                                    override fun onResponse(
                                        call: Call<chatting_room_read_model>,
                                        response: Response<chatting_room_read_model>
                                    ) {
                                        chatting_user_APIS.create().get_chatting_room_list_info(room_num.toString(),accessToken)?.enqueue(object :
                                            Callback<chatting_user_read_model> {
                                            override fun onResponse(
                                                call: Call<chatting_user_read_model>,
                                                response1: Response<chatting_user_read_model>
                                            ) {

                                                binding.chattingTitle.setText(response.body()?.room_name.toString()+" ("+response1.body()?.roomInfo?.get(0)?.userCount.toString()+"/"+response1.body()?.roomInfo?.get(0)?.maxUser.toString()+")")

                                            }
                                            override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                            }

                                        })

                                        chatting_listarray.clear()
                                        var free_length = response.body()?.list?.size
                                        if(free_length !=null) {
                                            for (i in 0..free_length - 1) {

                                                Log.i("receive_id", response.body()?.list?.get(i)?.user_id.toString())
                                                chatting_listarray.add(
                                                    chatting_list(
                                                        response.body()?.list?.get(i)?.num,
                                                        response.body()?.list?.get(i)?.room,
                                                        response.body()?.list?.get(i)?.user_id.toString(),
                                                        response.body()?.list?.get(i)?.chat.toString(),
                                                        response.body()?.list?.get(i)?.create_date.toString(),
                                                        response.body()?.list?.get(i)?.format_date.toString()
                                                    )
                                                )

                                            }
                                            chatting_listarray.reverse()
                                            binding.chattingRecycleview.layoutManager = LinearLayoutManager(this@ChattingActivity)
                                            binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
                                            binding.chattingRecycleview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                                    super.onScrolled(recyclerView, dx, dy)
                                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                                    if (firstVisibleItemPosition == 0) {
                                                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                                                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                                                        val firstVisibleView = layoutManager.findViewByPosition(firstVisibleItemPosition)
                                                        val offset = firstVisibleView?.top ?: 0
                                                        // 마지막 아이템에 도달했을 때 원하는 동작 수행
                                                        // 이때, 리사이클러뷰의 데이터를 더 가져오거나 다른 작업을 수행할 수 있습니다.
                                                        page = page + 1
                                                        chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"$page")?.enqueue(object :
                                                            Callback<chatting_room_read_model> {
                                                            override fun onResponse(
                                                                call: Call<chatting_room_read_model>,
                                                                response: Response<chatting_room_read_model>
                                                            ) {
                                                                chatting_user_APIS.create().get_chatting_room_list_info(room_num.toString(),accessToken)?.enqueue(object :
                                                                    Callback<chatting_user_read_model> {
                                                                    override fun onResponse(
                                                                        call: Call<chatting_user_read_model>,
                                                                        response1: Response<chatting_user_read_model>
                                                                    ) {

                                                                        binding.chattingTitle.setText(response.body()?.room_name.toString()+" ("+response1.body()?.roomInfo?.get(0)?.userCount.toString()+"/"+response1.body()?.roomInfo?.get(0)?.maxUser.toString()+")")

                                                                    }
                                                                    override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {

                                                                    }

                                                                })

                                                                var free_length = response.body()?.list?.size
                                                                if(free_length !=null) {
                                                                    for (i in 0..free_length - 1) {
                                                                        chatting_listarray.add(0,
                                                                            chatting_list(
                                                                                response.body()?.list?.get(i)?.num,
                                                                                response.body()?.list?.get(i)?.room,
                                                                                response.body()?.list?.get(i)?.user_id.toString(),
                                                                                response.body()?.list?.get(i)?.chat.toString(),
                                                                                response.body()?.list?.get(i)?.create_date.toString(),
                                                                                response.body()?.list?.get(i)?.format_date.toString()
                                                                            )
                                                                        )

                                                                    }
                                                                    chatting_list_adapter.notifyItemRangeInserted(0, free_length)
                                                                    Log.d("change_position", layoutManager.findLastVisibleItemPosition().toString())

                                                                    binding.chattingRecycleview.scrollToPosition(lastVisibleItemPosition+free_length-1)
                                                                    Log.d("change_position2", layoutManager.findLastVisibleItemPosition().toString())

                                                                }}

                                                            override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {

                                                            }

                                                        })
                                                    }
                                                }
                                            })
                                        }}
                                    override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {

                                    }

                                })
//        Toast.makeText(this@ChattingActivity, room_num, Toast.LENGTH_SHORT).show()
                                stompConnection = stomp.connect().subscribe {
                                    when (it.type) {
                                        Event.Type.OPENED -> {
                                            Log.i("OPEND", "!!")
                                        }
                                        Event.Type.CLOSED -> {
                                            Log.i("CLOSED", "!!")
                                        }
                                        Event.Type.ERROR -> {
                                            Log.i("ERROR", "!!")
                                            Log.e("CONNECT ERROR", it.exception.toString())
                                        }

                                        else -> {

                                        }
                                    }
                                }
                                topic = stomp.join("/sub/chat/room/$room_num").subscribe {
                                    try {
                                        val a = JSONObject(it).getString("num").toInt()
                                        val b = JSONObject(it).getString("room").toInt()
                                        val c = JSONObject(it).getString("user_id").toString()
                                        val d = JSONObject(it).getString("chat").toString()
                                        val e = JSONObject(it).getString("create_date").toString()
                                        val f = JSONObject(it).getString("format_date").toString()

                                        val chatting_data = chatting_list(a, b, c, d, e, f)
                                        val layoutManager = binding.chattingRecycleview.layoutManager as LinearLayoutManager

// 아이템 추가 이전의 스크롤 위치
                                        val previousScrollPosition = chatting_listarray.size - 1
                                        chatting_listarray.add(chatting_data)
                                        runOnUiThread {
                                            // UI 업데이트 코드
                                            chatting_list_adapter.notifyItemInserted(chatting_listarray.size - 1)
                                            val totalItemCount = chatting_listarray.size - 1
                                            val newScrollPosition = if (previousScrollPosition == totalItemCount - 1) {
                                                totalItemCount
                                            } else {
                                                previousScrollPosition - 1
                                            }

// 스크롤 위치를 변경하려면 스크롤뷰를 post 및 scrollToPosition 메서드를 사용합니다.
                                            binding.chattingRecycleview.post {
                                                binding.chattingRecycleview.scrollToPosition(newScrollPosition)
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    clear_APIS.chatting_clear_info(clear_model)?.enqueue(object :
                                        Callback<clear_chatting_result_model> {
                                        override fun onResponse(
                                            call: Call<clear_chatting_result_model>,
                                            response: Response<clear_chatting_result_model>
                                        ) {

                                        }
                                        override fun onFailure(call: Call<clear_chatting_result_model>, t: Throwable) {

                                        }

                                    })
//                                    chatting_listarray.clear()
//                                    chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"0")?.enqueue(object :
//                                        Callback<chatting_room_read_model> {
//                                        override fun onResponse(
//                                            call: Call<chatting_room_read_model>,
//                                            response: Response<chatting_room_read_model>
//                                        ) {
//                                            if(response.body()?.result==200){
//                                                chatting_user_APIS.create().get_chatting_room_list_info(room_num.toString(),accessToken)?.enqueue(object :
//                                                    Callback<chatting_user_read_model> {
//                                                    override fun onResponse(
//                                                        call: Call<chatting_user_read_model>,
//                                                        response1: Response<chatting_user_read_model>
//                                                    ) {
//
//                                                        binding.chattingTitle.setText(response.body()?.room_name.toString()+" ("+response1.body()?.roomInfo?.get(0)?.userCount.toString()+"/"+response1.body()?.roomInfo?.get(0)?.maxUser.toString()+")")
//
//                                                    }
//                                                    override fun onFailure(call: Call<chatting_user_read_model>, t: Throwable) {
//
//                                                    }
//
//                                                })
//                                                chatting_listarray.clear()
//                                                var free_length = response.body()?.list?.size
//                                                if(free_length !=null) {
//                                                    for (i in 0..free_length - 1) {
//
//                                                        Log.i("receive_id", response.body()?.list?.get(i)?.user_id.toString())
//                                                        chatting_listarray.add(
//                                                            chatting_list(
//                                                                response.body()?.list?.get(i)?.num,
//                                                                response.body()?.list?.get(i)?.room,
//                                                                response.body()?.list?.get(i)?.user_id.toString(),
//                                                                response.body()?.list?.get(i)?.chat.toString(),
//                                                                response.body()?.list?.get(i)?.create_date.toString(),
//                                                                response.body()?.list?.get(i)?.format_date.toString()
//                                                            )
//                                                        )
//
//                                                    }
//                                                    chatting_listarray.reverse()
//                                                    binding.chattingRecycleview.layoutManager = LinearLayoutManager(this@ChattingActivity)
//                                                    binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
//                                                }
//                                            }else{
//                                                Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
//                                                var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
//                                                startActivity(intent)
//                                                stompConnection.dispose()
//                                                finish()
//                                            }
//
//                                        }
//                                        override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {
//
//                                        }
//
//                                    })
                                }
//        topic = stomp.join("/sub/chat/room/$room_num").subscribe {
//            chatting_listarray.clear()
//            chatting_APIS.get_chatting_list_info(room_num.toString(),accessToken,"0")?.enqueue(object :
//                Callback<chatting_room_read_model> {
//                override fun onResponse(
//                    call: Call<chatting_room_read_model>,
//                    response: Response<chatting_room_read_model>
//                ) {
//                    chatting_listarray.clear()
//                    var free_length = response.body()?.list?.size
//                    if(free_length !=null) {
//                        for (i in 0..free_length - 1) {
//
//                            Log.i("receive_id", response.body()?.list?.get(i)?.user_id.toString())
//                            chatting_listarray.add(
//                                chatting_list(
//                                    response.body()?.list?.get(i)?.num,
//                                    response.body()?.list?.get(i)?.room,
//                                    response.body()?.list?.get(i)?.user_id.toString(),
//                                    response.body()?.list?.get(i)?.chat.toString(),
//                                    response.body()?.list?.get(i)?.create_date.toString(),
//                                    response.body()?.list?.get(i)?.format_date.toString()
//                                )
//                            )
//
//                        }
//                        chatting_listarray.reverse()
//                        binding.chattingRecycleview.layoutManager = LinearLayoutManager(this@ChattingActivity)
//                        binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)
//                    }}
//                override fun onFailure(call: Call<chatting_room_read_model>, t: Throwable) {
//
//                }
//
//            })
//        }



//        stomp.lifecycle().subscribe { lifecycleEvent ->
//            when (lifecycleEvent.type) {
//                LifecycleEvent.Type.OPENED -> {
//                    Log.i("OPEND", "!!")
//                }
//                LifecycleEvent.Type.CLOSED -> {
//                    Log.i("CLOSED", "!!")
//                }
//                LifecycleEvent.Type.ERROR -> {
//                    Log.i("ERROR", "!!")
//                    Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
//                }
//                else ->{
//                    Log.i("ELSE", lifecycleEvent.message)
//                }
//            }
//        }
//        stomp.connect()
//        stomp.topic("/sub/chat/room/$room_num").subscribe { topicMessage ->
//            Log.i("message Recieve", topicMessage.payload)
//        }
                                binding.chattingExit.setOnClickListener {
                                    stompConnection.dispose()
                                    finish()
                                }
                                binding.chattingMenu.setOnClickListener { view ->
                                    val popupMenu = PopupMenu(this@ChattingActivity, view)
                                    val inflater = popupMenu.menuInflater
                                    inflater.inflate(R.menu.chating_menu, popupMenu.menu)

                                    popupMenu.setOnMenuItemClickListener { item ->
                                        when (item.itemId) {
                                            R.id.exit -> {
                                                // 메뉴 항목 1이 선택된 경우
                                                exit_chatting_APIS.chatting_clear_info(room_num.toString(), accessToken)?.enqueue(object :
                                                    Callback<exit_chatting_result_model> {
                                                    override fun onResponse(
                                                        call: Call<exit_chatting_result_model>,
                                                        response: Response<exit_chatting_result_model>
                                                    ) {
                                                        Toast.makeText(this@ChattingActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                                                        stompConnection.dispose()
                                                        finish()
                                                    }
                                                    override fun onFailure(call: Call<exit_chatting_result_model>, t: Throwable) {

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
                                binding.sendChat.setOnClickListener {
                                    var text = binding.editChat.text.toString()
                                    if(binding.editChat.text.toString().equals("")) {
                                        Toast.makeText(this@ChattingActivity, "텍스트를 입력해주세요",Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        binding.editChat.setText("")
                                        var send_chat_model = send_chatting_model(
                                            accessToken,
                                            text,
                                            room_num!!.toInt()
                                        )
                                        send_chat_APIS.send_chatting_info(send_chat_model)?.enqueue(object :
                                            Callback<send_result_model> {
                                            override fun onResponse(
                                                call: Call<send_result_model>,
                                                response: Response<send_result_model>
                                            ) {
                                                if(response.body()?.result==200){
                                                    binding.chattingRecycleview.scrollToPosition(chatting_list_adapter.itemCount - 1)

                                                }else{
                                                    Toast.makeText(TedPermissionProvider.context, response.body()?.message.toString(), Toast.LENGTH_SHORT).show()
                                                    var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                                    startActivity(intent)
                                                    stompConnection.dispose()
                                                    finish()
                                                }

//                        Toast.makeText(this@ChattingActivity, room_num.toString(), Toast.LENGTH_SHORT).show()

//                        Toast.makeText(this@ChattingActivity, room_num, Toast.LENGTH_SHORT).show()
                                            }
                                            override fun onFailure(call: Call<send_result_model>, t: Throwable) {

                                            }
                                        })
                                    }
                                }


                            }
                            else {
                                var intent = Intent(context, StartActivity::class.java)
                                Toast.makeText(context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
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



    //    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        var mInflator = menuInflater
//        mInflator.inflate(R.menu.chating_menu, menu)
//        return true
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.exit -> {
//                // 메뉴 항목 1이 선택된 경우
//                exit_chatting_APIS.chatting_clear_info(room_num.toString(), accessToken)?.enqueue(object :
//                    Callback<exit_chatting_result_model> {
//                    override fun onResponse(
//                        call: Call<exit_chatting_result_model>,
//                        response: Response<exit_chatting_result_model>
//                    ) {
//                        Toast.makeText(this@ChattingActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
//                    }
//                    override fun onFailure(call: Call<exit_chatting_result_model>, t: Throwable) {
//
//                    }
//                })
//                // 원하는 동작을 수행
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
    private fun setupView() {
        // 키보드 Open/Close 체크
        binding.constraintLayout12.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.constraintLayout12.getWindowVisibleDisplayFrame(rect)

            val rootViewHeight = binding.constraintLayout12.rootView.height
            val heightDiff = rootViewHeight - rect.height()
            isOpen = heightDiff > rootViewHeight * 0.25 // true == 키보드 올라감
        }
    }
    private val onLayoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            // 키보드가 올라와 높이가 변함
            if (bottom < oldBottom) {
                binding.chattingRecycleview.scrollBy(0, oldBottom - bottom) // 스크롤 유지를 위해 추가
            }
        }
    private fun RecyclerView.isScrollable(): Boolean {
        return canScrollVertically(1) || canScrollVertically(-1)
    }

    /**
     * StackFromEnd 설정
     * */
    private fun RecyclerView.setStackFromEnd() {
        (layoutManager as? LinearLayoutManager)?.stackFromEnd = true
    }

    /**
     * StackFromEnd 확인
     * */
    private fun RecyclerView.getStackFromEnd(): Boolean {
        return (layoutManager as? LinearLayoutManager)?.stackFromEnd ?: false
    }
    override fun onBackPressed() {
    stompConnection.dispose()
        finish()
    }
}