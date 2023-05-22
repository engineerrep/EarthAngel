package com.earth.angel.login

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.appsflyer.AppsFlyerLib
import com.earth.angel.MainPhotoActivity
import com.earth.angel.R
import com.earth.angel.databinding.ActivityLoginBinding
import com.earth.angel.dialog.DialogAgreement
import com.earth.angel.dialog.DialogAgreementInterface
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.Config.sharerUserId
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.request.LoginRequest
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    private var phoneCode: String? = "86"
    private val mLoginModel by viewModel<LoginModel>()
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()
    private val maxPhone = 20//最多显示的字数
    private var mDialogAgreement: DialogAgreement? = null

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initActivity(savedInstanceState: Bundle?) {
        DataReportUtils.getInstance().report("Login");
        showActionBar()
        AppsFlyerLib.getInstance().start(this)
        AppsFlyerLib.getInstance().setAppInviteOneLink("HSGl")
        mBinding.run {
            handler = this@LoginActivity
            tvTitleCenter.text="LOGIN"
            mDialogAgreement = DialogAgreement(this@LoginActivity)
                .setOnInterface(object : DialogAgreementInterface {
                    override fun onSuccess() {
                        PreferencesHelper.saveAgreement(this@LoginActivity,true)
                    }
                    override fun onFail() {
                            finish()
                    }
                })
            mDialogAgreement?.create()
            code.text = "+86"
            llCode.setOnClickListener {
               // startActivityForResult(Intent(this@LoginActivity, PickActivity::class.java), 111)
            }
            btLogin.setOnClickListener {
                login()
            }
            var phone= PreferencesHelper.getPhone(this@LoginActivity).toString()
            if (!phone.isNullOrEmpty()) {
                etPhone.setText(phone)
                etPhone.setSelection(phone.length)
            }


            etPhone?.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    wordNum = text!!
                }
                doAfterTextChanged {
                    maxPhone - it!!.length
                    selectionStart = getSelectionStart()
                    selectionEnd = getSelectionEnd()
                    if (wordNum!!.length > maxPhone) {
                        it.delete(selectionStart - 1, selectionEnd)
                        val tempSelection = selectionEnd
                        setSelection(tempSelection)
                    }
                }
            }
        }
        mAppViewModel?.showLoadingProgress.observe(this, Observer {
            if (it) mLoadingDialog?.showAllowStateLoss(
                supportFragmentManager,
                mLoadingDialog::class.simpleName!!
            )
            else mLoadingDialog?.dismiss()
        })

    }

    private fun login() {
        DataReportUtils.getInstance().report("Click_start")
        if (DateUtils.isFastClick()){
            if (phoneCode.isNullOrEmpty()) {
                toast(getString(R.string.tips_code_request))
                return
            }
            var phone = etPhone.text.toString().trim()
            if (phone.isNullOrEmpty()) {
                toast(getString(R.string.tips_number_request))
                return
            }
            var bean: LoginRequest?=null
            if(sharerUserId!=""){
                bean = LoginRequest(phoneCode, phone,sharerUserId = sharerUserId.toLong())
            }else{
                bean = LoginRequest(phoneCode, phone)
            }
            launch {
                mLoginModel.login(bean).onStart {
                    mAppViewModel.showLoading()
                }.catch {
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    if (it.isOk(this@LoginActivity)) {
                        PreferencesHelper.savePhone(this@LoginActivity, phone)

                        it.data?.firstAuth?.let { firstBoolean ->
                            PreferencesHelper.saveMyProfileCache(
                                this@LoginActivity,
                                Gson().toJson(it.data)
                            )
                            if(!firstBoolean){
                                it.data?.token?.let {
                                    PreferencesHelper.saveToken(this@LoginActivity, it)
                                    val intent = Intent(this@LoginActivity, MainPhotoActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            }else{
                                it.data?.token?.let {
                                    PreferencesHelper.saveToken(this@LoginActivity, it)
                                    val intent = Intent(this@LoginActivity, UserNameActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                            }
                            PreferencesHelper.saveFirstAuth(this@LoginActivity, firstBoolean)
                        }
                        it.data?.imSig?.let { imToken ->
                            PreferencesHelper.saveImtoken(this@LoginActivity, imToken)
                        }
                        it.data?.uid?.let { imUsid ->
                            PreferencesHelper.saveUserId(this@LoginActivity, imUsid.toString())
                        }
                    } else {
                        toast(it.msg)
                    }
                }
            }
        }
    }

    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        if(hasFocus){
            if (PreferencesHelper.getAgreement(this@LoginActivity)==false) {
                mDialogAgreement?.show()
            }
        }
        super.onWindowFocusChanged(hasFocus)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 111 && resultCode == RESULT_OK) {

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}