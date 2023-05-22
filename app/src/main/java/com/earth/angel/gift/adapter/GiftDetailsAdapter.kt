package com.earth.angel.gift.adapter

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.PicTureEntity

class GiftDetailsAdapter(
    data: MutableList<PicTureEntity>?,
    var isExchange: Int,
    var addImg: () -> Unit = {},
    var upDade: (position: Int) -> Unit = {}
) :
    BaseQuickAdapter<PicTureEntity, BaseViewHolder>(R.layout.item_gift_details, data) {

    override fun convert(holder: BaseViewHolder, item: PicTureEntity) {
        val iv_thumb: ImageView=holder.getView(R.id.iv_thumb)
        val iv_details_sent: ImageView=holder.getView(R.id.gift_details_sent)

        item?.pictureUrl?.let {
            Glide.with(context)
                .load(it)
                .into(iv_thumb)
        }
        if(holder.layoutPosition==0){
            if(isExchange==1){
                iv_details_sent.visibility= View.VISIBLE
            }else{
                iv_details_sent.visibility= View.GONE
            }
        }else{
            iv_details_sent.visibility= View.GONE
        }
    }

}
