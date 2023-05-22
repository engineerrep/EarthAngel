package com.earth.libhome.adapter

import android.content.Context
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.view.ShapedImageView
import com.earth.libhome.R


class ArticleHomeAdapter(val mContext: Context, list: MutableList<ArticleEntity>) :
    BaseQuickAdapter<ArticleEntity, BaseViewHolder>(
        R.layout.item_home_article, list
    ) {

    override fun convert(holder: BaseViewHolder, item: ArticleEntity) {
        var itemArticleIv: ShapedImageView = holder.getView(R.id.itemArticleIv)



        item.coverPicture?.let {
            Glide.with(mContext)
                .load(it)
                .into(itemArticleIv)
        }


    }


}