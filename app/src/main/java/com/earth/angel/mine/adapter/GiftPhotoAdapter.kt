package com.earth.angel.mine.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.PicTureEntity

class GiftPhotoAdapter(
    data: MutableList<PicTureEntity>,
    var olddata: MutableList<PicTureEntity>
) :
    BaseQuickAdapter<PicTureEntity, BaseViewHolder>(R.layout.item_gift_photo, data) {

    override fun convert(holder: BaseViewHolder, item: PicTureEntity) {
        val ivContent: ImageView = holder.getView(R.id.iv_thumb)
        val iv_thumbView: ImageView = holder.getView(R.id.iv_thumbView)
        val iv_thumbnum: TextView = holder.getView(R.id.iv_thumbnum)

        item?.pictureUrl?.let {
            Glide.with(context)
                .load(it)
                .into(ivContent!!)

        }
        when(holder.layoutPosition){
            0 ->{
                iv_thumbView.visibility=View.GONE
                iv_thumbnum.visibility=View.GONE
            }
            1 ->{
                iv_thumbView.visibility=View.GONE
                iv_thumbnum.visibility=View.GONE
            }
            2 ->{

               if(olddata.size==3){
                   iv_thumbView.visibility=View.GONE
                   iv_thumbnum.visibility=View.GONE
               }else{
                   iv_thumbView.visibility=View.VISIBLE
                   iv_thumbnum.visibility=View.VISIBLE
                   iv_thumbView.visibility=View.VISIBLE
                   iv_thumbnum.visibility=View.VISIBLE
                   var lenth=olddata.size -3
                   iv_thumbnum.text= "+$lenth"
               }

            }
            else ->{

            }
        }


    }

}
