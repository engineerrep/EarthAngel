package com.earth.libhome.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.util.BaseScreenUtil.getScreenHeight
import com.earth.libbase.util.BaseScreenUtil.getScreenWidth
import com.earth.libhome.R

import java.util.ArrayList

class HomePopMessageUtil {

    private var contentView: View? = null
    private  var mLibHomeMoreDelete: TextView? = null
    fun showPopupWindow(context: Context?, v: View, onItemClick: HomeOnItemClick,message: String) {
        contentView =
            LayoutInflater.from(context).inflate(R.layout.home_popmessage, null)
        // Button button = (Button) contentView.findViewById(R.id.btn);
        val popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true
        )
        popupWindow.isTouchable = true
        /*   button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onclick==action",
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        popupWindow.isClippingEnabled = false
        //检测屏幕消失的事件
        popupWindow.setOnDismissListener { onItemClick.onItemClick() }
        popupWindow.width = getScreenWidth()
        popupWindow.height = getScreenHeight()
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        //获取自身的长宽高
        contentView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = contentView?.measuredHeight!!
        val popupWidth = contentView?.measuredWidth
        //获取控件在屏幕上的位置，并赋值给location数组
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
        // 如果要在控件下方显示，则使用这个方法
        //popupWindow.showAsDropDown(v);
        contentView?.let {
            val mLibHomeMoreCancel: TextView = it.findViewById(R.id.LibHomeMoreCancel)
            val mLibHomeShoppCl: ConstraintLayout = it.findViewById(R.id.LibHomeShoppCl)
            mLibHomeMoreDelete = it.findViewById(R.id.LibHomeMoreDelete)

            mLibHomeMoreCancel.setOnClickListener {
                popupWindow.dismiss()
            }

            mLibHomeShoppCl?.setOnClickListener {
                popupWindow.dismiss()
            }

            mLibHomeMoreDelete?.setOnClickListener {
                onItemClick.onItemDeleteClick()
                popupWindow.dismiss()

            }

            mLibHomeMoreDelete?.text= message

        }

    }



}