package com.earth.libhome.group

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.GroupNameRequest
import com.earth.libbase.network.request.UserUpdateRequest

class GroupModle : ViewModel() {

    var bean = GroupNameRequest()


    var registerParam = MutableLiveData<GroupNameRequest>()
    fun setName(firstName: String?) {
        firstName?.let {
            bean.communityName = it
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
    fun setAddress(address: String?) {
        address?.let {
            bean.address=it
        }
        registerParam.postValue(bean)
    }

    fun getData(): GroupNameRequest {
        return bean
    }
}