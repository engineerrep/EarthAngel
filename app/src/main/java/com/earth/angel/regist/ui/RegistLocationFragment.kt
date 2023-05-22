package com.earth.angel.regist.ui

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.FragmentRegistLocationBinding
import com.earth.angel.regist.RegistModel
import com.earth.libbase.base.*
import com.earth.libbase.util.LocationUtil
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistLocationFragment : BaseFragment<FragmentRegistLocationBinding>() {

    private val mRegistModel by viewModel<RegistModel>()
    private val mAppViewModel: AppViewModel by activityViewModels()
    private val mRegistViewModle: RegistViewModle by activityViewModels()
    private var longitude: Double? = null
    private var latitude: Double? = null

    override fun getLayoutId(): Int =R.layout.fragment_regist_location

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
            mBinding?.run {
                getPermissions()
                nextBtn?.setOnClickListener {
                    mRegistViewModle.setLivein(RegistLocationEt.text.toString())
                    updateUser()
                }
            }
    }

    private fun getPermissions() {
        var permissionsList: MutableList<String> = mutableListOf()
        permissionsList.add(0, Manifest.permission.ACCESS_COARSE_LOCATION)
        permissionsList.add(1, Manifest.permission.ACCESS_FINE_LOCATION)
        delayLaunch(500) {
            block = {
                requestPermissions {
                    permissions = permissionsList
                    onAllPermissionsGranted = {
                        PreferencesHelper.saveLocation(requireContext(), true)
                        if (LocationUtil().getLocationByNet(requireContext()) != null) {
                            latitude =
                                LocationUtil().getLocationByNet(requireContext()).doublelist[0]
                            longitude =
                                LocationUtil().getLocationByNet(requireContext()).doublelist[1]

                            if(latitude != null && longitude != null) {
                                mRegistViewModle.setLocation(latitude=latitude!!,longitude = longitude!!)
                            }
                            if(LocationUtil().getLocationByNet(requireContext()).addresseslist!=null){
                                LocationUtil().getLocationByNet(requireContext()).addresseslist?.let {
                                    if(it.size>0){
                                        it[0]?.let { itAddre->
                                            if(itAddre.adminArea!=null&&itAddre.locality!=null&&itAddre.subLocality!=null){
                                                mBinding?.RegistLocationEt?.setText(itAddre.adminArea+itAddre.locality+itAddre.subLocality)
                                            }

                                        }
                                    }

                                }
                            }

                        }
                    }
                    onPermissionsDenied = {
                    }
                    onPermissionsNeverAsked = {
                    }
                }
            }
        }

    }


    fun updateUser() {
        launch {
            mRegistModel.updateUser(mRegistViewModle.getData())
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