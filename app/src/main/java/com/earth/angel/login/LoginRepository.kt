package com.earth.angel.login


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository(private val api: ApiService) {

    suspend fun login(loging: LoginRequest) =
        withContext(Dispatchers.IO) {
            api.login(loging)
        }

}