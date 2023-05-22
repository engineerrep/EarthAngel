package com.earth.angel.appphoto.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.photo.gallery.GalleryModel


class PhotoPostAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GalleryModel>?,
    var upDade: (galleryModel: GalleryModel) -> Unit = {}
) : BaseQuickAdapter<GalleryModel, BaseViewHolder>(
    R.layout.item_photolist_post,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    companion object {
        var selectPosition = -1

    }

    override fun convert(holder: BaseViewHolder, item: GalleryModel) {
        var ivHeader: ImageView? = holder.getView(R.id.sivHeadimg)
        var iv_delete: ImageView? = holder.getView(R.id.iv_delete)
        var clPopularityBg: ConstraintLayout? = holder.getView(R.id.clPopularityBg)
        var sivHeadImgCover: TextView? = holder.getView(R.id.sivHeadImgCover)


        item.path?.let {
            Glide.with(context)
                .load(it)
                .into(ivHeader!!)
        }

        iv_delete?.setOnClickListener {
            upDade(item)

        }
        if(PhotoPostAdapter.selectPosition ==holder.layoutPosition){
            clPopularityBg?.setBackgroundResource(R.drawable.shape_stroke_themcolor_8)
        }else{
            clPopularityBg?.setBackgroundResource(0)
        }
        if(holder.layoutPosition==0){
            sivHeadImgCover?.visibility= View.VISIBLE
        }else{
            sivHeadImgCover?.visibility= View.GONE
        }

    }


}