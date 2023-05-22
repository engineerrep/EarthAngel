package com.earth.libhome.group

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libhome.R
import com.earth.libhome.databinding.ActivityGroupcreateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

@Route(path = Constance.GroupCreateActivityURL)
class GroupCreateActivity : BaseActivity<ActivityGroupcreateBinding>() {

    private val mGroupModle by viewModel<GroupModle>()


    override fun getLayoutId(): Int =R.layout.activity_groupcreate

    override fun initActivity(savedInstanceState: Bundle?) {
        fitSystemBar()
    }
}