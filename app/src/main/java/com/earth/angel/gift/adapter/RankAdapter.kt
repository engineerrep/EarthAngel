package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R


class RankAdapter(
    mContext: Context?,
    string: MutableList<String>?
) : BaseQuickAdapter<String, BaseViewHolder>(
    R.layout.item_rank,
    string
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    companion object {
        var selectPosition = 0
    }
    override fun convert(holder: BaseViewHolder, item: String) {
        var ivRank: TextView? = holder.getView(R.id.titleRank)

        item?.let {
            ivRank?.text=it
        }
        var position=holder.layoutPosition
        if(selectPosition==position){
            ivRank?.setBackgroundResource(R.drawable.shape_corner_8sp_themcoclor)
            ivRank?.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else{
            ivRank?.setBackgroundResource(R.drawable.shape_corner_8sp_uncoclor)
            ivRank?.setTextColor(ContextCompat.getColor(context, R.color.rank_un))
        }

    }


}