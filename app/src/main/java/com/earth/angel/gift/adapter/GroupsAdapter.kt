package com.earth.angel.gift.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.angel.R
import com.earth.angel.util.TimeForUtils
import com.earth.libbase.entity.HouseListEntity
import com.nine.palace.headpic.NinePalaceImageView
import com.nine.palace.headpic.NinePalaceImageViewAdapter


class GroupsAdapter(
    mContext: Context?,
    beans: MutableList<HouseListEntity>?,
    listOldstr: List<HouseListEntity>?,
    var upDade: (houseNumber: Long) -> Unit = {}
) : BaseQuickAdapter<HouseListEntity, BaseViewHolder>(
    R.layout.item_group,
    beans
) {
    var listOldstr: List<HouseListEntity>? = listOldstr
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    fun setList(listOldstr: List<HouseListEntity>?) {
        this.listOldstr = listOldstr
    }

    override fun convert(holder: BaseViewHolder, item: HouseListEntity) {
        var ivHeader: NinePalaceImageView<*> = holder.getView(R.id.iv)
        var ivHead: ImageView = holder.getView(R.id.ivHead)

        var tvmessage: TextView? = holder.getView(R.id.tvmessage)

        var tvName: TextView? = holder.getView(R.id.tv)
        var tvNum: TextView? = holder.getView(R.id.tvNum)

        var tvTime: TextView? = holder.getView(R.id.tvJoined)
        if (listOldstr != null) {
            for (bean in listOldstr!!) {
                if (bean.number == item.number) {
                    var textNum = item.releasesNumber - bean.releasesNumber
                    if (textNum > 0) {
                        tvNum?.visibility = View.VISIBLE
                        tvNum?.text = textNum.toString()
                    } else {
                        tvNum?.visibility = View.GONE
                    }
                    break
                }
            }
        }

        val adapter: NinePalaceImageViewAdapter<*> =
            object : NinePalaceImageViewAdapter<String>() {
                override fun onDisplayImage(context: Context, imageView: ImageView, s: String) {

                    Glide.with(context).load(s).apply(RequestOptions.bitmapTransform(CircleCrop())).into(imageView)
                }

                //重写该方法自定义生成ImageView方式，用于九宫格头像中的一个个图片控件，可以设置ScaleType等属性
                override fun generateImageView(context: Context): ImageView {
                    return super.generateImageView(context)
                }
            }


        item?.imgUrl?.let {
            if (it.isNotEmpty()) {
                if (item.type == 0) {
                    ivHeader.visibility = View.VISIBLE
                    ivHead.visibility = View.INVISIBLE
                    ivHeader.setAdapter(adapter)
                    ivHeader.setImagesData(it)
                } else {
                    ivHeader.visibility = View.INVISIBLE
                    ivHead.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(it[0])
                        .into(ivHead!!)
                }

            }
        }
        item?.name?.let {
            tvName?.text = it
        }
        if (item.type == 0) {
            item?.members?.let {
                tvmessage?.text = "[ $it people]"
            }
        }
        if (item.type == 1) {
            item?.releasesNumber?.let { itNum ->
                if(itNum.toString() == "0"){
                    tvmessage?.text = ""
                }else{
                    tvmessage?.text = "$itNum items"
                }
            }
        }

        item?.releaseTime?.let {
            if( TimeForUtils.getGiftTime(it)=="1970-01-01 08:00"){

                tvTime?.text =""

            }else{
                tvTime?.text =TimeForUtils.getGiftTime(it)
            }
        }
    }


}