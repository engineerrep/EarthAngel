package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R

class HomePackingBagDetailsAdapter (
    var mContext: Context,
    commentEntity: MutableList<BaseGiftEntity>?,
    var upDade: (str: String,position: Int) -> Unit = { s: String, i: Int -> }
) : BaseQuickAdapter<BaseGiftEntity, BaseViewHolder>(
    R.layout.item_libhome_packeddetalis,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: BaseGiftEntity) {
        var tvText: TextView? = holder.getView(R.id.tvText)
        tvText?.text = item.itemTitle
        holder.setText(R.id.tvText,item.itemTitle)
        var ivHeader: ShapedImageView? = holder.getView(R.id.ivImage)
        var ivContent: ImageView? = holder.getView(R.id.MinePopAddShiXiao)
        var ivMinePopAdd: ImageView? = holder.getView(R.id.MinePopAdd)
        ivMinePopAdd?.setOnClickListener {
            if (BaseDateUtils.isFastClick()){
                upDade(item.releaseRecordId,holder.layoutPosition)
            }

            }
        item?.pictureResources?.let {
            if(it.size>0){
                Glide.with(ivHeader!!.context)
                    .load(it[0])
                    .into(ivHeader)
            }
        }
        if(item.releaseStatus!=null){
            item?.releaseStatus?.let {
                when(it){
                    0 -> {
                        ivContent?.visibility= View.GONE
                        tvText?.setTextColor(ContextCompat.getColor(mContext,R.color.text_black))
                    }
                    else -> {
                        ivContent?.visibility= View.VISIBLE
                        tvText?.setTextColor(ContextCompat.getColor(mContext,R.color.textExpired))
                    }
                }
            }
        }else{
            ivContent?.visibility= View.GONE

        }
    }


}