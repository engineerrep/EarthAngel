package com.earth.libmine.set

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.network.request.ChangePasswordRequest
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.CheckPwdUtils
import com.earth.libbase.util.SnackbarUtil
import com.earth.libmine.R
import com.earth.libmine.databinding.FragmentChangePasswordBinding
import com.earth.libmine.ui.LibMineModle
import kotlinx.android.synthetic.main.libmine_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val mMineModle by viewModel<LibMineModle>()

    override fun getLayoutId(): Int = R.layout.fragment_change_password

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            tvLibMineLeftTool?.setOnClickListener {
                activity?.finish()
            }
            tvLibMinTitleCenter.text = "Change Password"
            nextBtn.setOnClickListener {
                if (check()) {
                    if (BaseDateUtils.isFastClick()) {
                        var emailStr = etOldPassword.text.toString().trim()
                        var etNewPasswordStr = etNewPassword.text.toString().trim()

                        emailLogin(etNewPasswordStr, emailStr)
                    }
                }
            }
        }
    }

    fun check(): Boolean {
        mBinding?.run {
            var emailStr = etOldPassword.text.toString().trim()
            if (emailStr.isNullOrEmpty()) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_oldPassword))
                return false
            }
            if (emailStr.length < 6 || emailStr.length > 20) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_password))
                return false
            }
            var etNewPasswordStr = etNewPassword.text.toString().trim()
            if (etNewPasswordStr.isNullOrEmpty()) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_newPassword))
                return false
            }


            if (etNewPasswordStr.length < 6 || etNewPasswordStr.length > 20) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_password))

                return false
            }
            if (!CheckPwdUtils.checkPwd(etNewPasswordStr)) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_Password_Error))
                return false
            }
            var etNewAgainPasswordStr = etNewAgainPassword.text.toString().trim()
            if (etNewAgainPasswordStr.isNullOrEmpty()) {
                SnackbarUtil.showError(
                    CoordinatorLayout,
                    getString(R.string.base_tips_newPasswordAgain)
                )
                return false
            }

            if (etNewAgainPasswordStr.length < 6 || etNewAgainPasswordStr.length > 20) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_password))
                return false
            }
            if (!CheckPwdUtils.checkPwd(etNewAgainPasswordStr)) {
                activity?.toast(R.string.base_Password_Error)
                return false
            }
            if (etNewPasswordStr!=etNewAgainPasswordStr) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_passwordAgain))
                return false
            }


        }
        return true
    }


    fun emailLogin(newPassword: String, oldPassword: String) {
        launch {
            mMineModle.changePassword(
                ChangePasswordRequest(
                    newPassword = newPassword,
                    oldPassword = oldPassword
                )
            )
                .onStart {
                }.catch {
                }.onCompletion {
                }.collectLatest {
                    if (it.isOk(activity)) {
                        findNavController().navigate(R.id.changeSuccessFragment)
                    }
                }
        }
    }


}