package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.base.clickWithTrigger
import com.earth.libbase.entity.GiftHouseEntity


class RecommendGroupsAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GiftHouseEntity>?,
    var upDade: (houseNumber: Long) -> Unit = {}
) : BaseQuickAdapter<GiftHouseEntity, BaseViewHolder>(
    R.layout.item_group_recommend,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: GiftHouseEntity) {
        var ivHeader: ImageView? = holder.getView(R.id.iv)
        var ivJoined: Layer? = holder.getView(R.id.lyJoined)

        var tvDistance: TextView? = holder.getView(R.id.tv)
        var tvmessage: TextView? = holder.getView(R.id.tvmessage)

        item?.houseLogo?.let {
            Glide.with(context)
                .load(it)
                // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                .into(ivHeader!!)
        }
        item?.houseName?.let {
            tvDistance?.text=it
        }
        ivJoined?.clickWithTrigger{
            upDade(item.houseNumber)
        }
        item?.members?.let {
            tvmessage?.text= "[ $it people]"
        }
    }


}