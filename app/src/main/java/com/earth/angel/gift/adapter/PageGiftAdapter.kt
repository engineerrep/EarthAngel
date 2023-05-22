package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.TimeForUtils
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.ShareUtil


class PageGiftAdapter(
    mContext: Context?,
    giftBeans: MutableList<GiftEntity>?,
    var upDade: (position: Int) -> Unit = {},
    var upMessage: (position: Int) -> Unit = {},
    var upUser: (item: GiftEntity, position: Int) -> Unit = { giftEntity: GiftEntity, i: Int -> },
    var upfollowe: (item: GiftEntity) -> Unit = {},
    var upReport: (item: GiftEntity) -> Unit = {},
    var upLike: (item: GiftEntity) -> Unit = {},
) : BaseQuickAdapter<GiftEntity, BaseViewHolder>(
    R.layout.item_page_gift,
    giftBeans
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun convert(holder: BaseViewHolder, item: GiftEntity) {
        var ivHeader: ImageView? = holder.getView(R.id.ivHead)
        var tvName: TextView? = holder.getView(R.id.tvName)
        var tvMessage: TextView? = holder.getView(R.id.tvMessage)
        var ivGift: ImageView? = holder.getView(R.id.ivGift)
        var ivCollect: ImageView? = holder.getView(R.id.ivCollect)
        var giftDetailsSent: ImageView? = holder.getView(R.id.gift_details_sent)

        var time: TextView? = holder.getView(R.id.time)

        var ivFree: ImageView? = holder.getView(R.id.ivFree)
        var tvFree: TextView? = holder.getView(R.id.tvFree)
        var tvFavorited: TextView? = holder.getView(R.id.tvFavorited)
        var llCollect: LinearLayout? = holder.getView(R.id.llCollect)

        var ivMessage: LinearLayout? = holder.getView(R.id.llMessage)
        var llShare: LinearLayout? = holder.getView(R.id.llShare)
        var follow: ImageView? = holder.getView(R.id.followed)
        var ivMore: ImageView? = holder.getView(R.id.ivMore)
        var follTV: TextView? = holder.getView(R.id.follTV)
        var tvComment: TextView? = holder.getView(R.id.tvComment)

            if(item.isExchange==1){
                giftDetailsSent?.visibility= View.VISIBLE
            }else{
                giftDetailsSent?.visibility= View.GONE
            }

        if (PreferencesHelper.getUserId(context) == item.userId) {
            ivMore?.visibility = View.GONE
        }else{
            ivMore?.visibility = View.VISIBLE
        }
        follTV?.setOnClickListener {
            if (DateUtils.isFastClick()){
                upLike(item)

            }
        }
        item.isConcern.let {
            if (it) {
                follow?.visibility = View.GONE
                follTV?.visibility = View.GONE
            } else {
                follow?.visibility = View.VISIBLE
                follTV?.visibility = View.VISIBLE
            }
        }
        item.releasedCommentNumbers?.let {
            if(it==0){
                tvComment?.setText("Comment")
            }else{
                tvComment?.setText("Comment($it)")
            }
        }

        item?.createTime?.let {
            time?.text = TimeForUtils.getGiftDtailsTime(it.toLong())
        }

        ivMore?.setOnClickListener {
            if (DateUtils.isFastClick()){
                upReport(item)

            }
        }


        ivHeader?.setOnClickListener {
            if (DateUtils.isFastClick()){
                upUser(item, holder.layoutPosition)

            }
            //   UserMainActivity.openUserMainActivity(context, item)
        }
        item.isConcern?.let { itbooleav ->
            if (itbooleav) {
                follow?.visibility = View.GONE
            } else {
                follow?.visibility = View.VISIBLE
            }
        }
        follow?.setOnClickListener {
            if (DateUtils.isFastClick()){
                upfollowe(item)

            }
        }
        item?.headImgUrl?.let {
            Glide.with(context)
                .load(it)
                .into(ivHeader!!)

        }
        item?.nickName?.let {
            tvName?.text = it
        }

        if (item?.description.isNullOrEmpty()) {
            tvMessage?.visibility = View.GONE
        } else {
            tvMessage?.visibility = View.VISIBLE
            tvMessage?.text = item?.description
        }
        item?.pictureResources?.let {
            if (it.isNotEmpty()) {
                Glide.with(context)
                    .load(it[0].pictureUrl)
                    .into(ivGift!!)
            }
        }
        item?.isLike?.let {
            if (it) {
                ivCollect?.setImageResource(R.mipmap.collect)
                tvFavorited?.text = context.getString(R.string.lab_Favored)
            } else {
                ivCollect?.setImageResource(R.mipmap.collection_un)
                tvFavorited?.text = context.getString(R.string.lab_Favorite)
            }
        }

        llCollect?.setOnClickListener {
            if (DateUtils.isFastClick()) {
                upDade(holder.layoutPosition)
            }
        }

        item?.sendMode?.let {
            if (it == 0) {
                ivFree?.setImageResource(R.mipmap.free)
                tvFree?.text = "Free"
            } else {
                ivFree?.setImageResource(R.mipmap.exchange)
                tvFree?.text = "Exchange"
            }
        }
        llShare?.setOnClickListener {
            if (DateUtils.isFastClick()) {
                DataReportUtils.getInstance().report(" Profile_details_share")
                ShareUtil.shareGoods(context, item.userId.toString(), item.id.toString())
            }

        }
    }


}