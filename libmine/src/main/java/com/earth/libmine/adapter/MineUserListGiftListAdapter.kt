package com.earth.libmine.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R
import com.scwang.smart.refresh.layout.SmartRefreshLayout


class MineUserListGiftListAdapter(
    var mContext: Activity?,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: BaseGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libmine_homegiftlist,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {

        holder.setText(R.id.tvText,item.itemTitle)
        var ivImage: ShapedImageView? = holder.getView(R.id.ivImage)
        var sivMineImgEx: ImageView? = holder.getView(R.id.sivMineImgEx)

        item?.releaseStatus?.let {
            if(it==0){
                sivMineImgEx?.visibility= View.GONE
            }else{
                sivMineImgEx?.visibility= View.VISIBLE
            }
        }

        if (holder.layoutPosition % 2 != 0) {
            //1
            val lp: ConstraintLayout.LayoutParams =
                ivImage?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(BaseScreenUtil.getPixelsFromDp(18,mContext), 0, BaseScreenUtil.getPixelsFromDp(9,mContext), 0)
            ivImage?.layoutParams = lp

        } else {
            //2
            val lp: ConstraintLayout.LayoutParams =
                ivImage?.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(BaseScreenUtil.getPixelsFromDp(9,mContext), 0, BaseScreenUtil.getPixelsFromDp(18,mContext), 0)
            ivImage?.layoutParams = lp
        }

        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivImage!!.context)
                    .load(it[0])
                    .placeholder(R.mipmap.base_comm_img)
                    .into(ivImage)
            }
        }

    }


}