package com.earth.angel.regist.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.FragmentRegistNameBinding
import com.earth.angel.regist.RegistModel
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.base.Constance
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistNameFragment : BaseFragment<FragmentRegistNameBinding>() {

    private val mRegisterViewModel: RegistViewModle by activityViewModels()
    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()

    override fun getLayoutId(): Int =R.layout.fragment_regist_name

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            nextBtn.setOnClickListener {
                var firstName=RegistNameFirstName.text.toString()
                var LastName=RegistNameLastName.text.toString()
                if(firstName.isNullOrEmpty()||firstName.length>10){
                    activity?.toast(getString(com.earth.libmine.R.string.base_tips_FirstName))
                    return@setOnClickListener
                }
                if(LastName.isNullOrEmpty()||LastName.length>10){
                    activity?.toast(getString(com.earth.libmine.R.string.base_tips_Lastname))
                    return@setOnClickListener
                }
                mRegisterViewModel.setName(firstName=firstName,lastName = LastName)
               // findNavController().navigate(R.id.registLocationFragment)
                updateUser()
            }
            RegistNameBank.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
    fun updateUser() {
        launch {
            mRegistModel.updateUser(mRegisterViewModel.getData())
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(activity)) {
                        it?.data?.let {
                            PreferencesHelper.saveMyProfileCache(
                                BaseApplication.instance,
                                Gson().toJson(it)
                            )
                            ARouter.getInstance().build(Constance.EarthAngelMainActivityURL).navigation()
                            activity?.finish()
                        }
                    }
                }
        }
    }

}