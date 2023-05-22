package com.earth.angel.regist.ui

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.FragmentLoginMainBinding
import com.earth.angel.regist.RegistModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.network.request.RegistRequest
import com.earth.libbase.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginMainFragment : BaseFragment<FragmentLoginMainBinding>() {

    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()

    override fun getLayoutId(): Int = R.layout.fragment_login_main

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            val lp: ConstraintLayout.LayoutParams =
                AppBank.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(0, BaseScreenUtil.getActivityMessageHeight(), 0, 0)
            AppBank.layoutParams = lp
            AppBank.setOnClickListener {
                findNavController().popBackStack()
            }
            nextBtn.setOnClickListener {
                var email = etPhoneLogin.text.toString().trim()
                var password = etPhonePassword.text.toString().trim()
                if (BaseDateUtils.isFastClick()) {
                    if (check(email, password)) {
                        emailLogin(email, password)
                    }
                }
            }
            PreferencesHelper.getEmail(BaseApplication.instance).let {
                etPhoneLogin.setText(it)
            }
            etPhonePassword.run {
                doAfterTextChanged {
                    val content = it.toString()
                    if (content.isEmpty()) {
                        ivPasswordSee.visibility = View.GONE
                    } else {
                        ivPasswordSee.visibility = View.VISIBLE
                    }
                }
            }
            etPhonePassword.transformationMethod = PasswordTransformationMethod.getInstance()


            ivPasswordSee.setOnClickListener {
                ivPasswordSee.isSelected = !ivPasswordSee.isSelected
                if (ivPasswordSee.isSelected) {
                    etPhonePassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    etPhonePassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
                val password = etPhonePassword.text.toString()
                if (password.isNotEmpty()) {
                    etPhonePassword.setSelection(password.length)
                }
            }
            tvForgotPassword.setOnClickListener {
                    ARouter.getInstance().build(Constance.ForgotPasswordActivityURL).navigation()
            }
        }
    }

    fun check(email: String, password: String): Boolean {
        mBinding?.run {
            if (email.isNullOrEmpty()) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_Email))
                return false
            }
            if (!AppUtils.isValidEmail(email)) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_EmailInvalid))
                return false
            }
            if (password.isNullOrEmpty()) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_Password))
                return false
            }
            if (password.length < 6 || password.length > 20) {
                SnackbarUtil.showError(CoordinatorLayout, getString(R.string.base_tips_password))
                return false
            }
        }
        return true

    }


    fun emailLogin(email: String, posswaor: String) {
        launch {
            mRegistModel.emailLogin(RegistRequest(email, posswaor))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                    mBinding?.let {
                        SnackbarUtil.showError(it.CoordinatorLayout, getString(R.string.label_emailpassword_Wrong))
                    }
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it?.data?.let {
                            it.token?.let {
                                PreferencesHelper.saveToken(BaseApplication.instance, it)
                            }
                            it.imSig?.let {
                                PreferencesHelper.saveImtoken(BaseApplication.instance, it)
                            }
                            PreferencesHelper.saveEmail(BaseApplication.instance, email)
                            it?.user?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                activity?.finish()
                                ARouter.getInstance().build(Constance.EarthAngelMainActivityURL)
                                    .navigation()

                                //findNavController().navigate(R.id.registNameFragment)

                            }
                        }
                    }
                }
        }
    }

}