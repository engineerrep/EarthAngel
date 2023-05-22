package com.earth.libarticl

import androidx.lifecycle.ViewModel
import com.earth.libbase.baseentity.BaseUserRequest
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ArticleModle(private val repository: ArticleRepository):ViewModel() {

    fun pageArticlesList(bean: CommentRequest) = flow {
        emit(repository.pageArticlesList(bean))
    }


    fun pageSelectOne(bean: CommentRequest) = flow {
        emit(repository.pageSelectOne(bean))
    }
    fun updateUser(mUserUpdateRequest: UserUpdateRequest) = flow {
        emit(repository.updateUser(mUserUpdateRequest))
    }
    fun signIn() = flow {
        emit(repository.signIn())
    }

    fun readRecord() = flow {
        emit(repository.readRecord())
    }
}