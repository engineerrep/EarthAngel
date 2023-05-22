package com.earth.angel.login

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.LoginRequest
import kotlinx.coroutines.flow.flow

class LoginModel(private val repository: LoginRepository): ViewModel() {

    fun login(loging: LoginRequest) = flow {
        emit(repository.login(loging))
    }

}