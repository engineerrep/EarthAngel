package com.earth.angel.regist.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.earth.angel.R
import com.earth.angel.databinding.FragmentPasswordCodeBinding
import com.earth.angel.regist.RegistModel
import com.earth.angel.util.CountDownTimerUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.network.request.EmailCodeRequest
import com.earth.libbase.network.request.RegistRequest
import com.earth.libbase.util.CheckPwdUtils
import com.earth.libbase.util.SnackbarUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class PasswordCodeFragment : BaseFragment<FragmentPasswordCodeBinding>() {

    private val mPasswordViewModle: PasswordViewModle by activityViewModels()
    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()

    override fun getLayoutId(): Int =R.layout.fragment_password_code

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            mPasswordViewModle.getData().email?.let {
                tvPhoneEmail.text=it
            }
            tvTitleCenter.text = "Forgot Password"
            tvLeftTool?.setOnClickListener {
                findNavController().popBackStack()
            }
            codeSend?.setOnClickListener {
                var emailStr=tvPhoneEmail.text.toString().trim()
                if(emailStr.isNullOrEmpty()){
                    val snackbar = Snackbar.make(
                        CoordinatorLayout, R.string.base_tips_Email, Snackbar.LENGTH_LONG
                    )
                    val mView = snackbar.view
                    val tvSnackBarText: TextView = mView.findViewById(R.id.snackbar_text)
                    tvSnackBarText.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_error))
                    mView.setBackgroundColor(
                        ContextCompat.getColor(requireContext(),
                            R.color.white))
                    snackbar.show()
                    return@setOnClickListener
                }
                sendCode(emailStr)
            }

            nextBtn.setOnClickListener {
                var emailStr=tvPhoneEmail.text.toString().trim()
                if(emailStr.isNullOrEmpty()){
                    val snackbar = Snackbar.make(
                        CoordinatorLayout, R.string.base_tips_Email, Snackbar.LENGTH_LONG
                    )
                    val mView = snackbar.view
                    val tvSnackBarText: TextView = mView.findViewById(R.id.snackbar_text)
                    tvSnackBarText.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_error))
                    mView.setBackgroundColor(
                        ContextCompat.getColor(requireContext(),
                            R.color.white))
                    snackbar.show()
                    return@setOnClickListener
                }
                var emailCode=etPhoneEmail.text.toString().trim()
                if (emailCode.isNullOrEmpty()) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_code))
                    return@setOnClickListener

                }
                var etNewPassword=etNewPassword.text.toString().trim()

                if (etNewPassword.isNullOrEmpty()) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_tips_newPassword))
                    return@setOnClickListener

                }

                if (etNewPassword.length < 6 || etNewPassword.length > 20) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_tips_password))
                    return@setOnClickListener

                }
                if (!CheckPwdUtils.checkPwd(etNewPassword)) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_Password_Error))

                    return@setOnClickListener
                }
                var etNewAgainPassword=etNewAgainPassword.text.toString().trim()
                if (etNewAgainPassword.isNullOrEmpty()) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_tips_newPasswordAgain))
                    return@setOnClickListener

                }

                if (etNewAgainPassword.length < 6 || etNewAgainPassword.length > 20) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_tips_password))
                    return@setOnClickListener

                }
                if (!CheckPwdUtils.checkPwd(etNewAgainPassword)) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_Password_Error))

                    return@setOnClickListener
                }
                if (etNewPassword!=etNewAgainPassword) {
                    SnackbarUtil.showError(CoordinatorLayout, getString(com.earth.libmine.R.string.base_tips_passwordAgain))
                    return@setOnClickListener


                }
                verifyEmailCode(emailStr,emailCode,etNewPassword)
            }
        }
    }

    fun sendCode(email: String) {
        launch {
            mRegistModel.emailCode(RegistRequest(email = email))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                    activity?.toast(getString(R.string.label_emailpassword_Wrong))
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                    /*    if (BaseDateUtils.isFastClick()) {
                            findNavController().navigate(R.id.passwordSuccessFragment)
                        }*/
                        var time =
                            CountDownTimerUtils(mBinding!!.codeSend, 60000, 1000)
                        time.start()
                    }
                }
        }
    }
    fun verifyEmailCode(email: String,code: String,newPassword: String) {
        launch {
            mRegistModel.verifyEmailCode(EmailCodeRequest(email = email,code = code,newPassword = newPassword))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                    activity?.toast(getString(R.string.label_emailpassword_Wrong))
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        findNavController().navigate(R.id.passwordSuccessFragment)
                    }
                }
        }
    }

}