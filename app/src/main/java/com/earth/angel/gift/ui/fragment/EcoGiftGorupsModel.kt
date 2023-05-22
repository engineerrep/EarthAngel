package com.earth.angel.gift.ui.fragment

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.ReleasedMessageRequest
import kotlinx.coroutines.flow.flow

class EcoGiftGorupsModel(private val repository: EcoGiftGroupsRepository): ViewModel() {

    fun recommend(pageNum: Int, pageSize: Int) = flow {
        emit(repository.recommend(pageNum,pageSize))
    }
    fun recommendMain(pageNum: Int, pageSize: Int) = flow {
        emit(repository.recommendMain(pageNum,pageSize))
    }
    fun joinGroup(houseNumber: Long) = flow {
        emit(repository.joinGroup(houseNumber))
    }
    fun creatGroup(houseName: String?,houseLogo: String?,userIds: List<Long>?) = flow {
        emit(repository.creatGroup(houseName,houseLogo,userIds))
    }
    fun invite(houseNum: Long?,userIds: List<Long>?) = flow {
        emit(repository.invite(houseNum,userIds))
    }
    fun getJoinGroup(pageNum: Int, pageSize: Int) = flow {
        emit(repository.getJoinGroup(pageNum,pageSize))
    }
    fun giftHouseUser(houseNumber: Long,pageNum: Int, pageSize: Int) = flow {
        emit(repository.giftHouseUser(houseNumber,pageNum,pageSize))
    }
    fun pageList(houseNumber: Long?,pageNum: Int?, pageSize: Int?,isSelf: Boolean?,releaseTypes: List<Int>?,sortField: String?,sortType : Int?) = flow {
        emit(repository.pageList(houseNumber,pageNum,pageSize,isSelf,releaseTypes,sortField,sortType))
    }
    fun goodsRelation(relationStatus: Int, releaseRecordId: Long) = flow {
        emit(repository.goodsRelation(relationStatus,releaseRecordId))
    }
    fun deleteGoodsRelation(relationStatus: Int?, releaseRecordId: Long?) = flow {
        emit(repository.deleteGoodsRelation(relationStatus,releaseRecordId))
    }
    fun messageRecord(messageRecord: ReleasedMessageRequest) = flow {
        emit(repository.messageRecord(messageRecord))
    }
    fun quitGroup(houseNumber: Long) = flow {
        emit(repository.quitGroup(houseNumber))
    }
    fun updateGroup(houseNumber: Long,houseName: String)= flow {
        emit(repository.updateGroup(houseNumber,houseName))
    }
    fun selectUser(houseNumber: Long,pageNum: Int, pageSize: Int) = flow {
        emit(repository.selectUser(houseNumber,pageNum,pageSize))
    }

    fun greenSlogan() = flow {
        emit(repository.greenSlogan())
    }

    fun indexPageList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.indexPageList(pageNum,pageSize))
    }

    fun commentPageList(pageNum: Int, pageSize: Int,id: Long) = flow {
        emit(repository.commentPageList(pageNum,pageSize,id))
    }

    fun addComment(commentUserMsg: String, releaseRecordId: Long) = flow {
        emit(repository.addComment(commentUserMsg, releaseRecordId))
    }

    fun deleteReleaseRecord(id: Long) = flow {
        emit(repository.deleteReleaseRecord(id))
    }
    fun reshelfReleaseRecord(id: Long) = flow {
        emit(repository.reshelfReleaseRecord(id))
    }

    fun likeFrends(pageNum: Int, pageSize: Int,id: Long) = flow {
        emit(repository.likeFrends(pageNum,pageSize,id))
    }

    fun sendGift(exchangePersonId: Long,id: Long) = flow {
        emit(repository.sendGift(exchangePersonId, id))
    }
    fun checkSendGift(id: Long) = flow {
        emit(repository.checkSendGift(id = id))
    }
    fun shoppingAddressIndex() = flow {
        emit(repository.shoppingAddressIndex())
    }
}