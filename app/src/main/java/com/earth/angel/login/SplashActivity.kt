package com.earth.angel.login

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.ActivitySplashBinding
import com.earth.angel.regist.RegistActivity
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.base.delayLaunch
import com.earth.libbase.util.PreferencesHelper

class SplashActivity : BaseActivity<ActivitySplashBinding>() {


    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        if (!PreferencesHelper.getToken(this@SplashActivity).toString().isNullOrEmpty()) {

            delayLaunch(3000) {
                block = {
                    ARouter.getInstance().build(Constance.EarthAngelMainActivityURL).navigation()
                    finish()
                }
            }
        } else {
            delayLaunch(3000) {
                block = {

                    if (PreferencesHelper.getWelcome(this@SplashActivity) == false) {
                        // 第一次进
                        startActivity(Intent(this@SplashActivity, IntroductoryActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, RegistActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}