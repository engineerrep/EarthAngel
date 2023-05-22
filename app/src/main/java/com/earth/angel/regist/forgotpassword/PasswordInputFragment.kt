package com.earth.angel.regist.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.earth.angel.R
import com.earth.angel.databinding.FragmentPasswordInputBinding
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.util.BaseDateUtils
import kotlinx.android.synthetic.main.include_top_bar.*


class PasswordInputFragment : BaseFragment<FragmentPasswordInputBinding>() {

    override fun getLayoutId(): Int =R.layout.fragment_password_input

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            tvTitleCenter.text = "Forgot Password"
            tvLeftTool?.setOnClickListener {
                findNavController().popBackStack()
            }

            nextBtn?.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    findNavController().navigate(R.id.passwordSuccessFragment)
                }
            }
        }
    }



}