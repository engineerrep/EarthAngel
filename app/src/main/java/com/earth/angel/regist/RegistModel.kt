package com.earth.angel.regist

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.EmailCodeRequest
import com.earth.libbase.network.request.RegistRequest
import com.earth.libbase.network.request.UserUpdateRequest
import kotlinx.coroutines.flow.flow

class RegistModel(private val repository: RegistRepository): ViewModel() {

    fun regist(regist: RegistRequest) = flow {
        emit(repository.regist(regist))
    }
    fun emailLogin(regist: RegistRequest) = flow {
        emit(repository.emailLogin(regist))
    }
    fun updateUser(mUserUpdateRequest: UserUpdateRequest) = flow {
        emit(repository.updateUser(mUserUpdateRequest))
    }

    fun emailCode(regist: RegistRequest) = flow {
        emit(repository.emailCode(regist))
    }

    fun verifyEmailCode(regist: EmailCodeRequest) = flow {
        emit(repository.verifyEmailCode(regist))
    }

}