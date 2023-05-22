package com.earth.libbase.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.earth.libbase.R
import com.earth.libbase.util.ActivityStackManager
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libbase.view.AndroidBottomSoftBar
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


abstract class BaseActivity<VDB : ViewDataBinding> : AppCompatActivity(),
    CoroutineScope by MainScope() {

    var bea: Unit? = null
    protected val mBinding: VDB by lazy {
        DataBindingUtil.setContentView(this, getLayoutId()) as VDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewBefore()
//        if (needTransparentStatus()) transparentStatusBar()
        mBinding.lifecycleOwner = this
        ActivityStackManager.addActivity(this)
        initActivity(savedInstanceState)
        ClassicsHeader.REFRESH_HEADER_PULLING = getString(R.string.Pull_down_to_refresh)
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.Refreshing)
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.Release_refresh_now)
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.Refresh_complete)
        ClassicsHeader.REFRESH_HEADER_UPDATE = "'Last update' M-d HH:mm"
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.loading)
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.Loading_complete)
    }

    open fun setContentViewBefore() {


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (needFitDarkMode())
            when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
    }

    open fun fishActivity() {

    }

    override fun onDestroy() {
        super.onDestroy()
        //ActivityStackManager.removeActivity(this)
        cancel()
        mBinding.unbind()
    }


    open fun transparentStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.hide()
    }

    open fun hideActionBar() {
        window.statusBarColor = Color.TRANSPARENT
        hideStatusBar(window)
    }
    // 最上面的控件距ActionBar屏幕高度
    open fun setViewActionBarHight(view: View) {
        val lp: ConstraintLayout.LayoutParams =
            view.layoutParams as ConstraintLayout.LayoutParams
        lp.setMargins(
            0,
            BaseScreenUtil.getActivityMessageHeight(),
            0,
            0
        )
        view.layoutParams = lp
    }

    fun fitSystemBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return
        val window = window
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT

    }
    open fun showActionBar() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    private fun hideStatusBar(window: Window?) {
        if (window != null && window.decorView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val view = window.decorView
                val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                if (view != null && uiOptions != view.systemUiVisibility) {
                    view.systemUiVisibility = uiOptions
                }
            }
        }
    }

    abstract fun getLayoutId(): Int

    abstract fun initActivity(savedInstanceState: Bundle?)

    protected open fun needTransparentStatus(): Boolean = true

    protected open fun needFitDarkMode(): Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (this@BaseActivity.currentFocus != null) {
                if (this@BaseActivity.currentFocus!!.windowToken != null) {
                    imm.hideSoftInputFromWindow(
                        this@BaseActivity.currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        }
        return super.onTouchEvent(event)
    }
}