package com.earth.angel.regist

import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.ActivityRegistBinding
import com.earth.angel.regist.ui.RegistViewModle
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.util.PreferencesHelper
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.RegistActivityURL)
class RegistActivity : BaseActivity<ActivityRegistBinding>() {

    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val mRegisterViewModel by viewModel<RegistViewModle>()

    override fun getLayoutId(): Int =R.layout.activity_regist

    override fun initActivity(savedInstanceState: Bundle?) {
            fitSystemBar()
        mAppViewModel?.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })

        PreferencesHelper.getToken(this)?.let {
            if(!it.isNullOrEmpty()){
                finish()
                ARouter.getInstance().build(Constance.EarthAngelMainActivityURL).navigation()
            }
        }

    }
}