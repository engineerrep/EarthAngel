package com.earth.angel.mine.ui

import android.os.Bundle
import com.earth.angel.R
import com.earth.angel.databinding.ActivityWhoWantBinding
import com.earth.libbase.base.BaseActivity
import kotlinx.android.synthetic.main.include_top_bar.*

class WhoWantActivity : BaseActivity<ActivityWhoWantBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_who_want



    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()

        mBinding?.let {
            tvTitleCenter.text=getString(R.string.lab_who_want)

        }


    }


}