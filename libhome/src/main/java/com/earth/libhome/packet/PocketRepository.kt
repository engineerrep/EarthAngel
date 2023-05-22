package com.earth.libhome.packet

import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PocketRepository (private val api: ApiService) {


    suspend fun poketBagPageList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.poketBagPageList(bean)
        }
    suspend fun poketBagdelete(bean: PocketDeleteRequest) =
        withContext(Dispatchers.IO) {
            api.poketBagdelete(bean)
        }
    suspend fun userSelectOne(bean: CommonRequest) =
        withContext(Dispatchers.IO) {
            api.userSelectOne(bean)
        }
}