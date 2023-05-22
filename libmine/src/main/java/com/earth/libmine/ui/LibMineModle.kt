package com.earth.libmine.ui

import androidx.lifecycle.ViewModel
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.request.*
import kotlinx.coroutines.flow.flow

class LibMineModle (private val repository: LibMineRepository): ViewModel()  {
    fun userIndex(bean: CommonRequest) = flow {
        emit(repository.userIndex(bean))
    }
    fun deleteAccount() = flow {
        emit(repository.deleteAccount())
    }
    fun poketAdd(bean: AddGiftRequest) = flow {
        emit(repository.poketAdd(bean))
    }
    fun poketDelete(bean: PocketDeleteRequest) = flow {
        emit(repository.poketDelete(bean))
    }
    fun concernedAdd(bean: BaseUserRequest) = flow {
        emit(repository.concernedAdd(bean))
    }
    fun concernedDelete(bean: BaseUserRequest) = flow {
        emit(repository.concernedDelete(bean))
    }
    fun selfPokedPageList(bean: CommonRequest) = flow {
        emit(repository.selfPokedPageList(bean))
    }
    fun goodsRelationpageList(bean: CommentRequest) = flow {
        emit(repository.goodsRelationpageList(bean))
    }
    fun concernedpageList(bean: CommentRequest) = flow {
        emit(repository.concernedpageList(bean))
    }
    fun updateUser(mUserUpdateRequest: UserUpdateRequest) = flow {
        emit(repository.updateUser(mUserUpdateRequest))
    }
    fun changePassword(mUserUpdateRequest: ChangePasswordRequest) = flow {
        emit(repository.changePassword(mUserUpdateRequest))
    }
    fun shoppingAddressIndex() = flow {
        emit(repository.shoppingAddressIndex())
    }
    fun shoppingAddressAdd(bean: ShipAddressRequest) = flow {
        emit(repository.shoppingAddressAdd(bean))
    }
    fun shoppingAddressUpdate(bean: ShipAddressRequest) = flow {
        emit(repository.shoppingAddressUpdate(bean))
    }
    fun goodsRelationAdd(bean: GoodsRelationRequest) = flow {
        emit(repository.goodsRelationAdd(bean))
    }
    fun goodsRelationDelete(bean: CommentRequest) = flow {
        emit(repository.goodsRelationDelete(bean))
    }
    fun userPageGiftList(bean: CommonRequest) = flow {
        emit(repository.userPageGiftList(bean))
    }
    fun statusPageList(bean: CommonRequest) = flow {
        emit(repository.statusPageList(bean))
    }
    fun userSelectOne(bean: CommonRequest) = flow {
        emit(repository.userSelectOne(bean))
    }
    fun categoryList() = flow {
        emit(repository.categoryList())
    }


    fun itemClassificationPageList(bean: CommentRequest) = flow {
        emit(repository.itemClassificationPageList(bean))
    }

    fun myScore() = flow {
        emit(repository.myScore())
    }
    fun myScorePageList(bean: CommentRequest) = flow {
        emit(repository.myScorePageList(bean))
    }
    fun myTradingRecord(bean: CommentRequest) = flow {
        emit(repository.myTradingRecord(bean))
    }

    fun myTradingRecordDelete(bean: FriendUserRequest) = flow {
        emit(repository.myTradingRecordDelete(bean))
    }
    fun shareIn() = flow {
        emit(repository.shareIn())
    }
}