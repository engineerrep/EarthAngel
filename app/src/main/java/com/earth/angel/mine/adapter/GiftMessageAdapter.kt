package com.earth.angel.mine.adapter

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.MessageEntity

class GiftMessageAdapter(
    MessageEntityBeans: MutableList<MessageEntity>?,
    var upDade: (position: Int) -> Unit = {}
) : BaseQuickAdapter<MessageEntity, BaseViewHolder>(
    R.layout.item_gift_message,
    MessageEntityBeans
) {
    override fun convert(holder: BaseViewHolder, item: MessageEntity) {

        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var tvName: TextView? = holder.getView(R.id.tvName)
        var tvQQNum: TextView? = holder.getView(R.id.tvQQNum)
        var tvMessage: TextView? = holder.getView(R.id.tvMessage)

        item?.headImgUrl?.let {
            if (it.isNotEmpty()) {
                Glide.with(context)
                    .load(it)
                    .into(ivHeader!!)
            }

        }

        item?.nickName?.let {
            tvName?.text = it
        }
        item?.contactDetails?.let {
            tvQQNum?.text = it
        }
        item?.msg?.let {
            tvMessage?.text = it
        }
    }


}