package com.earth.angel.share.modle

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.ShareRequest
import kotlinx.coroutines.flow.flow

class ShareModel(private val repository: ShareRepository): ViewModel() {

    fun shareVersionAdd(shareRequest : ShareRequest) = flow {
        emit(repository.shareVersionAdd(shareRequest))
    }
    fun shareVersionUpdate(shareRequest : ShareRequest) = flow {
        emit(repository.shareVersionUpdate(shareRequest))
    }
}