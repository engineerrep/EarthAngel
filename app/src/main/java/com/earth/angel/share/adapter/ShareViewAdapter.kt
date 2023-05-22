package com.earth.angel.share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R


class ShareViewAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<View>?,
    var upDade: () -> Unit = {}
) : BaseQuickAdapter<View, BaseViewHolder>(
    R.layout.item_share_view,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var userProfileBeans: MutableList<View>? = userProfileBeans


    override fun convert(holder: BaseViewHolder, item: View) {
        var ivHeader: LinearLayout? = holder.getView(R.id.llShareMain)
        ivHeader?.addView(item)
        if(holder.layoutPosition==0){
            ivHeader?.setBackgroundResource(R.color.shareBg)
            item?.run {
                val ivShareDown: ImageView = this.findViewById(R.id.ivShareDown)
                ivShareDown?.setOnClickListener {
                    upDade()
                }
            }

        }else{
            ivHeader?.setBackgroundResource(R.color.white)
        }

    }

}