package com.earth.angel.gift.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.CommentEntity


class ReportAdapter(
    commentEntity: MutableList<String>?,
    var upDade: (commentEntity: CommentEntity) -> Unit = {}
) : BaseQuickAdapter<String, BaseViewHolder>(
    R.layout.item_repot,
    commentEntity
) {
    companion object {
        var selectPosition = -1

    }

    override fun convert(holder: BaseViewHolder, item: String) {
        var tvReport: TextView? = holder.getView(R.id.tvReport)
        var ivReport: ImageView? = holder.getView(R.id.ivReport)
        tvReport?.text = item
            if(holder.layoutPosition==selectPosition){
                ivReport?.setImageResource(R.mipmap.report_yes)
            }else{
                ivReport?.setImageResource(R.mipmap.report_no)

            }
    }


}