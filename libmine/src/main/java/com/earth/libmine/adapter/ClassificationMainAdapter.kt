package com.earth.libmine.adapter

import android.content.Context
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.CategoryEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R
import org.jetbrains.anko.dip

class ClassificationMainAdapter(var mContext: Context,  list: MutableList<CategoryEntity>) :
    BaseQuickAdapter<CategoryEntity, BaseViewHolder>(
        R.layout.item_libhmine_cation,
        list
    ) {
    override fun convert(holder: BaseViewHolder, item: CategoryEntity) {
        var mItemCation: ShapedImageView = holder.getView(R.id.ItemCation)
        var mItemCationTV: TextView = holder.getView(R.id.ItemCationTV)

        item?.coverPicture?.let {
            Glide.with(mContext)
                .load(it)
                .into(mItemCation)
        }
        item.description?.let {
            mItemCationTV.text=it
        }
        if (holder.layoutPosition % 2 != 0) {
            //1

            val lp: ConstraintLayout.LayoutParams =
                mItemCation?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(9)!!, 0, mContext?.dip(18)!!, 0)
            mItemCation?.layoutParams = lp
        } else {
            //2
            val lp: ConstraintLayout.LayoutParams =
                mItemCation?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(mContext?.dip(18)!!, 0, mContext?.dip(9)!!, 0)
            mItemCation?.layoutParams = lp
        }

    }

}