package com.earth.angel.gift.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.earth.angel.R
import com.earth.angel.databinding.FragmentGiftmainBinding
import com.earth.angel.gift.ui.AddGroupActivity
import com.earth.angel.search.SearchActivity
import com.earth.libbase.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.search_layout.*


class GiftMainFragment : BaseFragment<FragmentGiftmainBinding>() {
    private var listTitle= listOf("Eco Gift Groups","Following")
    override fun getLayoutId(): Int = R.layout.fragment_giftmain

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        var fragments: ArrayList<Fragment> = ArrayList()
        fragments.add(FollowingFragment())
        fragments.add(FollowingFragment())
        mBinding?.run {
            handler=this@GiftMainFragment
            viewpager2.adapter = object : FragmentStateAdapter(this@GiftMainFragment) {

                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }

                override fun getItemCount(): Int {
                    return fragments?.size
                }

            }
            viewpager2.offscreenPageLimit = 3;

            TabLayoutMediator(tablayout, viewpager2 ,true
            ) { tab, position ->
                tab.text = listTitle[position]

            }.attach()
            tvSearch.setOnClickListener {
                SearchActivity.openSearchActivity(requireActivity())
            }

            ivAddGroup.setOnClickListener {
                AddGroupActivity.openAddGroupActivity(requireActivity())
            }
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {

        }
    }


}