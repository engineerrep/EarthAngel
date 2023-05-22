package com.earth.libhome.ui

import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeRepository (private val api: ApiService) {

    suspend fun indexMain() =
        withContext(Dispatchers.IO) {
            api.indexMain()
        }

    suspend fun newGifts(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.newGifts(bean)
        }

    suspend fun nearbyItemGifts(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.nearbyItemGifts(bean)
        }
    suspend fun updateUser(mUserUpdateRequest: UserUpdateRequest) =
        withContext(Dispatchers.IO) {
            api.updateUser(mUserUpdateRequest)
        }
    suspend fun userPageGiftList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userPageGiftList(bean)
        }

    suspend fun statusPageList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.statusPageList(bean)
        }
    suspend fun concernedAdd(bean: BaseUserRequest) =
        withContext(Dispatchers.IO) {
            api.concernedAdd(bean)
        }
    suspend fun concernedDelete(bean: BaseUserRequest) =
        withContext(Dispatchers.IO) {
            api.concernedDelete(bean)
        }
    suspend fun releaseGistOne(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.releaseGistOne(bean)
        }
    suspend fun userSelectOne(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userSelectOne(bean)
        }
    suspend fun goodsRelationAdd(bean: GoodsRelationRequest) =
        withContext(Dispatchers.IO) {
            api.goodsRelationAdd(bean)
        }
    suspend fun goodsRelationDelete(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.goodsRelationDelete(bean)
        }
    suspend fun poketAdd( bean: AddGiftRequest) =
        withContext(Dispatchers.IO) {
            api.poketAdd(bean)
        }
    suspend fun giftUnload( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.giftUnload(bean)
        }
    suspend fun giftRelist( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.giftRelist(bean)
        }
    suspend fun giftDelete( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.giftDelete(bean)
        }
    suspend fun recordMessageAdd( bean: RecordMessageAddRequest) =
        withContext(Dispatchers.IO) {
            api.recordMessageAdd(bean)
        }
    suspend fun recordMessagePageList( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.recordMessagePageList(bean)
        }
    suspend fun recordMessageDelete( bean: MessageRequest) =
        withContext(Dispatchers.IO) {
            api.recordMessageDelete(bean)
        }
    suspend fun reportMessage( bean: MessageRequest) =
        withContext(Dispatchers.IO) {
            api.reportMessage(bean)
        }
    suspend fun pageArticlesList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.pageArticlesList(bean)
        }
    suspend fun myScore() =
        withContext(Dispatchers.IO) {
            api.myScore()
        }
    suspend fun shareIn() =
        withContext(Dispatchers.IO) {
            api.shareIn()
        }
}