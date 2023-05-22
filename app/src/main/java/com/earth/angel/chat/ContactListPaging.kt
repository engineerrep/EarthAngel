package com.earth.angel.chat

import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.BasePagingDataAdapter
import com.earth.angel.base.BaseViewHolder
import com.earth.angel.databinding.ItemChatGiftBinding
import com.earth.libbase.baseentity.BaseGiftEntity


class ContactListPagingSource(
    private val repository: ContactListRepository
) : PagingSource<Int, BaseGiftEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BaseGiftEntity> {
        val page = params.key ?: 0
        return try {
            val popularityData =
                repository.loadPopularityData( page)
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





class ChatPopularityPagingAdapter :
    BasePagingDataAdapter<BaseGiftEntity, ItemChatGiftBinding>(DIFF_CALLBACK) {

    override fun getLayoutId() = R.layout.item_chat_gift

    override fun setVariable(
        data: BaseGiftEntity, position: Int, holder: BaseViewHolder<ItemChatGiftBinding>
    ) {
        holder.binding.data = data
        var  hul=data.pictureResources[0]
        var iv=holder.binding.sivHeadimg



        Glide.with(iv.context).load(hul).into(iv!!)
   /*     rlbottom.setOnClickListener {
            var bean=CustomHelloMessage()
            bean.version = TUIKitConstants.version
            bean.text=data.description
            bean.link=hul
            bean.customCellType="0"
            bean.identificationID=data.id
            EventBus.getDefault().post(bean)
        }*/
     /*   Glide.with(iv.context)
            .load(hul)
            .placeholder(R.mipmap.head_common)
            .into(iv)*/

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BaseGiftEntity>() {
            override fun areItemsTheSame(
                oldItem: BaseGiftEntity,
                newItem: BaseGiftEntity
            ): Boolean =
                oldItem.releaseRecordId == newItem.releaseRecordId

            override fun areContentsTheSame(
                oldItem: BaseGiftEntity,
                newItem: BaseGiftEntity
            ): Boolean =
                oldItem == newItem
        }
    }
}