package com.earth.libbase.base

import androidx.databinding.BindingAdapter
import com.earth.libbase.view.loadingstatus.ErrorReload
import com.earth.libbase.view.loadingstatus.LoadingStatusCode
import com.earth.libbase.view.loadingstatus.LoadingStatusView


/**
 * 错误处理绑定
 */
@BindingAdapter(value = ["bind:requestStatusCode", "bind:errorReload"], requireAll = false)
fun bindRequestStatus(
    statusView: LoadingStatusView,
    requestStatusCode: LoadingStatusCode?,
    errorReload: ErrorReload?
) {
    statusView.injectRequestStatus(requestStatusCode ?: LoadingStatusCode.Succeed)
    statusView.errorReload = errorReload
}