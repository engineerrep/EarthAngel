package com.earth.angel.view


import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.github.chrisbanes.photoview.PhotoView


class PhotoEditAdapter(
    data: MutableList<String>
) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_photo_show, data) {

    override fun convert(holder: BaseViewHolder, item: String) {
        val ivContent: PhotoView = holder.getView(R.id.sivPhotoHeadimg)
        ivContent?.let {
            item?.let {
                Glide.with(context)
                    .load(it)
                    .into(ivContent!!)
            }
        }
    }

}
