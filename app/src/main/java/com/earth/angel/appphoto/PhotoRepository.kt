package com.earth.angel.appphoto


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.baserequest.BasePhotoRequest
import com.earth.libbase.network.request.BaseFileRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.ReleaseRecordRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class PhotoRepository(private val api: ApiService) {
    suspend fun sortLabel() =
        withContext(Dispatchers.IO) {
            api.sortLabel()
        }

    suspend fun releaseRecord(bean: ReleaseRecordRequest) =
        withContext(Dispatchers.IO) {
            api.releaseRecord(bean)
        }

    suspend fun releaseRecordUpdate(bean: BasePhotoRequest) =
        withContext(Dispatchers.IO) {
            api.releaseRecordUpdate(bean)
        }
    suspend fun releaseRecordAdd(bean: BasePhotoRequest) =
        withContext(Dispatchers.IO) {
            api.releaseRecordAdd(bean)
        }
    suspend fun releaseGistOne(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.releaseGistOne(bean)
        }
    suspend fun picByIo(bean: BaseFileRequest) =
        withContext(Dispatchers.IO) {
            api.picByIo(bean)
        }
    suspend fun categoryList() =
        withContext(Dispatchers.IO) {
            api.categoryList()
        }
    suspend fun communityList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.communityList(bean)
        }

    suspend fun upload(list: List<MultipartBody.Part>) =
        withContext(Dispatchers.IO) {
            api.upload(list)
        }
}