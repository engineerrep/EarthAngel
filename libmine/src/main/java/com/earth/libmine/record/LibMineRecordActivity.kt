package com.earth.libmine.record

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityLibmineRecordBinding
import com.earth.libmine.ui.LibMineSaveEnviFragment
import com.earth.libmine.ui.LibMineSaveGiftFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.libmine_titlebar.*

@Route(path = Constance.LibmineRecordActivityURL)
class LibMineRecordActivity : BaseActivity<ActivityLibmineRecordBinding>() {

    var listTitle = listOf("I Got", "I Send")

    override fun getLayoutId(): Int =R.layout.activity_libmine_record

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            showActionBar()
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text=getString(R.string.label_Record)
            var fragments: ArrayList<Fragment> = ArrayList()
            fragments.add(LibMineIgotFragment())
            fragments.add(LibMineISendFragment())
            LibMineRecordVp2.adapter = object : FragmentStateAdapter(this@LibMineRecordActivity) {
                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }
                override fun getItemCount(): Int {
                    return fragments?.size
                }
            }

          //  LibMineRecordVp2.isUserInputEnabled = false;
            LibMineRecordVp2.offscreenPageLimit = 1
            TabLayoutMediator(LibMineRecordTl, LibMineRecordVp2 ,true
            ) { tab, position ->
                tab.text = listTitle[position]
            }.attach()
            LibMineRecordTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab?.text=="I Got"){
                        view1.visibility= View.VISIBLE
                        view2.visibility= View.INVISIBLE
                    }else{
                        view1.visibility= View.INVISIBLE
                        view2.visibility= View.VISIBLE
                    }
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }
}