package com.earth.angel.chat.adapter

import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.view.ShapedImageView

class ChatCustomListAdapter(
    userProfileBeans: MutableList<BaseGiftEntity>?
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_chatcustom_list,
    userProfileBeans
) {
    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {

        var mCustomImg: ShapedImageView? = holder.getView(R.id.CustomImg)
        var mCustomTv: TextView? = holder.getView(R.id.CustomTv)

        item?.pictureResources?.let {
            if (it.size > 0) {
                Glide.with(context)
                    .load(it[0])
                    .into(mCustomImg!!)
            }
        }
        item?.itemTitle?.let {
            mCustomTv?.text = it
        }
    }


}