package com.earth.libmine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R


class MineOffGiftItemAdapter(
    mContext: Context?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_off_item,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText,item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        var sivMineImgEx: ImageView? = holder.getView(R.id.sivMineImgEx)

        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .into(ivHeader)
            }
        }
        item?.releaseStatus?.let {
            if(it==0){
                sivMineImgEx?.visibility= View.GONE
            }else{
                sivMineImgEx?.visibility= View.VISIBLE
            }

        }
    }


}