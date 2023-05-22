package com.earth.libmine.ui

import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LibMineRepository (private val api: ApiService)  {
    suspend fun userIndex( bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userIndex(bean)
        }
    suspend fun deleteAccount() =
        withContext(Dispatchers.IO) {
            api.deleteAccount()
        }
    suspend fun poketAdd( bean: AddGiftRequest) =
        withContext(Dispatchers.IO) {
            api.poketAdd(bean)
        }
    suspend fun poketDelete( bean: PocketDeleteRequest) =
        withContext(Dispatchers.IO) {
            api.poketBagdelete(bean)
        }
    suspend fun concernedAdd(bean: BaseUserRequest) =
        withContext(Dispatchers.IO) {
            api.concernedAdd(bean)
        }
    suspend fun concernedDelete(bean: BaseUserRequest) =
        withContext(Dispatchers.IO) {
            api.concernedDelete(bean)
        }
    suspend fun selfPokedPageList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.selfPokedPageList(bean)
        }
    suspend fun goodsRelationpageList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.goodsRelationpageList(bean)
        }
    suspend fun concernedpageList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.concernedpageList(bean)
        }
    suspend fun updateUser(mUserUpdateRequest: UserUpdateRequest) =
        withContext(Dispatchers.IO) {
            api.updateUser(mUserUpdateRequest)
        }
    suspend fun changePassword(mUserUpdateRequest: ChangePasswordRequest) =
        withContext(Dispatchers.IO) {
            api.changePassword(mUserUpdateRequest)
        }

    suspend fun shoppingAddressIndex() =
        withContext(Dispatchers.IO) {
            api.shoppingAddressIndex()
        }
    suspend fun shoppingAddressAdd(bean: ShipAddressRequest) =
        withContext(Dispatchers.IO) {
            api.shoppingAddressAdd(bean)
        }
    suspend fun shoppingAddressUpdate(bean: ShipAddressRequest) =
        withContext(Dispatchers.IO) {
            api.shoppingAddressUpdate(bean)
        }
    suspend fun goodsRelationAdd(bean: GoodsRelationRequest) =
        withContext(Dispatchers.IO) {
            api.goodsRelationAdd(bean)
        }
    suspend fun goodsRelationDelete(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.goodsRelationDelete(bean)
        }
    suspend fun userPageGiftList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userPageGiftList(bean)
        }
    suspend fun statusPageList(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.statusPageList(bean)
        }
    suspend fun userSelectOne(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userSelectOne(bean)
        }

    suspend fun categoryList() =
        withContext(Dispatchers.IO) {
            api.categoryList()
        }
    suspend fun itemClassificationPageList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.itemClassificationPageList(bean)
        }
    suspend fun myScore() =
        withContext(Dispatchers.IO) {
            api.myScore()
        }
    suspend fun myScorePageList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.myScorePageList(bean)
        }
    suspend fun myTradingRecord(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.myTradingRecord(bean)
        }
    suspend fun myTradingRecordDelete(bean: FriendUserRequest) =
        withContext(Dispatchers.IO) {
            api.myTradingRecordDelete(bean)
        }
    suspend fun shareIn() =
        withContext(Dispatchers.IO) {
            api.shareIn()
        }
}