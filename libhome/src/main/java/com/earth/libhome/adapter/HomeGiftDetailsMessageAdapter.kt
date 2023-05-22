package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseGiftEntity

import com.earth.libbase.entity.MessageListEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.ImDateTimeUtil
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R
import java.util.*


class HomeGiftDetailsMessageAdapter(
    var mContext: Context,
    commentEntity: MutableList<MessageListEntity>?,
    var upDade: (str: MessageListEntity) -> Unit = {}
) : BaseQuickAdapter<MessageListEntity, BaseViewHolder>(
    R.layout.item_libhome_messagelist,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: MessageListEntity) {

        var ivMessageImage: ShapedImageView? = holder.getView(R.id.ivMessageImage)
        var ivMessageName: TextView? = holder.getView(R.id.ivMessageName)
        var ivMessageTime: TextView? = holder.getView(R.id.ivMessageTime)
        var ivMessageContent: TextView? = holder.getView(R.id.ivMessageContent)
        item?.let {
            it.headImgUrl?.let {
                Glide.with(mContext)
                    .load(it)
                    .placeholder(R.mipmap.base_comm_head)
                    .into(ivMessageImage!!)
            }
            it.userId?.let { userId ->
                ivMessageImage?.setOnClickListener {
                    if (BaseDateUtils.isFastClick()) {
                            ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                                .withString("userId", userId.toString())
                                .navigation()
                    }
                }
            }

            it.nickName?.let {
                ivMessageName?.text=it
            }
            it.createTime?.let {
                ivMessageTime?.text=  ImDateTimeUtil.getTimeFormatText(Date(it.toLong()))
            }
            it.msg?.let {
                ivMessageContent?.text=it
            }
        }

    }


}