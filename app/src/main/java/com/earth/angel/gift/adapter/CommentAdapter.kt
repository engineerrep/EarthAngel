package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.CommentEntity


class CommentAdapter(
    mContext: Context?,
    commentEntity: MutableList<CommentEntity>?,
    var upDade: (commentEntity: CommentEntity) -> Unit = {}
) : BaseQuickAdapter<CommentEntity, BaseViewHolder>(
    R.layout.item_comment,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: CommentEntity) {
        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var ivName: TextView? = holder.getView(R.id.tv)
        var tvComment: TextView? = holder.getView(R.id.tvComment)
        item?.commentUserHeadImgUrl?.let {
            Glide.with(context)
                .load(it)
                .placeholder(R.mipmap.head_common)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
        }
        item?.commentUserNickName?.let {
            ivName?.text=it
        }
        item?.commentUserMsg?.let {
            tvComment?.text=it
        }
    }


}