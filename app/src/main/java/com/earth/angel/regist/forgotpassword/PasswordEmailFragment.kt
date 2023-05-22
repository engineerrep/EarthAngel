package com.earth.angel.regist.forgotpassword

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.earth.angel.R
import com.earth.angel.databinding.FragmentPasswordEmailBinding
import com.earth.angel.regist.RegistModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.util.BaseDateUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.include_top_bar.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PasswordEmailFragment : BaseFragment<FragmentPasswordEmailBinding>() {
    private val mPasswordViewModle: PasswordViewModle by activityViewModels()
    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()



    override fun getLayoutId(): Int =R.layout.fragment_password_email

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            tvTitleCenter.text = "Forgot Password"
            tvLeftTool?.setOnClickListener {
               activity?.finish()
            }
            nextBtn.setOnClickListener {
                var emailStr=etPhoneEmail.text.toString().trim()
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
                mPasswordViewModle.setEmail(email = emailStr)


                if (BaseDateUtils.isFastClick()) {
                    findNavController().navigate(R.id.passwordCodeFragment)
                }
            }
        }
    }



}