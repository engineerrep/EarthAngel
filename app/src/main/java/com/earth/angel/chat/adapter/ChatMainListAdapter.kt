package com.earth.angel.chat.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.chat.CustomHelloMessage
import com.earth.libbase.view.ShapedImageView
import com.google.gson.Gson
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.qcloud.tim.uikit.utils.DateTimeUtil
import java.util.*

class ChatMainListAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<V2TIMConversation>?,
    var upDade: (houseNumber: Int) -> Unit = {}
) : BaseQuickAdapter<V2TIMConversation, BaseViewHolder>(
    R.layout.item_chat_main,
    userProfileBeans
) {
    override fun convert(holder: BaseViewHolder, item: V2TIMConversation) {
        holder.setText(R.id.itemChatMainName, item.showName)
        val head: ShapedImageView = holder.getView(R.id.itemChatMainHv)
        item?.faceUrl?.let {
            Glide.with(context)
                .load(it)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(head)
        }
        val headitemChatMainTime: TextView = holder.getView(R.id.itemChatMainGv)
        item?.lastMessage?.timestamp?.let {
            headitemChatMainTime.text =
                DateTimeUtil.getTimeFormatText(Date(it* 1000))
        }
        val content: TextView = holder.getView(R.id.itemChatMainTime)
        item?.lastMessage?.let {
            if(it.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT){
                content.text = it.textElem.text
            }else if(it.elemType == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM){
                var str= String(it.customElem.data)
                val mCustomHelloMessage: CustomHelloMessage = Gson().fromJson(str, CustomHelloMessage::class.java)
                content.text = mCustomHelloMessage.text
            }

        }
        val tvLibChatPacketNum: TextView = holder.getView(R.id.LibChatPacketNum)
        item?.unreadCount?.let {
            if(it==0){
                tvLibChatPacketNum.visibility= View.GONE
            }else{
                tvLibChatPacketNum.visibility= View.VISIBLE
            }
            tvLibChatPacketNum.text=it.toString()
        }

    }


}