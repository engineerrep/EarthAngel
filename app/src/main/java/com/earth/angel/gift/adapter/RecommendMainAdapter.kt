package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R

import com.earth.libbase.entity.GiftEntity

class RecommendMainAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GiftEntity>?,
    var upDade: () -> Unit = {}
) : BaseQuickAdapter<GiftEntity, BaseViewHolder>(
    R.layout.item_recommend_main,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: GiftEntity) {

        var ivHeader: ImageView? = holder.getView(R.id.test_custom_message_iv)
        var tvMessage: TextView? = holder.getView(R.id.test_custom_message_tv)
        var tvAboutThis: TextView? = holder.getView(R.id.tvAboutThis)
        var chat_cl_bg: ConstraintLayout? = holder.getView(R.id.chat_cl_bg)



        item?.pictureResources?.let {
            if (!it[0].pictureUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(it[0].pictureUrl)
                    // .apply(RequestOptions.bitmapTransform(BlurTransformation(5, 8)))
                    .into(ivHeader!!)
            }
        }

        item?.description?.let {
            tvMessage?.text = it
        }

        tvAboutThis?.setOnClickListener {
            upDade()
        }

    }


}