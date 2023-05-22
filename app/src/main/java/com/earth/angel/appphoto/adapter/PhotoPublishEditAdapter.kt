package com.earth.angel.appphoto.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.photo.gallery.GalleryModel


class PhotoPublishEditAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GalleryModel>?,
    var upDade: (galleryModel: GalleryModel) -> Unit = {}
) : BaseQuickAdapter<GalleryModel, BaseViewHolder>(
    R.layout.item_photopublish_edit,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    companion object {
        var selectPosition = -1
    }
    override fun convert(holder: BaseViewHolder, item: GalleryModel) {
        var ivHeader: ImageView? = holder.getView(R.id.ItemPublishEdit)
        item.path?.let {
            Glide.with(context)
                .load(it)
                .into(ivHeader!!)
        }
        var mItemCoverImage: ImageView? = holder.getView(R.id.ItemCoverImage)
            if(holder.layoutPosition==0){
                mItemCoverImage?.visibility=View.VISIBLE
            }else{
                mItemCoverImage?.visibility=View.GONE
            }


        //      var ItemPublishCover: TextView? = holder.getView(R.id.ItemPublishCover)
        /*     if(holder.layoutPosition==0){
                 ItemPublishCover?.visibility= View.VISIBLE
             }else{
                 ItemPublishCover?.visibility= View.GONE
             }*/
    }


}