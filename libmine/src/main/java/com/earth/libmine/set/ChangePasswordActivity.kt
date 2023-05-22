package com.earth.libmine.set

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityChangePasswordBinding
@Route(path = Constance.ChangePasswordActivityURL)
class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {


    override fun getLayoutId(): Int =R.layout.activity_change_password

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {

        }
    }
}