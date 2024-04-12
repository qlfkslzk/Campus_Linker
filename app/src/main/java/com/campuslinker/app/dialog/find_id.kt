package com.campuslinker.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.campuslinker.app.R

class find_id (context: Context)
{
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }

    fun showDialog_id()
    {
        dialog.setContentView(R.layout.find_id_dialog)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val edit_email = dialog.findViewById<EditText>(R.id.find_edit_email)

        dialog.findViewById<Button>(R.id.send_id).setOnClickListener {
            onClickListener.onClicked(edit_email.text.toString())
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener
    {
        fun onClicked(email: String)
    }

}