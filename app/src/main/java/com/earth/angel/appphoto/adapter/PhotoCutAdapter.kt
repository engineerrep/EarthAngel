package com.earth.angel.appphoto.adapter

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.PhotoCutEntity
import com.earth.libbase.view.ShapedImageView

class PhotoCutAdapter(
    data: MutableList<PhotoCutEntity>
) :
    BaseQuickAdapter<PhotoCutEntity, BaseViewHolder>(R.layout.item_photo_edit_show, data) {
    companion object {
        var selectPosition = -1
    }
    override fun convert(holder: BaseViewHolder, item: PhotoCutEntity) {
        val ivContent: ShapedImageView = holder.getView(R.id.sivHeadimg)
        val clPopularityBg: ConstraintLayout = holder.getView(R.id.clPopularityBg)
        val ivDelete: ImageView = holder.getView(R.id.iv_delete)
        if(selectPosition==holder.layoutPosition){
            clPopularityBg.setBackgroundResource(R.drawable.shape_stroke_themcolor_8)
            ivDelete.setImageResource(R.mipmap.new_picture)
        }else{
            clPopularityBg.setBackgroundResource(0)
            ivDelete.setImageResource(R.mipmap.old_picture)
        }
        item?.let {
            Glide.with(context)
                .load(it.sdPath)
                .into(ivContent!!)
        }
     /*   if (item.check) {
            item?.let {
                Glide.with(context)
                    .load(it.changePath)
                    .into(ivContent!!)
            }
        } else {
            item?.let {
                Glide.with(context)
                    .load(it.path)
                    .into(ivContent!!)
            }
        }*/
    }

}
