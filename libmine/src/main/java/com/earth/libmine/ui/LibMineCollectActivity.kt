package com.earth.libmine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.util.BaseDateUtils
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityLibmineCollectBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.libmine_titlebar.*
import org.jetbrains.anko.toast

@Route(path = Constance.LibMineCollectActivityURL)
class LibMineCollectActivity : BaseActivity<ActivityLibmineCollectBinding>() {

    var listTitle = listOf("Products", "Environmentalists")

    override fun getLayoutId(): Int =R.layout.activity_libmine_collect

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            showActionBar()
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            tvLibMinTitleCenter.text=getString(R.string.base_head_Saved)
            ivLibMineRightTool.setImageResource(R.mipmap.base_fenlei)
            ivLibMineRightTool?.setOnClickListener {
                if( BaseDateUtils.isFastClick()){
                    ARouter.getInstance().build(Constance.ClassificationMainActivityURL)
                        .navigation()
                }
            }
            var fragments: ArrayList<Fragment> = ArrayList()
            fragments.add(LibMineSaveGiftFragment())
            fragments.add(LibMineSaveEnviFragment())
            LibMineCollectVp2.adapter = object : FragmentStateAdapter(this@LibMineCollectActivity) {
                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }
                override fun getItemCount(): Int {
                    return fragments?.size
                }
            }
            LibMineCollectVp2.isUserInputEnabled = false;
            LibMineCollectVp2.offscreenPageLimit = 1
            TabLayoutMediator(LibMineCollectTl, LibMineCollectVp2 ,true
            ) { tab, position ->
                tab.text = listTitle[position]
            }.attach()
            LibMineCollectTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if(tab?.text=="Products"){
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