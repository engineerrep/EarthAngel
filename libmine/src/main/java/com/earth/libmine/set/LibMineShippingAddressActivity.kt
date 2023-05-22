package com.earth.libmine.set

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.Constance
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.request.ShipAddressRequest
import com.earth.libbase.network.request.UserUpdateRequest
import com.earth.libbase.util.PreferencesHelper
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityLibmineShippingaddressBinding
import com.earth.libmine.ui.LibMineModle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.libmine_titlebar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.LibMineShippingAddressActivityURL)
class LibMineShippingAddressActivity : BaseActivity<ActivityLibmineShippingaddressBinding>() {
    private val mMineModle by viewModel<LibMineModle>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    override fun getLayoutId(): Int = R.layout.activity_libmine_shippingaddress
    private var id: String? = null

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text = "Shipping Address"
            LibMineSAAddLy?.setOnClickListener {
                mBinding.LibMineSAAddLy.visibility = View.GONE
                mBinding.LibMineSACl.visibility = View.VISIBLE
                mBinding.LibMineSAAddRl.visibility = View.GONE
                mBinding.nextBtn.visibility = View.VISIBLE
            }
            nextBtn?.setOnClickListener {
                if (id == null) {
                    shoppingAddressAdd()
                } else {
                    shoppingAddressUpdate()
                }
            }
            LibMineSAAddRl?.setOnClickListener {
                mBinding.LibMineSAAddLy.visibility = View.GONE
                mBinding.LibMineSACl.visibility = View.VISIBLE
                mBinding.LibMineSAAddRl.visibility = View.GONE
                mBinding.nextBtn.visibility = View.VISIBLE
            }
            mAppViewModel?.showLoadingProgress.observe(
                this@LibMineShippingAddressActivity,
                Observer {
                    if (it) mLoadingDialog?.showAllowStateLoss(
                        supportFragmentManager,
                        mLoadingDialog::class.simpleName!!
                    )
                    else mLoadingDialog?.dismiss()
                })
        }
        updateAddress()
    }

    private fun updateAddress() {
        launch {
            mMineModle.shoppingAddressIndex()
                .onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LibMineShippingAddressActivity)) {
                        if (it.data == null) {
                            mBinding.LibMineSAAddLy.visibility = View.VISIBLE
                            mBinding.LibMineSACl.visibility = View.GONE
                            mBinding.LibMineSAAddRl.visibility = View.GONE
                            mBinding.nextBtn.visibility = View.GONE
                        } else {
                            it.data?.let {
                                mBinding.LibMineSAAddLy.visibility = View.GONE
                                mBinding.LibMineSACl.visibility = View.GONE
                                mBinding.LibMineSAAddRl.visibility = View.VISIBLE
                                mBinding.nextBtn.visibility = View.GONE
                                id = it.id
                                mBinding?.run {
                                    LibMineSANameEd.text = (it.nickName)
                                    LibMineSASA.text = (it.streetAddress)
                                    LibMineSAP.text = (it.district)+","+(it.province)+" "+it.zipCode
                                    LibMineSAZ.text =   it.phoneNumber
                                    LibMineSAName.setText(it.nickName)
                                    LibMineSAStreetAddress.setText(it.streetAddress)
                                    LibMineSADistrict.setText(it.district)
                                    LibMineSAProvince.setText(it.province)
                                    LibMineSAZipCode.setText(it.zipCode.toString())
                                    LibMineSAPhoneNumber.setText(it.phoneNumber)
                                }
                            }

                        }
                    }
                }
        }
    }

    private fun shoppingAddressAdd() {
        mBinding?.run {
            var mName = LibMineSAName.text.toString()
            var mStreetAddress = LibMineSAStreetAddress.text.toString()
            var mLibMineSADistrict = LibMineSADistrict.text.toString()
            var mLibMineSAProvince = LibMineSAProvince.text.toString()
            var mLibMineSAZipCode = LibMineSAZipCode.text.toString()
            var mLibMineSAPhoneNumber = LibMineSAPhoneNumber.text.toString()
            if (mName.isNullOrEmpty()||mName.length>20) {
                toast(getString(R.string.base_tips_name))
                return
            }
            if (mStreetAddress.isNullOrEmpty()||mStreetAddress.length>100) {
                toast(getString(R.string.base_tips_street_address))
                return
            }
            if (mLibMineSADistrict.isNullOrEmpty()||mLibMineSADistrict.length>20) {
                toast(getString(R.string.base_tips_District))
                return
            }
            if (mLibMineSAProvince.isNullOrEmpty()||mLibMineSAProvince.length>20) {
                toast(getString(R.string.base_tips_province))
                return
            }
            if (mLibMineSAZipCode.isNullOrEmpty()||mLibMineSAZipCode.length>10) {
                toast(getString(R.string.base_tips_zip_code))
                return
            }
            if (mLibMineSAPhoneNumber.isNullOrEmpty()||mLibMineSAPhoneNumber.length>20) {
                toast(getString(R.string.base_tips_phone_number))
                return
            }
            launch {
                mMineModle.shoppingAddressAdd(
                    ShipAddressRequest(
                        nickName = mName,
                        streetAddress = mStreetAddress,
                        district = mLibMineSADistrict,
                        province = mLibMineSAProvince,
                        zipCode = mLibMineSAZipCode,
                        phoneNumber = mLibMineSAPhoneNumber
                    )
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@LibMineShippingAddressActivity)) {
                            it.data?.let {
                                id=it.id
                                LibMineSAAddLy.visibility = View.GONE
                                LibMineSACl.visibility = View.GONE
                                LibMineSAAddRl.visibility = View.VISIBLE
                                mBinding.nextBtn.visibility = View.GONE
                                LibMineSANameEd.text = (it.nickName)
                                LibMineSASA.text = (it.streetAddress)
                                LibMineSAP.text = (it.district)+","+(it.province)+" "+it.zipCode
                                LibMineSAZ.text =   it.phoneNumber
                                LibMineSAName.setText(it.nickName)
                                LibMineSAStreetAddress.setText(it.streetAddress)
                                LibMineSADistrict.setText(it.district)
                                LibMineSAProvince.setText(it.province)
                                LibMineSAZipCode.setText(it.zipCode.toString())
                                LibMineSAPhoneNumber.setText(it.phoneNumber)
                            }
                        }
                    }
            }


        }


    }

    private fun shoppingAddressUpdate() {
        mBinding?.run {
            var mName = LibMineSAName.text.toString()
            var mStreetAddress = LibMineSAStreetAddress.text.toString()
            var mLibMineSADistrict = LibMineSADistrict.text.toString()
            var mLibMineSAProvince = LibMineSAProvince.text.toString()
            var mLibMineSAZipCode = LibMineSAZipCode.text.toString()
            var mLibMineSAPhoneNumber = LibMineSAPhoneNumber.text.toString()
            if (mName.isNullOrEmpty()||mName.length>20) {
                toast(getString(R.string.base_tips_name))
                return
            }
            if (mStreetAddress.isNullOrEmpty()||mStreetAddress.length>100) {
                toast(getString(R.string.base_tips_street_address))
                return
            }
            if (mLibMineSADistrict.isNullOrEmpty()||mLibMineSADistrict.length>20) {
                toast(getString(R.string.base_tips_District))
                return
            }
            if (mLibMineSAProvince.isNullOrEmpty()||mLibMineSAProvince.length>20) {
                toast(getString(R.string.base_tips_province))
                return
            }
            if (mLibMineSAZipCode.isNullOrEmpty()||mLibMineSAZipCode.length>10) {
                toast(getString(R.string.base_tips_zip_code))
                return
            }
            if (mLibMineSAPhoneNumber.isNullOrEmpty()||mLibMineSAPhoneNumber.length>20) {
                toast(getString(R.string.base_tips_phone_number))
                return
            }

            launch {
                mMineModle.shoppingAddressUpdate(
                    ShipAddressRequest(
                        nickName = mName,
                        streetAddress = mStreetAddress,
                        district = mLibMineSADistrict,
                        province = mLibMineSAProvince,
                        zipCode = mLibMineSAZipCode,
                        phoneNumber = mLibMineSAPhoneNumber,
                        id = id
                    )
                )
                    .onStart {
                        mAppViewModel.showLoading()
                    }.catch {
                    }.onCompletion {
                        mAppViewModel.dismissLoading()
                    }.collectLatest {
                        if (it.isOk(this@LibMineShippingAddressActivity)) {
                            it.data?.let {
                                id = it.id
                                LibMineSAAddLy.visibility = View.GONE
                                LibMineSACl.visibility = View.GONE
                                LibMineSAAddRl.visibility = View.VISIBLE
                                mBinding.nextBtn.visibility = View.GONE
                                LibMineSANameEd.text = (it.nickName)
                                LibMineSASA.text = (it.streetAddress)
                                LibMineSAP.text = (it.district)+","+(it.province)+" "+it.zipCode
                                LibMineSAZ.text =   it.phoneNumber
                                LibMineSAName.setText(it.nickName)
                                LibMineSAStreetAddress.setText(it.streetAddress)
                                LibMineSADistrict.setText(it.district)
                                LibMineSAProvince.setText(it.province)
                                LibMineSAZipCode.setText(it.zipCode.toString())
                                LibMineSAPhoneNumber.setText(it.phoneNumber)
                            }
                        }
                    }
            }


        }


    }

}