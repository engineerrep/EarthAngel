package com.earth.libhome.ui

import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.earth.libbase.base.BaseViewHolder
import com.earth.libbase.base.LibBasePagingDataAdapter
import com.earth.libhome.R
import com.earth.libhome.databinding.ItemHomeBinding

/*class HomePagingSource(): PagingSource<Int, String>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        val page = params.key ?: 0
         return try {
             val popularityData = arrayListOf("1","2","3")
         } catch (e: Exception) {
             LoadResult.Error(e)
         }
    }

}*/


class HomePagingAdapter : LibBasePagingDataAdapter<String , ItemHomeBinding>(DIFF_CALLBACK){

    override fun getLayoutId(): Int = R.layout.item_home

    override fun setVariable(data: String, position: Int, holder: BaseViewHolder<ItemHomeBinding>) {

    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean =
                oldItem == newItem
        }
    }
}