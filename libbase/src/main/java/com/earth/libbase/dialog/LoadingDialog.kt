package com.earth.libbase.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.earth.libbase.R

import com.earth.libbase.base.BaseDialogFragment
import com.earth.libbase.databinding.DialogLoadingBinding


class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>() {

    override fun layoutId() = R.layout.dialog_loading

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
    }

    override fun dialogFragmentAnim() = R.style.DialogAlterWithNoAnimation

    override fun dialogFragmentAttributes() = dialog?.window?.attributes?.apply {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
    }
}