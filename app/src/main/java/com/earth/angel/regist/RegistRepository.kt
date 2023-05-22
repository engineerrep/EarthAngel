package com.earth.angel.regist


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.EmailCodeRequest
import com.earth.libbase.network.request.RegistRequest
import com.earth.libbase.network.request.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistRepository(private val api: ApiService) {

    suspend fun regist(regist: RegistRequest) =
        withContext(Dispatchers.IO) {
            api.emailRegister(regist)
        }
    suspend fun emailLogin(regist: RegistRequest) =
        withContext(Dispatchers.IO) {
            api.emailLogin(regist)
        }
    suspend fun updateUser(mUserUpdateRequest: UserUpdateRequest) =
        withContext(Dispatchers.IO) {
            api.updateUser(mUserUpdateRequest)
        }

    suspend fun emailCode(regist: RegistRequest) =
        withContext(Dispatchers.IO) {
            api.emailCode(regist)
        }
    suspend fun verifyEmailCode(regist: EmailCodeRequest) =
        withContext(Dispatchers.IO) {
            api.verifyEmailCode(regist)
        }
}