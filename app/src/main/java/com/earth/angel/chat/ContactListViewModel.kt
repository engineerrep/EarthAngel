package com.earth.angel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.earth.angel.base.constPagerConfig
import com.earth.angel.gift.adapter.MainListPagingSource
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.PocketDeleteRequest
import com.earth.libbase.network.request.RecordDoneRequest
import kotlinx.coroutines.flow.flow


class ContactListViewModel(
    private val repository: ContactListRepository
) : ViewModel() {

    fun loadPopularityData() = Pager(constPagerConfig) {
        ContactListPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun loadMainListData() = Pager(constPagerConfig) {
        MainListPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun userPageList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.userPageList(pageNum,pageSize))
    }
    fun selfPokedPageList(bean: CommonRequest) = flow {
        emit(repository.selfPokedPageList(bean))
    }
    fun otherPageList(bean: CommonRequest) = flow {
        emit(repository.otherPageList(bean))
    }
    fun releaseRecordDone(bean: RecordDoneRequest) = flow {
        emit(repository.releaseRecordDone(bean))
    }
    fun deleteCounterparty(bean: PocketDeleteRequest) = flow {
        emit(repository.deleteCounterparty(bean))
    }
    fun releaseGistOne(bean: CommentRequest) = flow {
        emit(repository.releaseGistOne(bean))
    }
    fun poketBagdelete(bean: PocketDeleteRequest) = flow {
        emit(repository.poketBagdelete(bean))
    }
    fun userSelectOne(bean: CommonRequest) = flow {
        emit(repository.userSelectOne(bean))
    }
}

