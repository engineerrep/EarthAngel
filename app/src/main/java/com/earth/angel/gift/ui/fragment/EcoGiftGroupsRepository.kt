package com.earth.angel.gift.ui.fragment


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.ReleasedMessageRequest
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EcoGiftGroupsRepository(private val api: ApiService) {
    suspend fun recommend(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.recommend(CommonRequest(pageNum, pageSize))
        }
    suspend fun recommendMain(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.recommendMain(CommonRequest(pageNum, pageSize))
        }
    suspend fun joinGroup(houseNumber: Long) =
        withContext(Dispatchers.IO) {
            api.joinGroup(JoinRequest(houseNumber))
        }

    suspend fun creatGroup(houseName: String?, houseLogo: String?, userIds: List<Long>?) =
        withContext(Dispatchers.IO) {
            api.creatGroup(CreatGroupRequest(houseName, houseLogo, userIds))
        }

    suspend fun invite(houseNum: Long?, userIds: List<Long>?) =
        withContext(Dispatchers.IO) {
            api.invite(InviteGroupRequest(houseNum, userIds))
        }

    suspend fun getJoinGroup(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.getJoinGroup(CommonRequest(pageNum, pageSize))
        }

    suspend fun giftHouseUser(houseNumber: Long, pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.giftHouseUser(GroupMemberRequest(houseNumber, pageNum, pageSize))
        }

    suspend fun pageList(
        houseNumber: Long?,
        pageNum: Int?,
        pageSize: Int?,
        isSelf: Boolean?,
        releaseTypes: List<Int>?,
        sortField: String?,
        sortType: Int?
    ) =
        withContext(Dispatchers.IO) {
            api.pageList(
                PageGiftListRequest(
                    houseNumber,
                    pageNum,
                    pageSize,
                    isSelf,
                    releaseTypes,
                    sortField,
                    sortType
                )
            )
        }

    suspend fun goodsRelation(relationStatus: Int, releaseRecordId: Long) =
        withContext(Dispatchers.IO) {
            api.goodsRelation(GoodRelationRequest(relationStatus, releaseRecordId))
        }

    suspend fun deleteGoodsRelation(relationStatus: Int?, releaseRecordId: Long?) =
        withContext(Dispatchers.IO) {
            api.deleteGoodsRelation(GoodRelationRequest(relationStatus, releaseRecordId))
        }

    suspend fun messageRecord(messageRecord: ReleasedMessageRequest) =
        withContext(Dispatchers.IO) {
            api.releasedMessageRecord(messageRecord)
        }

    suspend fun quitGroup(houseNumber: Long) =
        withContext(Dispatchers.IO) {
            api.quitGroup(QuitGroupRequest(houseNumber = houseNumber))
        }

    suspend fun updateGroup(houseNumber: Long, houseName: String) =
        withContext(Dispatchers.IO) {
            api.updateGroup(QuitGroupRequest(houseNumber = houseNumber, houseName = houseName))
        }

    suspend fun selectUser(houseNumber: Long, pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.selectUserGift(SelectUserRequest(houseNumber, pageNum, pageSize))
        }

    suspend fun greenSlogan() =
        withContext(Dispatchers.IO) {
            api.greenSlogan()
        }

    suspend fun indexPageList(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.indexPageList(CommonRequest(pageNum, pageSize))
        }

    suspend fun commentPageList(pageNum: Int, pageSize: Int, id: Long) =
        withContext(Dispatchers.IO) {
            api.commentPageList(CommentRequest(pageNum, pageSize, id))
        }

    suspend fun addComment(commentUserMsg: String, releaseRecordId: Long) =
        withContext(Dispatchers.IO) {
            api.addComment(CommentAddRequest(commentUserMsg, releaseRecordId))
        }

    suspend fun deleteReleaseRecord(id: Long) =
        withContext(Dispatchers.IO) {
            api.deleteReleaseRecord(RecordOneRequest(id))
        }

    suspend fun reshelfReleaseRecord(id: Long) =
        withContext(Dispatchers.IO) {
            api.reshelfReleaseRecord(RecordOneRequest(id))
        }

    suspend fun likeFrends(pageNum: Int, pageSize: Int, id: Long) =
        withContext(Dispatchers.IO) {
            api.likeFrends(CommentRequest(pageNum, pageSize, id))
        }

    suspend fun sendGift(exchangePersonId: Long,id: Long) =
        withContext(Dispatchers.IO) {
            api.sendGift(SendGiftRequest(exchangePersonId, id))
        }
    suspend fun checkSendGift(id: Long) =
        withContext(Dispatchers.IO) {
            api.checkSendGift(SendGiftRequest(id = id))
        }
    suspend fun shoppingAddressIndex() =
        withContext(Dispatchers.IO) {
            api.shoppingAddressIndex()
        }
}