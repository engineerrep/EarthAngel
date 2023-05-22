package com.earth.angel.share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.libbase.entity.GiftEntity


class UserShareAdapter(
    mContext: Context?,
    userProfileBeans: MutableList<GiftEntity>?,
    var upDade: (GiftEntity: GiftEntity) -> Unit = {}
) : BaseQuickAdapter<GiftEntity, BaseViewHolder>(
    R.layout.item_user_share,
    userProfileBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var userProfileBeans: MutableList<GiftEntity>? = userProfileBeans


    override fun convert(holder: BaseViewHolder, item: GiftEntity) {

        var ivHeader: ImageView? = holder.getView(R.id.ivPhoto)
        var tvName: TextView? = holder.getView(R.id.tvUserName)
     //   var tvValue: TextView? = holder.getView(R.id.tvValue)
     //   var tvValueNum: TextView? = holder.getView(R.id.tvValueNum)
        var gift_issend: ImageView? = holder.getView(R.id.ivIsSend)
        if(item.isExchange==null){
            gift_issend?.visibility=View.GONE
        }else{
            gift_issend?.visibility=View.VISIBLE
            if(item.isExchange==1){
                gift_issend?.visibility=View.VISIBLE

            }else{
                gift_issend?.visibility=View.GONE

            }
        }
    /*    item.isConcern?.let {
            if (it) {
                if(item.isExchange==1){
                    lyMessage?.visibility = View.GONE
                }else{
                    lyMessage?.visibility = View.VISIBLE
                }
            } else {
                lyMessage?.visibility = View.GONE
            }
        }*/

      /*  item.valuation.let {
            if (it == 0.0) {
                tvValue?.visibility = View.GONE
                tvValueNum?.visibility = View.GONE
            } else {
                tvValue?.visibility = View.VISIBLE
                tvValueNum?.visibility = View.VISIBLE
                tvValueNum?.setText("$$it")
            }

        }*/
        item?.pictureResources?.let {
            if (it.isNotEmpty()) {
                Glide.with(context)
                    .load(it[0].pictureUrl)
                    .into(ivHeader!!)
            }
        }
        if (item?.description.isNullOrEmpty()) {
            tvName?.visibility = View.GONE
        } else {
            tvName?.visibility = View.VISIBLE
            tvName?.text = item?.description.toString().trim()
        }
    }

}