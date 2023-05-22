package com.earth.angel.appphoto.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.view.ShapedImageView

class CutTextAdapter(
    data: MutableList<Int>
) :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_cut_coclor, data) {
    companion object {
        var textPosition = -1
    }
    override fun convert(holder: BaseViewHolder, item: Int) {
        val ivContent: ShapedImageView = holder.getView(R.id.sivHeadimg)
        val clPopularityBg: ConstraintLayout = holder.getView(R.id.clPopularityBg)
        val tvSpring: TextView = holder.getView(R.id.tvSpring)
        if(CutTextAdapter.textPosition ==holder.layoutPosition){
            clPopularityBg.setBackgroundResource(R.drawable.shape_stroke_themcolor_8)
        }else{
            clPopularityBg.setBackgroundResource(0)
        }
        item?.let {
            ivContent.scaleType= ImageView.ScaleType.CENTER_INSIDE
            Glide.with(context)
                .load(item)
                .into(ivContent!!)
        }
        tvSpring.visibility= View.INVISIBLE
    }

}
