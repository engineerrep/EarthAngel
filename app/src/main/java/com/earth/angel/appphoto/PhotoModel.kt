package com.earth.angel.appphoto

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.baserequest.BasePhotoRequest
import com.earth.libbase.network.request.BaseFileRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.ReleaseRecordRequest
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

class PhotoModel(private val repository: PhotoRepository): ViewModel() {



    fun sortLabel() = flow {
        emit(repository.sortLabel())
    }
    fun releaseRecord(bean: ReleaseRecordRequest) = flow {
        emit(repository.releaseRecord(bean))
    }
    fun releaseRecordUpdate(bean: BasePhotoRequest) = flow {
        emit(repository.releaseRecordUpdate(bean))
    }


    fun releaseRecordAdd(bean: BasePhotoRequest) = flow {
        emit(repository.releaseRecordAdd(bean))
    }
    fun releaseGistOne(bean: CommentRequest) = flow {
        emit(repository.releaseGistOne(bean))
    }
    fun picByIo(bean: BaseFileRequest) = flow {
        emit(repository.picByIo(bean))
    }
    fun categoryList() = flow {
        emit(repository.categoryList())
    }
    fun communityList(bean: CommentRequest) = flow {
        emit(repository.communityList(bean))
    }
    fun upload(list: List<MultipartBody.Part>) = flow {
        emit(repository.upload(list))
    }
}