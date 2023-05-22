package com.earth.libmine.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.ArticleMainEntity
import com.earth.libbase.entity.PointRecordEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.ShapedImageView
import com.earth.libmine.R


class PointRecordAdapter(val mContext: Context, list: MutableList<PointRecordEntity>) :
    BaseQuickAdapter<PointRecordEntity, BaseViewHolder>(
        R.layout.item_point_record, list
    ) {

    override fun convert(holder: BaseViewHolder, item: PointRecordEntity) {
        var type=  when(item.changeType){
            "SIGN_IN" -> {
                PointRecordEntity.SIGN_IN
            }
            "SHARE" -> {
                PointRecordEntity.SHARE
            }
            "READ" -> {
                PointRecordEntity.READ
            }
            "GIVE_AWAY" -> {
                PointRecordEntity.GIVE_AWAY
            }
            "ASK_FOR" -> {
                PointRecordEntity.ASK_FOR
            }
            "RELEASE" -> {
                PointRecordEntity.RELEASE
            }
             else ->{
                 ""
             }

        }
        holder.setText(R.id.itemPointName,type)
        if(item.paymentType=="INCOME"){
            holder.setText(R.id.itemPointTVQJ,"+")
            holder.setText(R.id.itemPointTV,item.score.toString())
        }else{
            holder.setText(R.id.itemPointTVQJ,"-")
            holder.setText(R.id.itemPointTV,item.score.toString())
        }
        holder.setText(R.id.itemPointTime, BaseDateUtils.getPointTime(item.createTime))

    }


}