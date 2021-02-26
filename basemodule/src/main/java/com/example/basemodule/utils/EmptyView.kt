package com.example.basemodule.utils

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.basemodule.R

import kotlinx.android.synthetic.main.view_empty.view.*

class EmptyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    private var callback: MyViewCallback? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_empty, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER
        tv_btn.setOnClickListener {
            callback?.callback(0, "")
        }
    }

    fun setText(s: String) {
        tv.text = s
    }

    fun setImg(id: Int) {
        iv.setImageResource(id)
    }

    fun setBtn(s: String, callback: MyViewCallback) {
        this.callback = callback
        tv_btn.text = s
        tv_btn.visibility = View.VISIBLE
    }
}
