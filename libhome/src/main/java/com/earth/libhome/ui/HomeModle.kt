package com.earth.libhome.ui

import androidx.lifecycle.ViewModel
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class HomeModle (private val repository: HomeRepository): ViewModel() {

    fun indexMain() = flow {
        emit(repository.indexMain())
    }
    fun newGifts(bean: CommonRequest) = flow {
        emit(repository.newGifts(bean))
    }
    fun nearbyItemGifts(bean: CommonRequest) = flow {
        emit(repository.nearbyItemGifts(bean))
    }
    fun updateUser(mUserUpdateRequest: UserUpdateRequest) = flow {
        emit(repository.updateUser(mUserUpdateRequest))
    }
    fun userPageGiftList(bean: CommonRequest) = flow {
        emit(repository.userPageGiftList(bean))
    }
    fun statusPageList(bean: CommonRequest) = flow {
        emit(repository.statusPageList(bean))
    }
    fun concernedAdd(bean: BaseUserRequest) = flow {
        emit(repository.concernedAdd(bean))
    }
    fun concernedDelete(bean: BaseUserRequest) = flow {
        emit(repository.concernedDelete(bean))
    }
    fun releaseGistOne(bean: CommentRequest) = flow {
        emit(repository.releaseGistOne(bean))
    }
    fun userSelectOne(bean: CommonRequest) = flow {
        emit(repository.userSelectOne(bean))
    }
    fun goodsRelationAdd(bean: GoodsRelationRequest) = flow {
        emit(repository.goodsRelationAdd(bean))
    }
    fun goodsRelationDelete(bean: CommentRequest) = flow {
        emit(repository.goodsRelationDelete(bean))
    }
    fun poketAdd(bean: AddGiftRequest) = flow {
        emit(repository.poketAdd(bean))
    }
    fun giftUnload(bean: CommentRequest) = flow {
        emit(repository.giftUnload(bean))
    }
    fun giftRelist(bean: CommentRequest) = flow {
        emit(repository.giftRelist(bean))
    }
    fun giftDelete(bean: CommentRequest) = flow {
        emit(repository.giftDelete(bean))
    }
    fun recordMessageAdd(bean: RecordMessageAddRequest) = flow {
        emit(repository.recordMessageAdd(bean))
    }
    fun recordMessagePageList(bean: CommentRequest) = flow {
        emit(repository.recordMessagePageList(bean))
    }
    fun recordMessageDelete(bean: MessageRequest) = flow {
        emit(repository.recordMessageDelete(bean))
    }
    fun reportMessage(bean: MessageRequest) = flow {
        emit(repository.reportMessage(bean))
    }
    fun pageArticlesList(bean: CommentRequest) = flow {
        emit(repository.pageArticlesList(bean))
    }
    fun myScore() = flow {
        emit(repository.myScore())
    }
    fun shareIn() = flow {
        emit(repository.shareIn())
    }
}