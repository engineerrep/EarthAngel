package com.earth.angel.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Layer
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.gift.ui.UserMainActivity
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.entity.SearchDetailEntity
import com.earth.libbase.util.PreferencesHelper

class SearchUserdapter (
    mContext: Context?,
    userProfileBeans: MutableList<SearchDetailEntity>?,
    var upDade: (bean: SearchDetailEntity) -> Unit = {}
    ) : BaseQuickAdapter<SearchDetailEntity, BaseViewHolder>(
    R.layout.item_search_user,
    userProfileBeans
    ) {
        private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)


        override fun convert(holder: BaseViewHolder, item: SearchDetailEntity) {

            var ivHeader: ImageView? = holder.getView(R.id.iv)
            var tvname: TextView? = holder.getView(R.id.tv)
            var tvNum: TextView? = holder.getView(R.id.tvNum)
            var ivJoined: Layer? = holder.getView(R.id.lyJoined)
            var tvJoined: TextView? = holder.getView(R.id.tvJoined)
            ivHeader?.setOnClickListener {
                if (DateUtils.isFastClick()){
                    DataReportUtils.getInstance().report("Listing_details_photo")
                    UserMainActivity.openUserMainActivity(context,item)
                }

            }
            tvname?.setOnClickListener {
                if (DateUtils.isFastClick()){
                    DataReportUtils.getInstance().report("Listing_details_username")
                    UserMainActivity.openUserMainActivity(context,item)
                }

            }
            item?.imgUrl?.let {
                Glide.with(context)
                    .load(it)
                    .placeholder(R.mipmap.head_common)
                    .into(ivHeader!!)
            }
            item?.name?.let {
                tvname?.text=it
            }
            item?.number?.let {
                tvNum?.text="ID:$it"
            }
            ivJoined?.setOnClickListener {
                upDade(item)
            }
            if(PreferencesHelper.getUserId(context)== item.number){
                tvJoined?.visibility= View.GONE
            }

            item?.status?.let {
                if(it){
                    ivJoined?.visibility= View.GONE
                    tvJoined?.text=context.getString(R.string.lab_Messages)
                    ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_joined)
                }else{
                    ivJoined?.visibility= View.VISIBLE
                    if(item?.requestFrendsStatus){
                        tvJoined?.text=context.getString(R.string.lab_Cancel_Request)
                        ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                    }else{
                        tvJoined?.text=context.getString(R.string.lab_Add)
                        ivJoined?.setBackgroundResource(R.drawable.shape_corner_group_join)
                    }

                }
            }

        }


    }