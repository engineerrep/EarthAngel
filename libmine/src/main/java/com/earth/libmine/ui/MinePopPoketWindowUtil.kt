package com.earth.libmine.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.util.BaseScreenUtil.getScreenHeight
import com.earth.libbase.util.BaseScreenUtil.getScreenWidth
import com.earth.libmine.R
import com.earth.libmine.adapter.MinePopAddAdapter
import java.util.ArrayList

class MinePopPoketWindowUtil {

    private var mMinePopAddAdapter: MinePopAddAdapter? = null
    private var listString: ArrayList<BaseGiftEntity> = ArrayList()
    private var contentView: View? = null
    private var popupWindow: PopupWindow? = null
    private var tvLibMineDeletetv: TextView?=null

    fun showPopupWindow(context: Context?, v: View, onItemClick: MineOnItemClick) {
        contentView =
            LayoutInflater.from(context).inflate(R.layout.mine_popadd_gift, null)
        // Button button = (Button) contentView.findViewById(R.id.btn);
         popupWindow = PopupWindow(
            contentView,
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false
        )
        popupWindow?.isTouchable = true
        val mLibMinePopBg: TextView = contentView!!.findViewById(R.id.LibMinePopBg)
        mLibMinePopBg.setOnClickListener {
            popupWindow?.dismiss()
        }
        /*   button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onclick==action",
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        popupWindow?.isClippingEnabled = false
        //检测屏幕消失的事件
        popupWindow?.setOnDismissListener { onItemClick.onItemClick() }
        popupWindow?.width = getScreenWidth()
        var top=v.top
        var mhight= getScreenHeight()-top
        popupWindow?.height = getScreenHeight()-mhight
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        //获取自身的长宽高
        contentView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        //获取控件在屏幕上的位置，并赋值给location数组
        val location = IntArray(2)
        v.getLocationOnScreen(location)
        //在控件上方显示
        popupWindow?.showAtLocation(v, Gravity.TOP, 0, 0)
        // 如果要在控件下方显示，则使用这个方法
        //popupWindow.showAsDropDown(v);
        initList(context,onItemClick)
    }

    private fun initList(context: Context?,onItemClick: MineOnItemClick) {
        contentView?.let {

            val mLibMineShoppRlv: RecyclerView = it.findViewById(R.id.libMineShoppRlv)
            tvLibMineDeletetv= it.findViewById(R.id.tvLibMineDeletetv)

            val layoutManager1 = LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            mMinePopAddAdapter = MinePopAddAdapter(context, listString,upDade = {
                onItemClick.onItemDelete(it)
            })
            tvLibMineDeletetv?.setOnClickListener {
                var list: ArrayList<Long> = ArrayList()
                    for(item in listString){
                        list.add(item.releaseRecordId.toLong())
                    }
                onItemClick.onItemDeleteALL(list)
            }

            mLibMineShoppRlv.layoutManager = layoutManager1
            mLibMineShoppRlv.adapter = mMinePopAddAdapter

        }
    }

    open fun adapterList(listString: ArrayList<BaseGiftEntity>) {
        this.listString.clear()
        this.listString.addAll(listString)
        mMinePopAddAdapter?.notifyDataSetChanged()
        notifyEmpty()
    }
    open fun remove(bean: BaseGiftEntity) {
        this.listString.remove(bean)
        mMinePopAddAdapter?.notifyDataSetChanged()
        notifyEmpty()
    }
    open fun removeALL() {
        this.listString.clear()
        mMinePopAddAdapter?.notifyDataSetChanged()
        notifyEmpty()
    }

    open fun notifyEmpty() {
        if(listString.isNullOrEmpty()){
            tvLibMineDeletetv?.visibility=View.INVISIBLE
        }else{
            tvLibMineDeletetv?.visibility=View.VISIBLE
        }
    }

    open fun show(): Boolean {
        if (popupWindow!= null) {
            popupWindow?.isFocusable = true;//要先让popupwindow获得焦点，才能正确获取popupwindow的状态
            if(popupWindow!!.isShowing){
                popupWindow?.dismiss()
                return true
            }else{
                return false
            }
        }
        return false
    }
    open fun isShow(): Boolean {
        if (popupWindow!= null) {
            return popupWindow!!.isShowing
        }
        return false
    }
    open fun closed() {
        if (popupWindow!= null) {
            popupWindow!!.dismiss()
        }
    }
}