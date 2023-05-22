package com.earth.angel.mine


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MineRepository(private val api: ApiService) {
    suspend fun ownPageList(pageNum: Int, pageSize: Int,userid: Long?) =
        withContext(Dispatchers.IO) {
            api.ownPageList(CommonRequest(pageNum, pageSize,userid))
        }
    suspend fun ownSharePageList(pageNum: Int, pageSize: Int,userid: Long?) =
        withContext(Dispatchers.IO) {
            api.ownPageList(CommonRequest(pageNum, pageSize,userid,0))
        }
    suspend fun tradablePageList(pageNum: Int, pageSize: Int,userid: Long?) =
        withContext(Dispatchers.IO) {
            api.tradablePageList(CommonRequest(pageNum, pageSize,userid,0))
        }
    suspend fun userPageList(pageNum: Int, pageSize: Int,userid: Long?) =
        withContext(Dispatchers.IO) {
            api.userPageList(CommonRequest(pageNum, pageSize,userid))
        }
    suspend fun unreadPageList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.unreadPageList(CommonRequest(pageNum, pageSize))
        }
    suspend fun unreadSize() =
        withContext(Dispatchers.IO) {
            api.unreadSize()
        }
    suspend fun readPageList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.readPageList(CommonRequest(pageNum, pageSize))
        }
    suspend fun unload(id: Long) =
        withContext(Dispatchers.IO) {
            api.unload(UnloadRequest(id))
        }

    suspend fun selfPageList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.selfPageList(CommonRequest(pageNum, pageSize))
        }
    suspend fun selfPageListV2(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.selfPageListV2(CommonRequest(pageNum, pageSize))
        }
    suspend fun selUnfPageListV2(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.selfUnPageListV2(CommonRequest(pageNum, pageSize))
        }
    suspend fun likeList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.likeList(CommonRequest(pageNum, pageSize))
        }

    suspend fun selectOne(userid: Long?) =
        withContext(Dispatchers.IO) {
            api.selectOne(GiftHouseSearchRequest(userId = userid))
        }
    suspend fun selectMine() =
        withContext(Dispatchers.IO) {
            api.selectMine(GiftHouseSearchRequest())
        }
    suspend fun userSelectOne(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userSelectOne(bean)
        }
    suspend fun updateOne(headImgUrl: String, nickName: String) =
        withContext(Dispatchers.IO) {
            api.updateOne(UpdateUserRequest(headImgUrl,nickName))
        }
    suspend fun releaseRecordOne(id: Long) =
        withContext(Dispatchers.IO) {
            api.releaseRecordOne(RecordOneRequest(id))
        }
    suspend fun updateUser(mUserUpdateRequest: UserUpdateRequest) =
        withContext(Dispatchers.IO) {
            api.updateUser(mUserUpdateRequest)
        }
    suspend fun signIn() =
        withContext(Dispatchers.IO) {
            api.signIn()
        }
    suspend fun joinCommunity( bean: GroupRequest) =
        withContext(Dispatchers.IO) {
            api.joinCommunity(bean)
        }
}