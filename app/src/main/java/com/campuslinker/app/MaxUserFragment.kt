package com.campuslinker.app

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.campuslinker.app.databinding.FragmentMaxUserBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MaxUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MaxUserFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentMaxUserBinding? = null
    private val binding get() = _binding!!
    lateinit var matchboardactivity: MakematchBoardActivity
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

        _binding = FragmentMaxUserBinding.inflate(inflater, container, false)
        val view = binding.root
        matchboardactivity = context as MakematchBoardActivity
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.spinner5.adapter = ArrayAdapter.createFromResource(
            matchboardactivity,
            R.array.match_board_stid_1,
            android.R.layout.simple_spinner_item
        )
        binding.spinner6.adapter = ArrayAdapter.createFromResource(
            matchboardactivity,
            R.array.match_board_stid_2,
            android.R.layout.simple_spinner_item
        )
        if(binding.spinner6.selectedItem.equals("만")){
            binding.spinner7.visibility = View.VISIBLE
        }
        var bundle = Bundle()
        binding.button5.setOnClickListener {
            buttonClickListener.onButton1Clicked()



            dismiss()
        }



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
         * @return A new instance of fragment MaxUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MaxUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // 인터페이스
    interface OnButtonClickListener {
        fun onButton1Clicked()
    }
    // 클릭 이벤트 설정
    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener
}