package com.earth.angel.regist.forgotpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.PasswordEmailRequest

class PasswordViewModle : ViewModel() {

    var bean = PasswordEmailRequest()


    var registerParam = MutableLiveData<PasswordEmailRequest>()
    fun setEmail(email: String?) {
        email?.let {
            bean.email=it
        }
        registerParam.postValue(bean)
    }

    fun getData(): PasswordEmailRequest {
        return bean
    }
}