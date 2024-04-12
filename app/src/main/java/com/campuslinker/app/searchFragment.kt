package com.campuslinker.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.campuslinker.app.adapter.chatting_room_list
import com.campuslinker.app.adapter.chatting_room_list_adapter
import com.campuslinker.app.chatting.chatting_room_list_APIS
import com.campuslinker.app.chatting.chatting_room_list_read_model
import com.campuslinker.app.databinding.FragmentSearchBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gmail.bishoybasily.stomp.lib.Event
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.gun0912.tedpermission.provider.TedPermissionProvider
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [searchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchFragment : Fragment() {
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var chat_room_list_APIS = chatting_room_list_APIS.create()
    val api_token = Get_token_APIS.create()
    val url2 = "wss://sirobako.co.kr/campus-linker/socket/websocket"
    val intervalMillis = 5000L
    val client = OkHttpClient()
    val stomp = StompClient(client, intervalMillis).apply{this@apply.url=url2}
    var id = token_management.prefs.getString("id","기본값")
    lateinit var stompConnection: Disposable
    lateinit var topic: Disposable
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    var chatting_list_array = arrayListOf<chatting_room_list>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

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

                            else -> {}
                        }
                    }
                    topic = stomp.join("/sub/chat/room/$id").subscribe {
                        chat_room_list_APIS.get_chatting_room_list_info(accessToken)?.enqueue(object :
                            Callback<chatting_room_list_read_model> {
                            override fun onResponse(
                                call: Call<chatting_room_list_read_model>,
                                response: Response<chatting_room_list_read_model>
                            ) {
                                chatting_list_array.clear()
                                var chatting_room_list_length = response.body()?.list?.size
                                if(chatting_room_list_length !=null) {
                                    for (i in 0..chatting_room_list_length - 1) {
                                        chatting_list_array.add(
                                            chatting_room_list(
                                                response.body()?.list?.get(i)?.room_num,
                                                response.body()?.list?.get(i)?.room_name.toString(),
                                                response.body()?.list?.get(i)?.last_chat.toString(),
                                                response.body()?.list?.get(i)?.not_read,
                                                response.body()?.list?.get(i)?.format_date.toString()
                                            )
                                        )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                    }

                                    var chatting_room_list_adapter = chatting_room_list_adapter(chatting_list_array)
                                    binding.chattingRoomList.apply{
                                        adapter = chatting_room_list_adapter
                                        layoutManager = LinearLayoutManager(context)
                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                    }
                                    chatting_room_list_adapter.setOnItemclickListner(object : chatting_room_list_adapter.OnItemClickListner{

                                        override fun onItemClick(view: View, position: Int) {
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, ChattingActivity::class.java)
                                                token_management.prefs.setString("chatting_room_num", chatting_list_array[position].room_num.toString())
                                                startActivity(intent)
//                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@searchFragment)?.commit()
                                            }
                                        }
                                    })
                                }

                            }
                            override fun onFailure(call: Call<chatting_room_list_read_model>, t: Throwable) {

                            }

                        })
                    }
                    api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                        override fun onResponse(
                            call: Call<Token_data>,
                            response: Response<Token_data>
                        ) {
                            if(response.body()?.result==200){
                            }
                            else{
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, StartActivity::class.java)
                                    startActivity(intent)
                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@searchFragment)?.commit()
                                }
                            }

                        }
                        override fun onFailure(call: Call<Token_data>, t: Throwable) {

                        }

                    })
                    chat_room_list_APIS.get_chatting_room_list_info(accessToken)?.enqueue(object :
                        Callback<chatting_room_list_read_model> {
                        override fun onResponse(
                            call: Call<chatting_room_list_read_model>,
                            response: Response<chatting_room_list_read_model>
                        ) {

                            var chatting_room_list_length = response.body()?.list?.size
                            if(chatting_room_list_length !=null) {
                                for (i in 0..chatting_room_list_length - 1) {
                                    chatting_list_array.add(
                                        chatting_room_list(
                                            response.body()?.list?.get(i)?.room_num,
                                            response.body()?.list?.get(i)?.room_name.toString(),
                                            response.body()?.list?.get(i)?.last_chat.toString(),
                                            response.body()?.list?.get(i)?.not_read,
                                            response.body()?.list?.get(i)?.format_date.toString()
                                        )
                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                }

                                var chatting_room_list_adapter = chatting_room_list_adapter(chatting_list_array)
                                binding.chattingRoomList.apply{
                                    adapter = chatting_room_list_adapter
                                    layoutManager = LinearLayoutManager(context)
                                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                }
                                chatting_room_list_adapter.setOnItemclickListner(object : chatting_room_list_adapter.OnItemClickListner{

                                    override fun onItemClick(view: View, position: Int) {
                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                            var intent = Intent(context, ChattingActivity::class.java)
                                            intent.putExtra("chatting_room_name", chatting_list_array[position].room_name.toString()) // 데이터 넣기
                                            token_management.prefs.setString("chatting_room_num", chatting_list_array[position].room_num.toString())
                                            startActivity(intent)
                                        }
                                    }

                                })

                            }

                        }
                        override fun onFailure(call: Call<chatting_room_list_read_model>, t: Throwable) {

                        }

                    })
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

                                        else -> {}
                                    }
                                }
                                topic = stomp.join("/sub/chat/room/$id").subscribe {
                                    chat_room_list_APIS.get_chatting_room_list_info(accessToken)?.enqueue(object :
                                        Callback<chatting_room_list_read_model> {
                                        override fun onResponse(
                                            call: Call<chatting_room_list_read_model>,
                                            response: Response<chatting_room_list_read_model>
                                        ) {
                                            chatting_list_array.clear()
                                            var chatting_room_list_length = response.body()?.list?.size
                                            if(chatting_room_list_length !=null) {
                                                for (i in 0..chatting_room_list_length - 1) {
                                                    chatting_list_array.add(
                                                        chatting_room_list(
                                                            response.body()?.list?.get(i)?.room_num,
                                                            response.body()?.list?.get(i)?.room_name.toString(),
                                                            response.body()?.list?.get(i)?.last_chat.toString(),
                                                            response.body()?.list?.get(i)?.not_read,
                                                            response.body()?.list?.get(i)?.format_date.toString()
                                                        )
                                                    )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }

                                                var chatting_room_list_adapter = chatting_room_list_adapter(chatting_list_array)
                                                binding.chattingRoomList.apply{
                                                    adapter = chatting_room_list_adapter
                                                    layoutManager = LinearLayoutManager(context)
                                                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                }
                                                chatting_room_list_adapter.setOnItemclickListner(object : chatting_room_list_adapter.OnItemClickListner{

                                                    override fun onItemClick(view: View, position: Int) {
                                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                            var intent = Intent(context, ChattingActivity::class.java)
                                                            token_management.prefs.setString("chatting_room_num", chatting_list_array[position].room_num.toString())
                                                            startActivity(intent)
//                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@searchFragment)?.commit()
                                                        }
                                                    }
                                                })
                                            }

                                        }
                                        override fun onFailure(call: Call<chatting_room_list_read_model>, t: Throwable) {

                                        }

                                    })
                                }
                                api_token.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                    override fun onResponse(
                                        call: Call<Token_data>,
                                        response: Response<Token_data>
                                    ) {
                                        if(response.body()?.result==200){
                                        }
                                        else{
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                                activity?.supportFragmentManager?.beginTransaction()?.remove(this@searchFragment)?.commit()
                                            }
                                        }

                                    }
                                    override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                    }

                                })
                                chat_room_list_APIS.get_chatting_room_list_info(accessToken)?.enqueue(object :
                                    Callback<chatting_room_list_read_model> {
                                    override fun onResponse(
                                        call: Call<chatting_room_list_read_model>,
                                        response: Response<chatting_room_list_read_model>
                                    ) {

                                        var chatting_room_list_length = response.body()?.list?.size
                                        if(chatting_room_list_length !=null) {
                                            for (i in 0..chatting_room_list_length - 1) {
                                                chatting_list_array.add(
                                                    chatting_room_list(
                                                        response.body()?.list?.get(i)?.room_num,
                                                        response.body()?.list?.get(i)?.room_name.toString(),
                                                        response.body()?.list?.get(i)?.last_chat.toString(),
                                                        response.body()?.list?.get(i)?.not_read,
                                                        response.body()?.list?.get(i)?.format_date.toString()
                                                    )
                                                )
//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                            }

                                            var chatting_room_list_adapter = chatting_room_list_adapter(chatting_list_array)
                                            binding.chattingRoomList.apply{
                                                adapter = chatting_room_list_adapter
                                                layoutManager = LinearLayoutManager(context)
                                                addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                            }
                                            chatting_room_list_adapter.setOnItemclickListner(object : chatting_room_list_adapter.OnItemClickListner{

                                                override fun onItemClick(view: View, position: Int) {
                                                    activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                        var intent = Intent(context, ChattingActivity::class.java)
                                                        intent.putExtra("chatting_room_name", chatting_list_array[position].room_name.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("chatting_room_num", chatting_list_array[position].room_num.toString())
                                                        startActivity(intent)
                                                    }
                                                }

                                            })

                                        }

                                    }
                                    override fun onFailure(call: Call<chatting_room_list_read_model>, t: Throwable) {

                                    }

                                })

                            }
                            else {
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(TedPermissionProvider.context, StartActivity::class.java)
                                    Toast.makeText(TedPermissionProvider.context, refresh_response.body()?.massage.toString(),Toast.LENGTH_SHORT).show()
                                    startActivity(intent)
                                }
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


        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment searchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}