package com.earth.angel.mine.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivityVerifyPhoneBinding
import com.earth.angel.user.UserModel
import com.earth.angel.util.DataReportUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.util.PreferencesHelper
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerifyPhoneActivity : BaseActivity<ActivityVerifyPhoneBinding>() {
    private var phoneCode: String? = "86"
    private val userModle by viewModel<UserModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    override fun getLayoutId(): Int = R.layout.activity_verify_phone

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding.run {
            handler = this@VerifyPhoneActivity
            tvTitleCenter.text = getString(R.string.lab_My_Phone_Number)


            code.text = "+" + EarthAngelApp.myProfileEntity?.phoneCode

            llCode.setOnClickListener {

            }
            codeSend.isEnabled = false
            btSave.isEnabled = false
            PreferencesHelper.getPhone(this@VerifyPhoneActivity)?.let {
                etPhone.setText(it)
                etPhone.setSelection(it.length)
                codeSend.isEnabled = true
            }
            etPhone.run {
                doAfterTextChanged {
                    checkCanSend()
                    checkCanSave()
                }
            }
            etCode.run {
                doAfterTextChanged {
                    checkCanSave()
                }
            }

            codeSend.setOnClickListener {
                send()

            }
            btSave.setOnClickListener {
                save()
            }
            mAppViewModel.showLoadingProgress.observe(this@VerifyPhoneActivity, Observer {
                if (it) mLoadingDialog.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog.dismiss()
            })
        }


    }

    private fun save() {
        DataReportUtils.getInstance().report("Get_code")

        launch {
            userModle.updateMobileNumberRequest(
                mBinding.etCode.text.toString(),
                phoneCode!!,
                mBinding.etPhone.text.toString()
            ).catch {
                toast("Verify code expired")
            }
                .onStart {
                    mAppViewModel.showLoading()
                }
                .onCompletion {
                    mAppViewModel.dismissLoading()
                }
                .collect {
                    if (it.isOk(this@VerifyPhoneActivity)) {
                        PreferencesHelper.savePhone(
                            this@VerifyPhoneActivity,
                            mBinding.etPhone.text.toString()
                        )
                        setResult(100)
                        finish()
                    }

                }
        }
    }

    private fun send() {
        launch {
            userModle.sendPhoneVerifyCode(phoneCode!!, mBinding?.etPhone.text.toString()).catch { }
                .onStart {
                    mAppViewModel.showLoading()
                }
                .onCompletion {
                    mAppViewModel.dismissLoading()
                }
                .collect {
                    if (it.isOk(this@VerifyPhoneActivity)) {
                        /*var time =
                            CountDownTimerUtils(mBinding?.codeSend, mBinding?.etPhone, 60000, 1000)
                        time.start()*/
                    }

                }
        }
    }

    private fun checkCanSave() {
        mBinding?.btSave.isEnabled = when {
            mBinding.etPhone.text.isNullOrEmpty() -> false
            mBinding.etCode.text.isNullOrEmpty() -> false
            else -> {
                true
            }
        }
    }

    private fun checkCanSend() {
        mBinding?.codeSend.isEnabled = when {
            mBinding.etPhone.text.isNullOrEmpty() -> false
            else -> {
                true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 111 && resultCode == RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}