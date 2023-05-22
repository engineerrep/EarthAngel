package com.earth.libarticl.adapter

import android.content.Context
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.earth.libarticl.R
import com.earth.libbase.entity.ArticleEntity
import com.earth.libbase.entity.ArticleMainEntity
import com.earth.libbase.view.ShapedImageView


class ArticleMainAdapter(val mContext: Context, list: MutableList<ArticleMainEntity>) :
    BaseQuickAdapter<ArticleMainEntity, BaseViewHolder>(
        R.layout.item_main_article, list
    ) {

    override fun convert(holder: BaseViewHolder, item: ArticleMainEntity) {
        holder.setText(R.id.itemMainTv,(holder.layoutPosition).toString())
        holder.setText(R.id.itemMainTitle,item.title)
        holder.setText(R.id.itemMainContent,item.content)

    }


}