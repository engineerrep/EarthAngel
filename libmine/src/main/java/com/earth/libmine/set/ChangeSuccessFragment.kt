package com.earth.libmine.set

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.PreferencesHelper
import com.earth.libmine.R
import com.earth.libmine.databinding.FragmentChangePasswordBinding
import com.earth.libmine.databinding.FragmentChangeSuccessBinding
import com.google.android.material.snackbar.Snackbar
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager

import kotlinx.android.synthetic.main.libmine_titlebar.*


class ChangeSuccessFragment : BaseFragment<FragmentChangeSuccessBinding>() {



    override fun getLayoutId(): Int = R.layout.fragment_change_success

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            tvLibMineLeftTool?.visibility=View.INVISIBLE
            tvLibMinTitleCenter.text="Change Password"
            nextBtn.setOnClickListener {
                ActivityStackManager.finishAll()
                BaseApplication.myBaseUser = null
                PreferencesHelper.clear(requireContext())
                ARouter.getInstance().build(Constance.RegistActivityURL).navigation()
                V2TIMManager.getInstance().logout(object : V2TIMCallback {
                    override fun onError(code: Int, desc: String) {
                    }
                    override fun onSuccess() {
                    }
                })

            }
        }
    }

  /*  fun emailLogin(email: String) {
        launch {
            mRegistModel.emailCode(RegistRequest(email = email))
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                    activity?.toast("Error in account or password")
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        if (BaseDateUtils.isFastClick()) {
                            findNavController().navigate(R.id.passwordInputFragment)
                        }
                    }
                }
        }
    }*/


}