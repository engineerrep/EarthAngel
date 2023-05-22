package com.earth.libbase.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    val showLoadingProgress = MutableLiveData<Boolean>()

    fun showLoading() = showLoadingProgress.postValue(true)

    fun dismissLoading() = showLoadingProgress.postValue(false)

}