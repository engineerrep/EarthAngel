package com.earth.angel.appphoto.adapter

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.view.ShapedImageView

class CutColorAdapter(
    data: MutableList<Int>
) :
    BaseQuickAdapter<Int, BaseViewHolder>(R.layout.item_cut_coclor, data) {
    companion object {
        var colorPosition = -1
    }
    override fun convert(holder: BaseViewHolder, item: Int) {
        val ivContent: ShapedImageView = holder.getView(R.id.sivHeadimg)
        val clPopularityBg: ConstraintLayout = holder.getView(R.id.clPopularityBg)
        val tvSpring: TextView = holder.getView(R.id.tvSpring)

        if(CutColorAdapter.colorPosition ==holder.layoutPosition){
            clPopularityBg.setBackgroundResource(R.drawable.shape_stroke_themcolor_8)
        }else{
            clPopularityBg.setBackgroundResource(0)
        }

        item?.let {
            when(holder.layoutPosition){
                0 -> {
                    ivContent.setImageResource(R.mipmap.iv_none)
                }
                else ->{
                    ivContent.setImageResource(0)
                    ivContent.setBackgroundColor(it)
                }
            }
        }
        when(holder.layoutPosition){
            0 -> tvSpring.text="None"
            1 -> tvSpring.text="White"
            2 -> tvSpring.text="Grey"
        }
    }

}
