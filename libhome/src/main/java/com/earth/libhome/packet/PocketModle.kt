package com.earth.libhome.packet

import androidx.lifecycle.ViewModel
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.request.*
import kotlinx.coroutines.flow.flow

class PocketModle (private val repository: PocketRepository): ViewModel() {

    fun poketBagPageList(bean: CommentRequest) = flow {
        emit(repository.poketBagPageList(bean))
    }
    fun poketBagdelete(bean: PocketDeleteRequest) = flow {
        emit(repository.poketBagdelete(bean))
    }
    fun userSelectOne(bean: CommonRequest) = flow {
        emit(repository.userSelectOne(bean))
    }
}