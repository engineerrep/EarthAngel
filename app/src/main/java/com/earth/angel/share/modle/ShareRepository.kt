package com.earth.angel.share.modle


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.ShareRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShareRepository(private val api: ApiService) {

    suspend fun shareVersionAdd(shareRequest : ShareRequest) =
        withContext(Dispatchers.IO) {
            api.shareVersionAdd(shareRequest)
        }
    suspend fun shareVersionUpdate(shareRequest : ShareRequest) =
        withContext(Dispatchers.IO) {
            api.shareVersionUpdate(shareRequest)
        }
}