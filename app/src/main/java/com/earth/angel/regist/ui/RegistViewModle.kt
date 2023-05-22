package com.earth.angel.regist.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.UserUpdateRequest

class RegistViewModle : ViewModel() {

    var bean = UserUpdateRequest()


    var registerParam = MutableLiveData<UserUpdateRequest>()
    fun setBean(firstName: String?, headImgUrl: String?, lastName: String?,latitude: Double,liveIn: String?,longitude: Double? ) {
        firstName?.let {
            bean.firstName = it
        }
        headImgUrl?.let {
            bean.headImgUrl = it
        }
        lastName?.let {
            bean.lastName = it
        }
        latitude?.let {
            bean.latitude = it
        }
        liveIn?.let {
            bean.liveIn = it
        }
        longitude?.let {
            bean.longitude = it
        }
        registerParam.postValue(bean)
    }
    fun setName(firstName: String?, lastName: String?) {
        firstName?.let {
            bean.firstName = it
        }

        lastName?.let {
            bean.lastName = it
        }
        registerParam.postValue(bean)
    }
    fun setLocation(latitude: Double,longitude: Double?) {
        latitude?.let {
            bean.latitude = it
        }
        longitude?.let {
            bean.longitude = it
        }
        registerParam.postValue(bean)
    }
    fun setLivein(liveIn: String?) {
        liveIn?.let {
            bean.liveIn = it
        }
        registerParam.postValue(bean)
    }
    fun getData(): UserUpdateRequest {
        return bean
    }
}