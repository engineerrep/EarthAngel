package com.earth.libhome.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R


class GroupMainAdapter(
    var mContext: Activity?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_groupmain,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText,item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)

        item?.mainPicture?.let {
                Glide.with(ivHeader!!.context)
                    .load(it)
                    .placeholder(R.mipmap.base_img_loding)
                    .into(ivHeader)
        }


        if (holder.layoutPosition % 2 != 0) {
            //1
            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(BaseScreenUtil.getPixelsFromDp(18,mContext), 0, BaseScreenUtil.getPixelsFromDp(9,mContext), 0)
            ivHeader?.layoutParams = lp

        } else {
            //2
            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(BaseScreenUtil.getPixelsFromDp(9,mContext), 0, BaseScreenUtil.getPixelsFromDp(18,mContext), 0)
            ivHeader?.layoutParams = lp
        }
    }


}