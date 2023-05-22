package com.earth.angel.regist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.earth.angel.R
import com.earth.angel.databinding.FragmentRegistEmailBinding
import com.earth.angel.regist.RegistModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.network.request.RegistRequest
import com.earth.libbase.util.AppUtils.isValidEmail
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.CheckPwdUtils
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistEmailFragment : BaseFragment<FragmentRegistEmailBinding>() {

    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()


    override fun getLayoutId(): Int = R.layout.fragment_regist_email

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            nextBtn.setOnClickListener {
                var email = etPhoneLogin.text.toString().trim()
                var password = etPhonePassword.text.toString().trim()
                var etPhoneCode = etPhoneCode.text.toString().trim()
                if (BaseDateUtils.isFastClick()) {
                    if (check(email, password)) {
                        regist(email, password,etPhoneCode)
                    }
                }
            }
            logintBtn.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    findNavController().navigate(R.id.registLoginMainFragment)
                }
            }
        }
    }

    fun check(email: String, password: String): Boolean {
        mBinding?.run {
            if (email.isNullOrEmpty()) {
                activity?.toast(R.string.base_tips_Email)
                return false
            }
           if (!isValidEmail(email)) {
                activity?.toast(R.string.base_tips_EmailInvalid)
                return false
            }
            if (password.isNullOrEmpty()) {
                activity?.toast(R.string.base_tips_Password)
                return false
            }


            if (password.length<6||password.length>20) {
                activity?.toast(R.string.base_tips_password)
                return false
            }
            if (!CheckPwdUtils.checkPwd(password)) {
                activity?.toast(R.string.base_Password_Error)
                return false
            }
        }
        return true

    }


    fun regist(email: String, password: String,etPhoneCode: String) {
        var mRegistRequest=RegistRequest()
        mRegistRequest.email=email
        mRegistRequest.password=password
        if(!etPhoneCode.isNullOrEmpty()){
            mRegistRequest.invitationCode=etPhoneCode
        }
        launch {
            mRegistModel.regist(mRegistRequest)
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it?.data?.let {
                            it?.user?.let { ituser ->
                                PreferencesHelper.saveMyProfileCache(
                                    BaseApplication.instance,
                                    Gson().toJson(ituser)
                                )
                                findNavController().navigate(R.id.registNameFragment)
                            }
                            it?.token?.let {
                                PreferencesHelper.saveToken(BaseApplication.instance, it)
                            }
                            it?.imSig?.let {
                                PreferencesHelper.saveImtoken(BaseApplication.instance, it)
                            }
                        }

                        PreferencesHelper.saveEmail(BaseApplication.instance,email)
                    }
                }
        }
    }


}