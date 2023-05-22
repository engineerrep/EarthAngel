package com.earth.angel.mine.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.ActivitySetBinding
import com.earth.angel.login.LoginActivity
import com.earth.angel.search.ShakeFragment
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.view.webview.Html5Activity
import com.earth.angel.view.webview.Html5Activity.WEB_VIEW_TITLE
import com.earth.angel.view.webview.Html5Activity.WEB_VIEW_URL
import com.earth.libbase.Config
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.PreferencesHelper
import com.earth.libbase.util.ShareUtil
import kotlinx.android.synthetic.main.include_top_bar.*

class SetActivity : BaseActivity<ActivitySetBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_set

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            handler = this@SetActivity
            tvTitleCenter.text = getString(R.string.lab_Settings)
            btLogin.setOnClickListener {
                ActivityStackManager.finishAll()
                EarthAngelApp.myProfileEntity=null
                PreferencesHelper.clear(this@SetActivity)
                val intent = Intent(this@SetActivity, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            getVersionName(this@SetActivity).let {
                tvVersionName.text= "V $it"
            }
        }
    }
    fun getVersionName(context: Context): String? {
        // 包管理者
        val mg: PackageManager = context.packageManager
        try {
            // getPackageInfo(packageName 包名, flags 标志位（表示要获取什么数据）);
            // 0表示获取基本数据
            val info: PackageInfo = mg.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    fun goActivity(view: View) {
        if (DateUtils.isFastClick()) {
            when (view.id) {
                R.id.lyMyGiftMine -> {
                    // 发邮件
                    ShareUtil.email(this@SetActivity)

                }
                R.id.lyMessageMine -> {
                    DataReportUtils.getInstance().report("Click_Privacy");
                    //Privacy Policy
                    startActivity(
                        Intent(this@SetActivity, Html5Activity::class.java)
                            .putExtra(
                                WEB_VIEW_URL,
                                Config.Privacy
                            )
                            .putExtra(WEB_VIEW_TITLE, getString(R.string.lab_Privacy))
                    )
                }
                R.id.lyiSendMine -> {
                    DataReportUtils.getInstance().report("Click_Terms");
                    // Terms of Use
                    startActivity(
                        Intent(this@SetActivity, Html5Activity::class.java)
                            .putExtra(
                                WEB_VIEW_URL,
                                Config.Terms
                            )
                            .putExtra(WEB_VIEW_TITLE, getString(R.string.lab_Terms))
                    )
                }
                R.id.lyConsideringMine -> {
                    // About Us
                    DataReportUtils.getInstance().report("About_us");

                    startActivity(
                        Intent(this@SetActivity, Html5Activity::class.java)
                            .putExtra(
                                WEB_VIEW_URL,
                                Config.About
                            )
                            .putExtra(WEB_VIEW_TITLE, getString(R.string.lab_About))
                    )
                }
                R.id.lyShake -> {
                    startActivity(
                        Intent(this@SetActivity, ShakeFragment::class.java)
                    )
                }

            }
        }
    }

    companion object {
        fun openSetActivity(context: Context?) {
            val intent = Intent(context, SetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }
}