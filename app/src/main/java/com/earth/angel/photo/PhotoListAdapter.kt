package com.earth.angel.photo

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.photo.gallery.GalleryModel


class PhotoListAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GalleryModel>?,
    var upDade: (galleryModel: GalleryModel) -> Unit = {}
) : BaseQuickAdapter<GalleryModel, BaseViewHolder>(
    R.layout.item_photo_list,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    companion object {
        var selectPosition = -1
    }

    override fun convert(holder: BaseViewHolder, item: GalleryModel) {
        var ivHeader: ImageView? = holder.getView(R.id.sivHeadimg)
        var iv_delete: ImageView? = holder.getView(R.id.iv_delete)
       // var clPopularityBg: Layer? = holder.getView(R.id.clPopularityBg)


        item.path?.let {
            Glide.with(context)
                .load(it)
                .into(ivHeader!!)
        }

        iv_delete?.setOnClickListener {
            upDade(item)

        }
        /*if(PhotoListAdapter.selectPosition ==holder.layoutPosition){
            clPopularityBg?.setBackgroundResource(R.drawable.shape_stroke_themcolor_8)
        }else{
            clPopularityBg?.setBackgroundResource(0)
        }*/


    }


}