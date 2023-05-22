package com.earth.angel.regist.forgotpassword

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.FragmentPasswordSuccessBinding
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.PreferencesHelper
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import kotlinx.android.synthetic.main.include_top_bar.*


class PasswordSuccessFragment : BaseFragment<FragmentPasswordSuccessBinding>() {

    override fun getLayoutId(): Int =R.layout.fragment_password_success

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            tvLeftTool?.visibility=View.GONE
            nextBtn.setOnClickListener {
                ActivityStackManager.finishAll()
                BaseApplication.myBaseUser = null
                PreferencesHelper.clear(requireContext())
                ARouter.getInstance().build(Constance.RegistActivityURL).navigation()
                V2TIMManager.getInstance().logout(object : V2TIMCallback {
                    override fun onSuccess() {
                    }
                    override fun onError(code: Int, desc: String?) {
                    }
                })
            }
        }
    }



}