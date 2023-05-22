package com.earth.angel.view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.earth.angel.R
import com.earth.libbase.util.BaseScreenUtil.getScreenWidth
import com.earth.libbase.view.loopview.LoopView

class PointPopMessageUtil {

    private var contentView: View? = null
    private var mLibHomeMoreDelete: TextView? = null
    fun showPopupWindow(context: Context, v: View, onItemClick: PointPopOnItemClick, item: String) {
        contentView =
            LayoutInflater.from(context).inflate(R.layout.point_popmessage, null)
        // Button button = (Button) contentView.findViewById(R.id.btn);
        val popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, true
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
        popupWindow.setOnDismissListener {
            //    onItemClick.onItemClick()
        }
        popupWindow.width = getScreenWidth()
        popupWindow.height = v.bottom
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
            val mLoopView: LoopView = it.findViewById(R.id.loopView)
            val listPoint =
                listOf(context.getString(R.string.label_Free), "10", "20", "30", "40", "50", "60", "70", "80", "90", "100")
            mLoopView.setCenterTextColor(ContextCompat.getColor(context, R.color.themColor))
            mLibHomeMoreDelete = it.findViewById(R.id.LibHomeMoreDelete)

            mLibHomeMoreCancel.setOnClickListener {
                val selectedItem: Int = mLoopView.selectedItem
                onItemClick.onItemClick(listPoint[selectedItem])
                popupWindow.dismiss()
            }

            mLibHomeShoppCl?.setOnClickListener {
                popupWindow.dismiss()
            }

            mLibHomeMoreDelete?.setOnClickListener {

                popupWindow.dismiss()

            }
            // 设置原始数据
            mLoopView.setItems(listPoint)
            if (item == "Free") {
                mLoopView.setCurrentPosition(0)
            } else {
                for (position in 1..10) {
                    var newPosition = position * 10
                    if (newPosition.toString() == item) {
                        mLoopView.setCurrentPosition(position)
                        break
                    }
                }
            }
        }

    }


}