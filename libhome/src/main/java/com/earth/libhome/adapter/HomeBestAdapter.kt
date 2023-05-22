package com.earth.libhome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.base.Constance
import com.earth.libbase.baseentity.BaseBestGiftEntity

import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R


class HomeBestAdapter(
    var mContext: Context?,
    commentEntity: MutableList<BaseBestGiftEntity>?,
    var upDade: (str: BaseBestGiftEntity) -> Unit = {}
) : BaseQuickAdapter<BaseBestGiftEntity, BaseViewHolder>(
    R.layout.item_libhome_bestlist,
    commentEntity
) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    private val location: String? = mContext?.let { PreferencesHelper.getLocationName(it) }

    override fun convert(holder: BaseViewHolder, item: BaseBestGiftEntity) {
        item?.run {
           nickName?.let {
               holder.setText(R.id.ivLibHomeName,it)
           }
            liveIn?.let {
                holder.setText(R.id.ivLibHomedIntroduce,it)
            }
            var save: ImageView? = holder.getView(R.id.LibHomeUserSave)

            isConcern?.let {
                if(it){
                    save?.setImageResource(R.mipmap.lib_home_user_saved)
                }else{
                    save?.setImageResource(R.mipmap.lib_home_user_save)
                }
            }
            save?.setOnClickListener {
                if (BaseDateUtils.isFastClick()){
                    upDade(item)
                }
            }

            var one: ShapedImageView? = holder.getView(R.id.LibHomeItemGiftOne)
            var oneLL: LinearLayout? = holder.getView(R.id.LibHomeItemGiftOneLl)
            oneLL?.setOnClickListener {
                item.items[0]?.let {
                    if (BaseDateUtils.isFastClick()){
                        ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                            .withString("id",it.releaseRecordId)
                            .withString("userid",it.userId)
                            .navigation()
                    }
                }

            }
            var two: ShapedImageView? = holder.getView(R.id.LibHomeItemGiftTwo)
            var twoLL: LinearLayout? = holder.getView(R.id.LibHomeItemGiftTwoLl)
            twoLL?.setOnClickListener {
                item.items[1]?.let {
                    if (BaseDateUtils.isFastClick()){
                        ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                            .withString("id",it.releaseRecordId)
                            .withString("userid",it.userId)
                            .navigation()
                    }
                }
            }
            items?.let {
                when(items.size){
                    1 -> {
                        holder.setText(R.id.tvText,it[0].itemTitle)
                        Glide.with(one!!.context)
                            .load(it[0].pictureResources[0])
                            .placeholder(R.mipmap.base_comm_img)
                            .into(one)
                    }
                    2 -> {
                        holder.setText(R.id.tvText,it[0].itemTitle)
                        Glide.with(one!!.context)
                            .load(it[0].pictureResources[0])
                            .placeholder(R.mipmap.base_comm_img)
                            .into(one)
                        holder.setText(R.id.tvTextTwo,it[1].itemTitle)
                        Glide.with(two!!.context)
                            .load(it[1].pictureResources[0])
                            .placeholder(R.mipmap.base_comm_img)
                            .into(two)
                    }
                    else -> {

                    }
                }


            }

        }
        val ivHead: ShapedImageView = holder.getView(R.id.ivlibHomeHeadImage)
        item?.headImgUrl?.let {
            Glide.with(ivHead!!.context)
                .load(it)
                .placeholder(R.mipmap.base_comm_head)
                .into(ivHead)
        }
        ivHead?.setOnClickListener {
            if (BaseDateUtils.isFastClick()){
                ARouter.getInstance().build(Constance.LibMineUserActivityURL)
                    .withString("userId",item.userId)
                    .navigation()
            }
        }
        var duce: TextView? = holder.getView(R.id.ivLibHomedIntroduce)
        val ivduce: ImageView = holder.getView(R.id.LibHomeUserLocationIV)

        if(location.isNullOrEmpty()){
            duce?.visibility= View.INVISIBLE
            ivduce.visibility= View.INVISIBLE
        }else{
            duce?.text=location
            duce?.visibility= View.VISIBLE
            ivduce.visibility= View.VISIBLE
        }

    }


}