package com.campuslinker.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.campuslinker.app.adapter.alarm_adapter
import com.campuslinker.app.adapter.alarm_list
import com.campuslinker.app.alarm.alarm_list_read_APIS
import com.campuslinker.app.alarm.alarm_list_read_model
import com.campuslinker.app.databinding.FragmentNoticeBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.gun0912.tedpermission.provider.TedPermissionProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoticeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class NoticeFragment : Fragment() {
    var accessToken = token_management.prefs.getString("access_token","기본값")
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!
    val alarm_APIS = alarm_list_read_APIS.create()
    var alarm_list_array = arrayListOf<alarm_list>()
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
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
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
                    binding.swipeRefreshLayout.setOnRefreshListener {
                        // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                        // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                        alarm_APIS.get_alarm_list_info(accessToken)?.enqueue(object : Callback<alarm_list_read_model> {
                            override fun onResponse(
                                call: Call<alarm_list_read_model>,
                                response: Response<alarm_list_read_model>
                            ) {
//                Toast.makeText(context,response.body()?.result.toString(),Toast.LENGTH_SHORT).show()
                                if(response.body()?.result==200){
                                    alarm_list_array.clear()
//                Toast.makeText(context,response.body()?.school_list.toString(),Toast.LENGTH_SHORT).show()
//                    alarm_list_array.clear()
                                    var alarm_list_length = response.body()?.school_list?.size
                                    if(alarm_list_length !=null) {
                                        for (i in 0..alarm_list_length - 1) {
                                            alarm_list_array.add(
                                                alarm_list(
                                                    response.body()?.school_list?.get(i)?.purpose.toString(),
                                                    response.body()?.school_list?.get(i)?.num,
                                                    response.body()?.school_list?.get(i)?.purpose_num,
                                                    response.body()?.school_list?.get(i)?.create_date.toString(),
                                                    response.body()?.school_list?.get(i)?.user.toString(),
                                                    response.body()?.school_list?.get(i)?.content.toString(),
                                                    response.body()?.school_list?.get(i)?.format_date.toString()
                                                )
                                            )

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                        }
                                        var alarm_list_adapter = alarm_adapter(alarm_list_array)
                                        binding.chattingRoomList.apply{
                                            adapter = alarm_list_adapter
                                            layoutManager = LinearLayoutManager(context)
                                            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                        }
                                        alarm_list_adapter.setOnItemclickListner(object : alarm_adapter.OnItemClickListner{

                                            override fun onItemClick(view: View, position: Int) {
                                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                    if(alarm_list_array[position].purpose.equals("free")){
                                                        var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                        intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                        startActivity(intent)
                                                    }
                                                    else if(alarm_list_array[position].purpose.equals("match")){
                                                        var intent = Intent(context, MapsActivity::class.java)
                                                        intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                        startActivity(intent)
                                                    }
                                                    else{
                                                        var intent = Intent(context, ChattingActivity::class.java)
                                                        token_management.prefs.setString("chatting_room_num", alarm_list_array[position].purpose_num.toString())
                                                        startActivity(intent)
                                                    }

                                                }
                                            }

                                        })

                                    }
                                }
                                else{
                                    activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                        var intent = Intent(context, StartActivity::class.java)
                                        startActivity(intent)
                                        activity?.supportFragmentManager?.beginTransaction()?.remove(this@NoticeFragment)?.commit()
                                    }
                                }

                            }
                            override fun onFailure(call: Call<alarm_list_read_model>, t: Throwable) {

                            }

                        })
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    alarm_APIS.get_alarm_list_info(accessToken)?.enqueue(object : Callback<alarm_list_read_model> {
                        override fun onResponse(
                            call: Call<alarm_list_read_model>,
                            response: Response<alarm_list_read_model>
                        ) {
//                Toast.makeText(context,response.body()?.result.toString(),Toast.LENGTH_SHORT).show()
                            if(response.body()?.result==200){
//                Toast.makeText(context,response.body()?.school_list.toString(),Toast.LENGTH_SHORT).show()
//                    alarm_list_array.clear()
                                var alarm_list_length = response.body()?.school_list?.size
                                if(alarm_list_length !=null) {
                                    for (i in 0..alarm_list_length - 1) {
                                        alarm_list_array.add(
                                            alarm_list(
                                                response.body()?.school_list?.get(i)?.purpose.toString(),
                                                response.body()?.school_list?.get(i)?.num,
                                                response.body()?.school_list?.get(i)?.purpose_num,
                                                response.body()?.school_list?.get(i)?.create_date.toString(),
                                                response.body()?.school_list?.get(i)?.user.toString(),
                                                response.body()?.school_list?.get(i)?.content.toString(),
                                                response.body()?.school_list?.get(i)?.format_date.toString()
                                            )
                                        )

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                    }
                                    var alarm_list_adapter = alarm_adapter(alarm_list_array)
                                    binding.chattingRoomList.apply{
                                        adapter = alarm_list_adapter
                                        layoutManager = LinearLayoutManager(context)
                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                    }
                                    alarm_list_adapter.setOnItemclickListner(object : alarm_adapter.OnItemClickListner{

                                        override fun onItemClick(view: View, position: Int) {
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                if(alarm_list_array[position].purpose.equals("free")){
                                                    var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                    intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                    token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                    startActivity(intent)
                                                }
                                                else if(alarm_list_array[position].purpose.equals("match")){
                                                    var intent = Intent(context, MapsActivity::class.java)
                                                    intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                    token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                    startActivity(intent)
                                                }
                                                else{
                                                    var intent = Intent(context, ChattingActivity::class.java)
                                                    token_management.prefs.setString("chatting_room_num", alarm_list_array[position].purpose_num.toString())
                                                    startActivity(intent)
                                                }

                                            }
                                        }

                                    })

                                }
                            }
                            else{
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, StartActivity::class.java)
                                    startActivity(intent)
                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@NoticeFragment)?.commit()
                                }
                            }

                        }
                        override fun onFailure(call: Call<alarm_list_read_model>, t: Throwable) {

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

                                binding.swipeRefreshLayout.setOnRefreshListener {
                                    // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                                    // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                                    alarm_APIS.get_alarm_list_info(accessToken)?.enqueue(object : Callback<alarm_list_read_model> {
                                        override fun onResponse(
                                            call: Call<alarm_list_read_model>,
                                            response: Response<alarm_list_read_model>
                                        ) {
//                Toast.makeText(context,response.body()?.result.toString(),Toast.LENGTH_SHORT).show()
                                            if(response.body()?.result==200){
                                                alarm_list_array.clear()
//                Toast.makeText(context,response.body()?.school_list.toString(),Toast.LENGTH_SHORT).show()
//                    alarm_list_array.clear()
                                                var alarm_list_length = response.body()?.school_list?.size
                                                if(alarm_list_length !=null) {
                                                    for (i in 0..alarm_list_length - 1) {
                                                        alarm_list_array.add(
                                                            alarm_list(
                                                                response.body()?.school_list?.get(i)?.purpose.toString(),
                                                                response.body()?.school_list?.get(i)?.num,
                                                                response.body()?.school_list?.get(i)?.purpose_num,
                                                                response.body()?.school_list?.get(i)?.create_date.toString(),
                                                                response.body()?.school_list?.get(i)?.user.toString(),
                                                                response.body()?.school_list?.get(i)?.content.toString(),
                                                                response.body()?.school_list?.get(i)?.format_date.toString()
                                                            )
                                                        )

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                    }
                                                    var alarm_list_adapter = alarm_adapter(alarm_list_array)
                                                    binding.chattingRoomList.apply{
                                                        adapter = alarm_list_adapter
                                                        layoutManager = LinearLayoutManager(context)
                                                        addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                    }
                                                    alarm_list_adapter.setOnItemclickListner(object : alarm_adapter.OnItemClickListner{

                                                        override fun onItemClick(view: View, position: Int) {
                                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                                if(alarm_list_array[position].purpose.equals("free")){
                                                                    var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                                    intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                                    token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                                    startActivity(intent)
                                                                }
                                                                else if(alarm_list_array[position].purpose.equals("match")){
                                                                    var intent = Intent(context, MapsActivity::class.java)
                                                                    intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                                    token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                                    startActivity(intent)
                                                                }
                                                                else{
                                                                    var intent = Intent(context, ChattingActivity::class.java)
                                                                    token_management.prefs.setString("chatting_room_num", alarm_list_array[position].purpose_num.toString())
                                                                    startActivity(intent)
                                                                }

                                                            }
                                                        }

                                                    })

                                                }
                                            }
                                            else{
                                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                    var intent = Intent(context, StartActivity::class.java)
                                                    startActivity(intent)
                                                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@NoticeFragment)?.commit()
                                                }
                                            }

                                        }
                                        override fun onFailure(call: Call<alarm_list_read_model>, t: Throwable) {

                                        }

                                    })
                                    binding.swipeRefreshLayout.isRefreshing = false
                                }
                                alarm_APIS.get_alarm_list_info(accessToken)?.enqueue(object : Callback<alarm_list_read_model> {
                                    override fun onResponse(
                                        call: Call<alarm_list_read_model>,
                                        response: Response<alarm_list_read_model>
                                    ) {
//                Toast.makeText(context,response.body()?.result.toString(),Toast.LENGTH_SHORT).show()
                                        if(response.body()?.result==200){
//                Toast.makeText(context,response.body()?.school_list.toString(),Toast.LENGTH_SHORT).show()
//                    alarm_list_array.clear()
                                            var alarm_list_length = response.body()?.school_list?.size
                                            if(alarm_list_length !=null) {
                                                for (i in 0..alarm_list_length - 1) {
                                                    alarm_list_array.add(
                                                        alarm_list(
                                                            response.body()?.school_list?.get(i)?.purpose.toString(),
                                                            response.body()?.school_list?.get(i)?.num,
                                                            response.body()?.school_list?.get(i)?.purpose_num,
                                                            response.body()?.school_list?.get(i)?.create_date.toString(),
                                                            response.body()?.school_list?.get(i)?.user.toString(),
                                                            response.body()?.school_list?.get(i)?.content.toString(),
                                                            response.body()?.school_list?.get(i)?.format_date.toString()
                                                        )
                                                    )

//                        GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body()?.list?.get(i)?.category+response.body()?.list?.get(i)?.title
//                                +response.body()?.list?.get(i)?.create_date+response.body()?.list?.get(i)?.reaction.toString()+response.body()?.list?.get(i)?.comment.toString())
//                        dialog.setTitle("결과")
//                        dialog.setMessage(response.body()?.list?.get(i)?.create_date)
//                        dialog.show()
                                                }
                                                var alarm_list_adapter = alarm_adapter(alarm_list_array)
                                                binding.chattingRoomList.apply{
                                                    adapter = alarm_list_adapter
                                                    layoutManager = LinearLayoutManager(context)
                                                    addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                                                }
                                                alarm_list_adapter.setOnItemclickListner(object : alarm_adapter.OnItemClickListner{

                                                    override fun onItemClick(view: View, position: Int) {
                                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                            if(alarm_list_array[position].purpose.equals("free")){
                                                                var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                                intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                                token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                                startActivity(intent)
                                                            }
                                                            else if(alarm_list_array[position].purpose.equals("match")){
                                                                var intent = Intent(context, MapsActivity::class.java)
                                                                intent.putExtra("boadr_num", alarm_list_array[position].purpose_num.toString()) // 데이터 넣기
                                                                token_management.prefs.setString("board_num", alarm_list_array[position].purpose_num.toString())
                                                                startActivity(intent)
                                                            }
                                                            else{
                                                                var intent = Intent(context, ChattingActivity::class.java)
                                                                token_management.prefs.setString("chatting_room_num", alarm_list_array[position].purpose_num.toString())
                                                                startActivity(intent)
                                                            }

                                                        }
                                                    }

                                                })

                                            }
                                        }
                                        else{
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                                activity?.supportFragmentManager?.beginTransaction()?.remove(this@NoticeFragment)?.commit()
                                            }
                                        }

                                    }
                                    override fun onFailure(call: Call<alarm_list_read_model>, t: Throwable) {

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




        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NoticeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoticeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}