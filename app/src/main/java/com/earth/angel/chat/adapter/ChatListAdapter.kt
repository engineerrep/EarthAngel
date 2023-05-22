package com.earth.angel.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.view.ShapedImageView

class ChatListAdapter(
     var mContext: Context?,
    userProfileBeans: MutableList<BaseGiftEntity>?,
    var upDade: (houseNumber: Int) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_chat_gift,
    userProfileBeans
) {
    companion object{
        var showDelete=false
    }

    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {

        var ivHeader: ShapedImageView? = holder.getView(R.id.sivHeadimg)
        var sivHeadimgEx: ImageView? = holder.getView(R.id.sivHeadimgEx)
        var sivHeadimgDelete: ImageView? = holder.getView(R.id.sivHeadimgDelete)
        var sivHeadimgPoint: TextView? = holder.getView(R.id.sivHeadimgPoint)


        item.score?.let {
            if(it==0){
                sivHeadimgPoint?.text = mContext?.getString(R.string.label_Free)
            }else{
                sivHeadimgPoint?.text = it.toString()
            }
        }

        if(showDelete){
            sivHeadimgDelete?.visibility=View.VISIBLE
        }else{
            sivHeadimgDelete?.visibility=View.GONE
        }
        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(context)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivHeader!!)
            }
        }
        item?.releaseStatus?.let {
            if(it==BaseGiftEntity.DOING){
                sivHeadimgEx?.visibility=View.GONE
            }else{
                sivHeadimgEx?.visibility=View.VISIBLE
            }
        }
        sivHeadimgDelete?.setOnClickListener {
            upDade(holder.layoutPosition)
        }

    }


}