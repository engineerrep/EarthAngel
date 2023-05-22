package com.earth.libmine.adapter

import android.app.Activity
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R
import org.jetbrains.anko.dip


class MineGiftListAdapter(
    var mContext: Activity?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_giftlist,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        holder.setText(R.id.tvText,item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivHeader)
            }
        }
        if (holder.layoutPosition % 2 != 0) {
            //1

            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(9)!!, 0, mContext?.dip(18)!!, 0)
            ivHeader?.layoutParams = lp
        } else {
            //2
            val lp: ConstraintLayout.LayoutParams =
                ivHeader?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(18)!!, 0, mContext?.dip(9)!!, 0)
            ivHeader?.layoutParams = lp
        }
    }


}