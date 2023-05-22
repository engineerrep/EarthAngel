package com.earth.libarticl.adapter

import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libarticl.R
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.view.ShapedImageView


class ArticleAdapter(val mContext: Context, list: MutableList<ArticleEntity>) :
    BaseQuickAdapter<ArticleEntity, BaseViewHolder>(
        R.layout.item_article, list
    ) {

    override fun convert(holder: BaseViewHolder, item: ArticleEntity) {
        var itemArticleIv: ShapedImageView = holder.getView(R.id.itemArticleIv)
        var itemArticleDetTv: TextView = holder.getView(R.id.itemArticleDetTv)


        item.title?.let {

            itemArticleDetTv.text=it
        }
        holder.setText(R.id.itemArticleTv, BaseDateUtils.getMonthDayTime(item.createTime))
        item?.coverPicture?.let {
            Glide.with(mContext)
                .load(it)
                .into(itemArticleIv)
        }


    }


}