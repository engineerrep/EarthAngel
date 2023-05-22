package com.earth.libmine.set

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.*
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.util.PreferencesHelper
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityLibmineEtnameBinding
import com.earth.libmine.ui.LibMineModle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.libmine_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.LibMineEtNameActivityURL)
class LibMineEtNameActivity : BaseActivity<ActivityLibmineEtnameBinding>() {

    private val mMineModle by viewModel<LibMineModle>()
        
    private val mAppViewModel by viewModel<AppViewModel>()

    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    override fun getLayoutId(): Int = R.layout.activity_libmine_etname

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding.run {
            tvLibMineLeftTool?.setOnClickListener {
                finish()
            }
            LibMineEtNameDone.isEnabled=false
            LibMineEtNameFirst.run {
                doAfterTextChanged {
                    checkCanLogin()
                }
            }

            BaseApplication.myBaseUser?.firstName?.let {
                LibMineEtNameFirst.setText(it)
            }
            BaseApplication.myBaseUser?.lastName?.let {
                LibMineEtNameLast.setText(it)
            }
            tvLibMinTitleCenter.text = "Edit Name"
            LibMineEtNameLast.run {
                doAfterTextChanged {
                    checkCanLogin()
                }
            }
            LibMineEtNameDone.setOnClickListener {
                updateUser()
            }
            mAppViewModel.showLoadingProgress.observe(this@LibMineEtNameActivity, Observer {
                if (it) mLoadingDialog.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog.dismiss()
            })
        }
    }
    fun updateUser() {
        var first=mBinding.LibMineEtNameFirst.text.toString()
        var last=mBinding.LibMineEtNameLast.text.toString()

        launch {
            mMineModle.updateUser(UserUpdateRequest(firstName = first, lastName= last))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineEtNameActivity)) {
                        it?.data?.let {
                            PreferencesHelper.saveMyProfileCache(
                                BaseApplication.instance,
                                Gson().toJson(it)
                            )
                            MessageObservable.messageObservable.newMessage(UpdateEntity(nikeName = it.nickName))

                            finish()
                        }
                    }
                }
        }
    }

    private fun checkCanLogin() {
        mBinding.LibMineEtNameDone.isEnabled = when {
            mBinding.LibMineEtNameFirst.text.isNullOrEmpty() -> false
            mBinding.LibMineEtNameLast.text.isNullOrEmpty() -> false

            mBinding.LibMineEtNameFirst.text!!.length > 10 -> {
                false
            }
            mBinding.LibMineEtNameLast.text!!.length > 10 -> {
                false
            }
            else -> {
                true
            }
        }
    }

}