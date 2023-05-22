package com.earth.angel.gift.adapter

import android.content.Intent
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.BasePagingDataAdapter
import com.earth.angel.base.BaseViewHolder
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.chat.ContactListRepository
import com.earth.angel.databinding.ItemRecommendMainBinding
import com.earth.angel.gift.ui.GiftDetailsActivity
import com.earth.libbase.entity.GiftEntity


class MainListPagingSource(
    private val repository: ContactListRepository
) : PagingSource<Int, GiftEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GiftEntity> {
        val page = params.key ?: 0
        return try {
            val popularityData =
                repository.loadMainListData( page)
                    ?: mutableListOf()
            LoadResult.Page(
                data = popularityData,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (popularityData.isEmpty()) {
                    null
                } else {
                    if (page == 0) {
                       // CoogerApp.userid.clear()
                    }
                   // EventBus.getDefault().post(ChatFourUserEvent(true))
                    page + 1
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}





class MainListPagingAdapter :
    BasePagingDataAdapter<GiftEntity, ItemRecommendMainBinding>(DIFF_CALLBACK) {

    override fun getLayoutId() = R.layout.item_recommend_main

    override fun setVariable(
        data: GiftEntity, position: Int, holder: BaseViewHolder<ItemRecommendMainBinding>
    ) {
        holder.binding.data = data

        var iv=holder.binding.testCustomMessageIv
        var tv=holder.binding.testCustomMessageTv
        var chatClBg=holder.binding.chatClBg
        chatClBg.setOnClickListener {

            val intent = Intent(EarthAngelApp.instance, GiftDetailsActivity::class.java)
                .putExtra("giftEntity", data)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            EarthAngelApp.instance?.startActivity(intent)
        }
        data?.pictureResources?.let {
            if (!it[0].pictureUrl.isNullOrEmpty()) {
                Glide.with(iv.context)
                    .load(it[0].pictureUrl)
                    .into(iv)
            }
        }

        data?.description?.let {
            tv?.text = it
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GiftEntity>() {
            override fun areItemsTheSame(
                oldItem: GiftEntity,
                newItem: GiftEntity
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: GiftEntity,
                newItem: GiftEntity
            ): Boolean =
                oldItem == newItem
        }
    }
}