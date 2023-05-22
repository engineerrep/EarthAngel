package com.earth.angel.chat


import com.earth.angel.base.PAGING_PAGER_SIZE
import com.earth.libbase.baseentity.BaseGiftEntity
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.network.request.RecordDoneRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ContactListRepository(val api: ApiService) {

    suspend fun loadPopularityData(page: Int): MutableList<BaseGiftEntity>? =
        withContext(Dispatchers.IO) {
            api.selfPokedPageList(CommonRequest(page + 1, PAGING_PAGER_SIZE,ChatListActivity.mUserid?.toLong())).data?.poketList
        }
    suspend fun loadMainListData(page: Int): MutableList<GiftEntity>? =
        withContext(Dispatchers.IO) {
            api.recommendMain(CommonRequest(page + 1, PAGING_PAGER_SIZE,ChatListActivity.mUserid?.toLong())).data
        }

    suspend fun userPageList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.ownPageList(CommonRequest(pageNum, pageSize,ChatListActivity.mUserid?.toLong()))
        }
    suspend fun selfPokedPageList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.selfPokedPageList(bean)
        }
    suspend fun otherPageList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.otherPageList(bean)
        }
    suspend fun releaseRecordDone(bean: RecordDoneRequest) =
        withContext(Dispatchers.IO) {
            api.releaseRecordDone(bean)
        }
    suspend fun deleteCounterparty(bean: PocketDeleteRequest) =
        withContext(Dispatchers.IO) {
            api.deleteCounterparty(bean)
        }
    suspend fun releaseGistOne(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.releaseGistOne(bean)
        }
    suspend fun poketBagdelete(bean: PocketDeleteRequest) =
        withContext(Dispatchers.IO) {
            api.poketBagdelete(bean)
        }
    suspend fun userSelectOne(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userSelectOne(bean)
        }
}