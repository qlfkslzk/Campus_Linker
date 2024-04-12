package com.campuslinker.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.campuslinker.app.FCM.FCM_Token_model
import com.campuslinker.app.FCM.FCM_Token_result_model
import com.campuslinker.app.FCM.Post_FCM_Token_APIS
import com.campuslinker.app.adapter.MatchListAdapter
import com.campuslinker.app.check_board_list.Board_List_Model
import com.campuslinker.app.check_board_list.Match_Board_List_Model
import com.campuslinker.app.check_board_list.check_board_APIS
import com.campuslinker.app.check_board_list.check_board_APIS_match
import com.campuslinker.app.databinding.FragmentHomeBinding
import com.campuslinker.app.token.Get_token_APIS
import com.campuslinker.app.token.Re_Put_Token_APIS
import com.campuslinker.app.token.Token_data
import com.campuslinker.app.token.re_token_data
import com.campuslinker.app.token.re_token_model
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.provider.TedPermissionProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

val api_token1 = Get_token_APIS.create()
/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter : ListAdapter
    lateinit var match_adapter : MatchListAdapter
    lateinit var board : ArrayList<Board_class>
    val POST_FCM_Token = Post_FCM_Token_APIS.create()
    var accessToken = token_management.prefs.getString("access_token","기본값")
    var refreshToken = token_management.prefs.getString("refresh_token","기본값")
    val free_board_list = check_board_APIS.create()
    val match_board_list = check_board_APIS_match.create()
    val Get_user_info_APIS = Get_token_APIS.create()
    var token : String ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }

    }
    private var backPressedTime: Long = 0
    fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTime >= 2000) {
            backPressedTime = System.currentTimeMillis()
        } else {
            ActivityCompat.finishAffinity(NaviActivity())
            System.exit(0)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        val naviActivity = NaviActivity.naviActivity
        var free_board_list_array =arrayListOf<Board_class>()
        var match_board_list_array =arrayListOf<Board_class>()
        var school_name = view.findViewById<TextView>(R.id.school_name)


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        var refreshToken = token_management.prefs.getString("refresh_token","기본값")
        Log.d("refreshToken", refreshToken)

        val refresh_token_data = re_token_model(
            refreshToken
        )
        Get_token_APIS.create().gettokenlInfo(accessToken)?.enqueue(object :
            Callback<Token_data> {
            override fun onResponse(
                call: Call<Token_data>,
                response: Response<Token_data>
            ) {
                if(response.body()?.result==200 || response.body()?.result==406){
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("testt", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        token = task.result
                        var token_model = FCM_Token_model(
                            accessToken,
                            token.toString()
                        )
                        // Log and toast
                        POST_FCM_Token.Post_FCM__info(token_model)?.enqueue(object : Callback<FCM_Token_result_model> {
                            override fun onResponse(
                                call: Call<FCM_Token_result_model>,
                                response: Response<FCM_Token_result_model>
                            ) {

                            }
                            override fun onFailure(call: Call<FCM_Token_result_model>, t: Throwable) {

                            }

                        })
                        Log.d("testt", token.toString())
                        Log.d("acctoken", accessToken)
                        Log.d("FCMToken", token.toString())
                    })



                    Get_user_info_APIS.gettokenlInfo(accessToken)?.enqueue(object :  Callback<Token_data> {
                        override fun onResponse(call: Call<Token_data>, response: Response<Token_data>) {
                            token_management.prefs.setString("name", response.body()?.user?.get(0)?.name.toString())
                            token_management.prefs.setString("id", response.body()?.user?.get(0)?.id.toString())
                            var name = token_management.prefs.getString("name","기본값")
                            if (school_name != null) {
                                school_name.setText(response.body()?.user?.get(0)?.univ.toString())
                            }
                        }
                        override fun onFailure(call: Call<Token_data>, t: Throwable) {
                            TODO("Not yet implemented")
                        }
                    })
                    binding.swipeRefreshLayout.setOnRefreshListener {
                        // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                        // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                        api_token1.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                            override fun onResponse(
                                call: Call<Token_data>,
                                response: Response<Token_data>
                            ) {
                                if(response.body()?.result==200 || response.body()?.result==406){
                                    free_board_list.getboardlistlInfo("free","0","","","$accessToken")?.enqueue(object :
                                        Callback<Board_List_Model> {
                                        override fun onResponse(
                                            call: Call<Board_List_Model>,
                                            response: Response<Board_List_Model>
                                        ) {
                                            if(response.body()?.result==200 || response.body()?.result==406){
                                                free_board_list_array.clear()
                                                var free_length = response.body()?.list?.size
                                                if (free_length != null) {
                                                    if(free_length<4){
                                                        for(i in 0..free_length-1){
                                                            free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_date,
                                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                response.body()?.list?.get(i)?.comment.toString(),
                                                                response.body()?.list?.get(i)?.num?.toInt()))
                                                        }
                                                    }
                                                    else{
                                                        for(i in 0..3){
                                                            free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                response.body()?.list?.get(i)?.title,
                                                                response.body()?.list?.get(i)?.format_date,
                                                                response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                response.body()?.list?.get(i)?.comment.toString(),
                                                                response.body()?.list?.get(i)?.num?.toInt()))
                                                        }
                                                    }
                                                }

//                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
                                                var adapter = ListAdapter(requireContext(), free_board_list_array) // 어댑터를 초기화하세요
                                                var free_board_list : ListView = view.findViewById(R.id.list_freeboard)
                                                free_board_list.adapter = adapter
                                                free_board_list.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                    val selectedItem = parent.getItemAtPosition(position).toString()
                                                    var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                    intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                    token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                    startActivity(intent)
                                                    // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                    // 예: 아이템 텍스트를 로그에 출력
                                                    Log.d("ListView", "Selected item: $selectedItem")
                                                }
                                                var totalHeight = 0
                                                for (i in 0 until adapter.count) {
                                                    val listItem = adapter.getView(i, null, free_board_list)
                                                    listItem.measure(
                                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                    )
                                                    totalHeight += listItem.measuredHeight
                                                }

// 리스트뷰의 높이를 동적으로 설정
                                                val params = free_board_list.layoutParams
                                                params.height = totalHeight + (free_board_list.dividerHeight * (adapter.count - 1))
                                                free_board_list.layoutParams = params
                                                free_board_list.requestLayout()

                                            }

                                            else{
                                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                    var intent = Intent(context, StartActivity::class.java)
                                                    startActivity(intent)
                                                }
                                            }
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                        }
                                        override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                        }

                                    })

                                    match_board_list.getboardlist_matchlInfo("match","0","","","$accessToken")?.enqueue(object :
                                        Callback<Match_Board_List_Model> {
                                        override fun onResponse(
                                            call: Call<Match_Board_List_Model>,
                                            response: Response<Match_Board_List_Model>
                                        ) {
                                            match_board_list_array.clear()
                                            var match_length = response.body()?.list?.size
                                            if (match_length != null) {
                                                if(match_length<4){
                                                    for(i in 0..match_length-1){
                                                        match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_create_date,
                                                            response.body()?.list?.get(i)?.gender.toString(),
                                                            response.body()?.list?.get(i)?.users.toString(),
                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                    }
                                                }
                                                else{
                                                    for(i in 0..3){
                                                        match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_create_date,
                                                            response.body()?.list?.get(i)?.gender.toString(),
                                                            response.body()?.list?.get(i)?.users.toString(),
                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                    }
                                                }
                                            }
                                            val match_adapter = MatchListAdapter(requireContext(), match_board_list_array)
                                            var match_board_list1 : ListView = view.findViewById(R.id.list_matchboard)
                                            match_board_list1.adapter = match_adapter
                                            match_board_list1.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                val selectedItem = parent.getItemAtPosition(position).toString()
                                                var intent = Intent(context, MapsActivity::class.java)
                                                intent.putExtra("board_num", match_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",match_board_list_array[position].num.toString())
                                                startActivity(intent)
                                                // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                // 예: 아이템 텍스트를 로그에 출력
                                                Log.d("board_num", match_board_list_array[position].num.toString())
                                            }
                                            var totalHeight = 0
                                            for (i in 0 until match_adapter.count) {
                                                val listItem = match_adapter.getView(i, null, match_board_list1)
                                                listItem.measure(
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                )
                                                totalHeight += listItem.measuredHeight
                                            }

// 리스트뷰의 높이를 동적으로 설정
                                            val params = match_board_list1.layoutParams
                                            params.height = totalHeight + (match_board_list1.dividerHeight * (match_adapter.count - 1))
                                            match_board_list1.layoutParams = params
                                            match_board_list1.requestLayout()

//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                        }
                                        override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                        }

                                    })
                                }
                                else{
                                    activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                        var intent = Intent(context, StartActivity::class.java)
                                        startActivity(intent)
                                        naviActivity!!.finish()
                                    }
                                }

                            }
                            override fun onFailure(call: Call<Token_data>, t: Throwable) {

                            }

                        })


                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    api_token1.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                        override fun onResponse(
                            call: Call<Token_data>,
                            response: Response<Token_data>
                        ) {
                            if(response.body()?.result==200 || response.body()?.result==406){
                                free_board_list.getboardlistlInfo("free","0","","","$accessToken")?.enqueue(object :
                                    Callback<Board_List_Model> {
                                    override fun onResponse(
                                        call: Call<Board_List_Model>,
                                        response: Response<Board_List_Model>
                                    ) {
                                        if(response.body()?.result==200 || response.body()?.result==406){
                                            var free_length = response.body()?.list?.size
                                            if (free_length != null) {
                                                if(free_length<4){
                                                    for(i in 0..free_length-1){
                                                        free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_date,
                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                    }
                                                }
                                                else{
                                                    for(i in 0..3){
                                                        free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                            response.body()?.list?.get(i)?.title,
                                                            response.body()?.list?.get(i)?.format_date,
                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                    }
                                                }
                                            }

//                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
                                            var adapter = ListAdapter(requireContext(), free_board_list_array) // 어댑터를 초기화하세요
                                            var free_board_list : ListView = view.findViewById(R.id.list_freeboard)
                                            free_board_list.adapter = adapter
                                            free_board_list.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                val selectedItem = parent.getItemAtPosition(position).toString()
                                                var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                intent.putExtra("board_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                startActivity(intent)
                                                // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                // 예: 아이템 텍스트를 로그에 출력
                                                Log.d("board_num", free_board_list_array[position].num.toString())
                                            }
                                            var totalHeight = 0
                                            for (i in 0 until adapter.count) {
                                                val listItem = adapter.getView(i, null, free_board_list)
                                                listItem.measure(
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                )
                                                totalHeight += listItem.measuredHeight
                                            }

// 리스트뷰의 높이를 동적으로 설정
                                            val params = free_board_list.layoutParams
                                            params.height = totalHeight + (free_board_list.dividerHeight * (adapter.count - 1))
                                            free_board_list.layoutParams = params
                                            free_board_list.requestLayout()
                                        }

                                        else{
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                    }
                                    override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                    }

                                })

                                match_board_list.getboardlist_matchlInfo("match","0","","","$accessToken")?.enqueue(object :
                                    Callback<Match_Board_List_Model> {
                                    override fun onResponse(
                                        call: Call<Match_Board_List_Model>,
                                        response: Response<Match_Board_List_Model>
                                    ) {

                                        var match_length = response.body()?.list?.size
                                        if (match_length != null) {
                                            if(match_length<4){
                                                for(i in 0..match_length-1){
                                                    match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                        response.body()?.list?.get(i)?.title,
                                                        response.body()?.list?.get(i)?.format_create_date,
                                                        response.body()?.list?.get(i)?.gender.toString(),
                                                        response.body()?.list?.get(i)?.users.toString(),
                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                }
                                            }
                                            else{
                                                for(i in 0..3){
                                                    match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                        response.body()?.list?.get(i)?.title,
                                                        response.body()?.list?.get(i)?.format_create_date,
                                                        response.body()?.list?.get(i)?.gender.toString(),
                                                        response.body()?.list?.get(i)?.users.toString(),
                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                }
                                            }
                                        }
                                        val match_adapter = MatchListAdapter(requireContext(), match_board_list_array)
                                        var match_board_list1 : ListView = view.findViewById(R.id.list_matchboard)
                                        match_board_list1.adapter = match_adapter
                                        match_board_list1.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                            val selectedItem = parent.getItemAtPosition(position).toString()
                                            var intent = Intent(context, MapsActivity::class.java)
                                            intent.putExtra("board_num", match_board_list_array[position].num.toString()) // 데이터 넣기
                                            token_management.prefs.setString("board_num",match_board_list_array[position].num.toString())
                                            startActivity(intent)
                                            // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                            // 예: 아이템 텍스트를 로그에 출력
                                            Log.d("board_num", match_board_list_array[position].num.toString())
                                        }
                                        var totalHeight = 0
                                        for (i in 0 until match_adapter.count) {
                                            val listItem = match_adapter.getView(i, null, match_board_list1)
                                            listItem.measure(
                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                            )
                                            totalHeight += listItem.measuredHeight
                                        }

// 리스트뷰의 높이를 동적으로 설정
                                        val params = match_board_list1.layoutParams
                                        params.height = totalHeight + (match_board_list1.dividerHeight * (match_adapter.count - 1))
                                        match_board_list1.layoutParams = params
                                        match_board_list1.requestLayout()

//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                    }
                                    override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                    }

                                })
                            }
                            else{
                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                    var intent = Intent(context, StartActivity::class.java)
                                    startActivity(intent)
                                    naviActivity!!.finish()
                                }
                            }

                        }
                        override fun onFailure(call: Call<Token_data>, t: Throwable) {

                        }

                    })

                    var free_board : TextView = view.findViewById(R.id.textView15) //자유게시판 이동
                    free_board.setOnClickListener {
                        activity?.let{
                            var intent = Intent(context, BoardActivity::class.java)
                            intent.putExtra("boadr_type", "free") // 데이터 넣기
                            token_management.prefs.setString("boadr_type", "free")
                            startActivity(intent)
//                activity?.supportFragmentManager?.beginTransaction()?.remove(this@HomeFragment)?.commit()//프래그먼트 종료 함수
                        }
                    }
                    var match_board : TextView = view.findViewById(R.id.textView16) //매치게시판 이동
                    match_board.setOnClickListener {
                        activity?.let{
                            var intent = Intent(context, BoardActivity::class.java)
                            intent.putExtra("boadr_type", "match") // 데이터 넣기
                            token_management.prefs.setString("boadr_type", "match")
                            startActivity(intent)
//                activity?.supportFragmentManager?.beginTransaction()?.remove(this@HomeFragment)?.commit()//프래그먼트 종료 함수
                        }
                    }
//        var ex : TextView = view.findViewById(R.id.textView20)
//        ex.setText(free_board_list_array.toString())
                    var board_list_array =arrayListOf<Board_class>(
                        Board_class("주제","제목","날짜","좋아요","댓글"),
                        Board_class("theme","title","date","favorite","comment"),
                        Board_class("theme","title","date","favorite","comment")
                    )
                }
                else {
                    Re_Put_Token_APIS.create().post_re_token(refresh_token_data)?.enqueue(object :
                        Callback<re_token_data> {
                        override fun onResponse(
                            call: Call<re_token_data>,
                            refresh_response: Response<re_token_data>
                        ) {
                            if(refresh_response.body()?.result==200 || response.body()?.result==406){
                                token_management.prefs.setString("access_token", refresh_response.body()?.accessToken.toString())
                                token_management.prefs.setString("refresh_token", refresh_response.body()?.refreshToken.toString())
                                accessToken = token_management.prefs.getString("access_token","기본값")
                                refreshToken = token_management.prefs.getString("refresh_token","기본값")


                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w("testt", "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    token = task.result
                                    var token_model = FCM_Token_model(
                                        accessToken,
                                        token.toString()
                                    )
                                    // Log and toast
                                    POST_FCM_Token.Post_FCM__info(token_model)?.enqueue(object : Callback<FCM_Token_result_model> {
                                        override fun onResponse(
                                            call: Call<FCM_Token_result_model>,
                                            response: Response<FCM_Token_result_model>
                                        ) {

                                        }
                                        override fun onFailure(call: Call<FCM_Token_result_model>, t: Throwable) {

                                        }

                                    })
                                    Log.d("testt", token.toString())
                                    Log.d("acctoken", accessToken)
                                    Log.d("FCMToken", token.toString())
                                })



                                Get_user_info_APIS.gettokenlInfo(accessToken)?.enqueue(object :  Callback<Token_data> {
                                    override fun onResponse(call: Call<Token_data>, response: Response<Token_data>) {
                                        token_management.prefs.setString("name", response.body()?.user?.get(0)?.name.toString())
                                        token_management.prefs.setString("id", response.body()?.user?.get(0)?.id.toString())
                                        var name = token_management.prefs.getString("name","기본값")
                                        if (school_name != null) {
                                            school_name.setText(response.body()?.user?.get(0)?.univ.toString())
                                        }
                                    }
                                    override fun onFailure(call: Call<Token_data>, t: Throwable) {
                                        TODO("Not yet implemented")
                                    }
                                })
                                binding.swipeRefreshLayout.setOnRefreshListener {
                                    // 사용자가 당겨서 새로고침을 요청하면 이 코드 블록이 실행됩니다.
                                    // 여기에서 원하는 작업(데이터 다시 로드 등)을 수행한 후 아래 코드로 새로고침 완료를 알립니다.
                                    api_token1.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                        override fun onResponse(
                                            call: Call<Token_data>,
                                            response: Response<Token_data>
                                        ) {
                                            if(response.body()?.result==200 || response.body()?.result==406){
                                                free_board_list.getboardlistlInfo("free","0","","","$accessToken")?.enqueue(object :
                                                    Callback<Board_List_Model> {
                                                    override fun onResponse(
                                                        call: Call<Board_List_Model>,
                                                        response: Response<Board_List_Model>
                                                    ) {
                                                        if(response.body()?.result==200 || response.body()?.result==406){
                                                            free_board_list_array.clear()
                                                            var free_length = response.body()?.list?.size
                                                            if (free_length != null) {
                                                                if(free_length<4){
                                                                    for(i in 0..free_length-1){
                                                                        free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_date,
                                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                                    }
                                                                }
                                                                else{
                                                                    for(i in 0..3){
                                                                        free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                            response.body()?.list?.get(i)?.title,
                                                                            response.body()?.list?.get(i)?.format_date,
                                                                            response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                            response.body()?.list?.get(i)?.comment.toString(),
                                                                            response.body()?.list?.get(i)?.num?.toInt()))
                                                                    }
                                                                }
                                                            }

//                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
                                                            var adapter = ListAdapter(requireContext(), free_board_list_array) // 어댑터를 초기화하세요
                                                            var free_board_list : ListView = view.findViewById(R.id.list_freeboard)
                                                            free_board_list.adapter = adapter
                                                            free_board_list.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                                val selectedItem = parent.getItemAtPosition(position).toString()
                                                                var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                                intent.putExtra("boadr_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                                token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                                startActivity(intent)
                                                                // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                                // 예: 아이템 텍스트를 로그에 출력
                                                                Log.d("ListView", "Selected item: $selectedItem")
                                                            }
                                                            var totalHeight = 0
                                                            for (i in 0 until adapter.count) {
                                                                val listItem = adapter.getView(i, null, free_board_list)
                                                                listItem.measure(
                                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                                )
                                                                totalHeight += listItem.measuredHeight
                                                            }

// 리스트뷰의 높이를 동적으로 설정
                                                            val params = free_board_list.layoutParams
                                                            params.height = totalHeight + (free_board_list.dividerHeight * (adapter.count - 1))
                                                            free_board_list.layoutParams = params
                                                            free_board_list.requestLayout()

                                                        }

                                                        else{
                                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                                var intent = Intent(context, StartActivity::class.java)
                                                                startActivity(intent)
                                                            }
                                                        }
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                    }

                                                })

                                                match_board_list.getboardlist_matchlInfo("match","0","","","$accessToken")?.enqueue(object :
                                                    Callback<Match_Board_List_Model> {
                                                    override fun onResponse(
                                                        call: Call<Match_Board_List_Model>,
                                                        response: Response<Match_Board_List_Model>
                                                    ) {
                                                        match_board_list_array.clear()
                                                        var match_length = response.body()?.list?.size
                                                        if (match_length != null) {
                                                            if(match_length<4){
                                                                for(i in 0..match_length-1){
                                                                    match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_create_date,
                                                                        response.body()?.list?.get(i)?.gender.toString(),
                                                                        response.body()?.list?.get(i)?.users.toString(),
                                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                                }
                                                            }
                                                            else{
                                                                for(i in 0..3){
                                                                    match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_create_date,
                                                                        response.body()?.list?.get(i)?.gender.toString(),
                                                                        response.body()?.list?.get(i)?.users.toString(),
                                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                                }
                                                            }
                                                        }
                                                        val match_adapter = MatchListAdapter(requireContext(), match_board_list_array)
                                                        var match_board_list1 : ListView = view.findViewById(R.id.list_matchboard)
                                                        match_board_list1.adapter = match_adapter
                                                        match_board_list1.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                            val selectedItem = parent.getItemAtPosition(position).toString()
                                                            var intent = Intent(context, MapsActivity::class.java)
                                                            intent.putExtra("board_num", match_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",match_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                            // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                            // 예: 아이템 텍스트를 로그에 출력
                                                            Log.d("board_num", match_board_list_array[position].num.toString())
                                                        }
                                                        var totalHeight = 0
                                                        for (i in 0 until match_adapter.count) {
                                                            val listItem = match_adapter.getView(i, null, match_board_list1)
                                                            listItem.measure(
                                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                            )
                                                            totalHeight += listItem.measuredHeight
                                                        }

// 리스트뷰의 높이를 동적으로 설정
                                                        val params = match_board_list1.layoutParams
                                                        params.height = totalHeight + (match_board_list1.dividerHeight * (match_adapter.count - 1))
                                                        match_board_list1.layoutParams = params
                                                        match_board_list1.requestLayout()

//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                    }
                                                    override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                    }

                                                })
                                            }
                                            else{
                                                activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                    var intent = Intent(context, StartActivity::class.java)
                                                    startActivity(intent)
                                                    naviActivity!!.finish()
                                                }
                                            }

                                        }
                                        override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                        }

                                    })


                                    binding.swipeRefreshLayout.isRefreshing = false
                                }
                                api_token1.gettokenlInfo(accessToken)?.enqueue(object : Callback<Token_data> {
                                    override fun onResponse(
                                        call: Call<Token_data>,
                                        response: Response<Token_data>
                                    ) {
                                        if(response.body()?.result==200 || response.body()?.result==406){
                                            free_board_list.getboardlistlInfo("free","0","","","$accessToken")?.enqueue(object :
                                                Callback<Board_List_Model> {
                                                override fun onResponse(
                                                    call: Call<Board_List_Model>,
                                                    response: Response<Board_List_Model>
                                                ) {
                                                    if(response.body()?.result==200 || response.body()?.result==406){
                                                        var free_length = response.body()?.list?.size
                                                        if (free_length != null) {
                                                            if(free_length<4){
                                                                for(i in 0..free_length-1){
                                                                    free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_date,
                                                                        response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                        response.body()?.list?.get(i)?.comment.toString(),
                                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                                }
                                                            }
                                                            else{
                                                                for(i in 0..3){
                                                                    free_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                        response.body()?.list?.get(i)?.title,
                                                                        response.body()?.list?.get(i)?.format_date,
                                                                        response.body()?.list?.get(i)?.reaction_count.toString(),
                                                                        response.body()?.list?.get(i)?.comment.toString(),
                                                                        response.body()?.list?.get(i)?.num?.toInt()))
                                                                }
                                                            }
                                                        }

//                GMailSender().sendEmail("qlfkslzk@hs.ac.kr", response.body().toString()) //리스폰스 값 메일로 확인
                                                        var adapter = ListAdapter(requireContext(), free_board_list_array) // 어댑터를 초기화하세요
                                                        var free_board_list : ListView = view.findViewById(R.id.list_freeboard)
                                                        free_board_list.adapter = adapter
                                                        free_board_list.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                            val selectedItem = parent.getItemAtPosition(position).toString()
                                                            var intent = Intent(context, ReadFreeBoardActivity::class.java)
                                                            intent.putExtra("board_num", free_board_list_array[position].num.toString()) // 데이터 넣기
                                                            token_management.prefs.setString("board_num",free_board_list_array[position].num.toString())
                                                            startActivity(intent)
                                                            // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                            // 예: 아이템 텍스트를 로그에 출력
                                                            Log.d("board_num", free_board_list_array[position].num.toString())
                                                        }
                                                        var totalHeight = 0
                                                        for (i in 0 until adapter.count) {
                                                            val listItem = adapter.getView(i, null, free_board_list)
                                                            listItem.measure(
                                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                            )
                                                            totalHeight += listItem.measuredHeight
                                                        }

// 리스트뷰의 높이를 동적으로 설정
                                                        val params = free_board_list.layoutParams
                                                        params.height = totalHeight + (free_board_list.dividerHeight * (adapter.count - 1))
                                                        free_board_list.layoutParams = params
                                                        free_board_list.requestLayout()
                                                    }

                                                    else{
                                                        activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                            var intent = Intent(context, StartActivity::class.java)
                                                            startActivity(intent)
                                                        }
                                                    }
//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                }
                                                override fun onFailure(call: Call<Board_List_Model>, t: Throwable) {

                                                }

                                            })

                                            match_board_list.getboardlist_matchlInfo("match","0","","","$accessToken")?.enqueue(object :
                                                Callback<Match_Board_List_Model> {
                                                override fun onResponse(
                                                    call: Call<Match_Board_List_Model>,
                                                    response: Response<Match_Board_List_Model>
                                                ) {

                                                    var match_length = response.body()?.list?.size
                                                    if (match_length != null) {
                                                        if(match_length<4){
                                                            for(i in 0..match_length-1){
                                                                match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                    response.body()?.list?.get(i)?.title,
                                                                    response.body()?.list?.get(i)?.format_create_date,
                                                                    response.body()?.list?.get(i)?.gender.toString(),
                                                                    response.body()?.list?.get(i)?.users.toString(),
                                                                    response.body()?.list?.get(i)?.num?.toInt()))
                                                            }
                                                        }
                                                        else{
                                                            for(i in 0..3){
                                                                match_board_list_array.add(Board_class(response.body()?.list?.get(i)?.category,
                                                                    response.body()?.list?.get(i)?.title,
                                                                    response.body()?.list?.get(i)?.format_create_date,
                                                                    response.body()?.list?.get(i)?.gender.toString(),
                                                                    response.body()?.list?.get(i)?.users.toString(),
                                                                    response.body()?.list?.get(i)?.num?.toInt()))
                                                            }
                                                        }
                                                    }
                                                    val match_adapter = MatchListAdapter(requireContext(), match_board_list_array)
                                                    var match_board_list1 : ListView = view.findViewById(R.id.list_matchboard)
                                                    match_board_list1.adapter = match_adapter
                                                    match_board_list1.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                                                        val selectedItem = parent.getItemAtPosition(position).toString()
                                                        var intent = Intent(context, MapsActivity::class.java)
                                                        intent.putExtra("board_num", match_board_list_array[position].num.toString()) // 데이터 넣기
                                                        token_management.prefs.setString("board_num",match_board_list_array[position].num.toString())
                                                        startActivity(intent)
                                                        // 선택한 아이템에 대한 작업을 수행할 수 있습니다.
                                                        // 예: 아이템 텍스트를 로그에 출력
                                                        Log.d("board_num", match_board_list_array[position].num.toString())
                                                    }
                                                    var totalHeight = 0
                                                    for (i in 0 until match_adapter.count) {
                                                        val listItem = match_adapter.getView(i, null, match_board_list1)
                                                        listItem.measure(
                                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                                        )
                                                        totalHeight += listItem.measuredHeight
                                                    }

// 리스트뷰의 높이를 동적으로 설정
                                                    val params = match_board_list1.layoutParams
                                                    params.height = totalHeight + (match_board_list1.dividerHeight * (match_adapter.count - 1))
                                                    match_board_list1.layoutParams = params
                                                    match_board_list1.requestLayout()

//                var text = response.body()
//                Toast.makeText(getActivity(), response.body()?.result+response.body()?.message, Toast.LENGTH_SHORT).show();
                                                }
                                                override fun onFailure(call: Call<Match_Board_List_Model>, t: Throwable) {

                                                }

                                            })
                                        }
                                        else{
                                            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
                                                var intent = Intent(context, StartActivity::class.java)
                                                startActivity(intent)
                                                naviActivity!!.finish()
                                            }
                                        }

                                    }
                                    override fun onFailure(call: Call<Token_data>, t: Throwable) {

                                    }

                                })

                                var free_board : TextView = view.findViewById(R.id.textView15) //자유게시판 이동
                                free_board.setOnClickListener {
                                    activity?.let{
                                        var intent = Intent(context, BoardActivity::class.java)
                                        intent.putExtra("boadr_type", "free") // 데이터 넣기
                                        token_management.prefs.setString("boadr_type", "free")
                                        startActivity(intent)
//                activity?.supportFragmentManager?.beginTransaction()?.remove(this@HomeFragment)?.commit()//프래그먼트 종료 함수
                                    }
                                }
                                var match_board : TextView = view.findViewById(R.id.textView16) //매치게시판 이동
                                match_board.setOnClickListener {
                                    activity?.let{
                                        var intent = Intent(context, BoardActivity::class.java)
                                        intent.putExtra("boadr_type", "match") // 데이터 넣기
                                        token_management.prefs.setString("boadr_type", "match")
                                        startActivity(intent)
//                activity?.supportFragmentManager?.beginTransaction()?.remove(this@HomeFragment)?.commit()//프래그먼트 종료 함수
                                    }
                                }
//        var ex : TextView = view.findViewById(R.id.textView20)
//        ex.setText(free_board_list_array.toString())
                                var board_list_array =arrayListOf<Board_class>(
                                    Board_class("주제","제목","날짜","좋아요","댓글"),
                                    Board_class("theme","title","date","favorite","comment"),
                                    Board_class("theme","title","date","favorite","comment")
                                )


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
//        var button : Button = view.findViewById(R.id.button)
//        button.setOnClickListener{


//            activity?.let{//프래그먼트 -> 액티비티 화면전환 방법
//                var intent = Intent(context, LoginActivity::class.java)
//                startActivity(intent)
//            }


//            var text : TextView = view.findViewById(R.id.textView12) //프래그먼트에서 위젯 사용방식
//            text.setText("버튼 클릭")

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}