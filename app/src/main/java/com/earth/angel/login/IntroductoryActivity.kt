package com.earth.angel.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.earth.angel.R
import com.earth.angel.databinding.ActivityIntroducttoryBinding
import com.earth.angel.login.adapter.GuidePageAdapter
import com.earth.angel.regist.RegistActivity
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.util.PreferencesHelper


class IntroductoryActivity : BaseActivity<ActivityIntroducttoryBinding>() {

    private var mAdapter: GuidePageAdapter? = null
    private var list: ArrayList<View>? = ArrayList()

    override fun getLayoutId(): Int = R.layout.activity_introducttory

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding.run {
            list?.add(getView(R.layout.welcome_page1))
            list?.add(getView(R.layout.welcome_page2))
            list?.add(getView(R.layout.welcome_page3))
            mAdapter = GuidePageAdapter(list!!)
            viewPager2.adapter = mAdapter

            list?.get(2)?.findViewById<LinearLayout>(R.id.LibStart)?.setOnClickListener {
                PreferencesHelper.saveWelcome(this@IntroductoryActivity,true)
                startActivity(Intent(this@IntroductoryActivity, RegistActivity::class.java))
                finish()
            }
        }

    }


    private fun getView(resId: Int): View {
        return LayoutInflater.from(this).inflate(resId, null)
    }
}