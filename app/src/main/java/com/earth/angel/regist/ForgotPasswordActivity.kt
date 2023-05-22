package com.earth.angel.regist

import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.angel.R
import com.earth.angel.databinding.ActivityPasswordMainBinding
import com.earth.angel.regist.forgotpassword.PasswordViewModle
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.dialog.LoadingDialog
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


@Route(path = Constance.ForgotPasswordActivityURL)
class ForgotPasswordActivity : BaseActivity<ActivityPasswordMainBinding>() {
    private val mPasswordViewModle by viewModel<PasswordViewModle>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    override fun getLayoutId(): Int =R.layout.activity_password_main

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mAppViewModel?.showLoadingProgress.observe(this@ForgotPasswordActivity, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })
    }

}