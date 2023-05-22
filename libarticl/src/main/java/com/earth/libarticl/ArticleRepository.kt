package com.earth.libarticl

import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(private val api: ApiService) {
    suspend fun pageArticlesList(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.pageArticlesList(bean)
        }

    suspend fun pageSelectOne(bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.pageSelectOne(bean)
        }
    suspend fun updateUser(mUserUpdateRequest: UserUpdateRequest) =
        withContext(Dispatchers.IO) {
            api.updateUser(mUserUpdateRequest)
        }
    suspend fun signIn() =
        withContext(Dispatchers.IO) {
            api.signIn()
        }
    suspend fun readRecord() =
        withContext(Dispatchers.IO) {
            api.readRecord()
        }
}