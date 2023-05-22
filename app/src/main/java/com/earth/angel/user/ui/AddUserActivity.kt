package com.earth.angel.user.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.earth.angel.R
import com.earth.angel.databinding.ActivityAddUserBinding
import com.earth.angel.user.ui.fragment.ContactsFragment
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.dialog.LoadingDialog
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_add_user.*
import kotlinx.android.synthetic.main.include_top_bar.*
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddUserActivity : BaseActivity<ActivityAddUserBinding>() {
    private val mAppViewModel by viewModel<AppViewModel>()
    private val mLoadingDialog by lifecycleScope.inject<LoadingDialog>()

    var listTitle = listOf("Friend Requests", "Contacts","Friends of Friends")


    override fun getLayoutId(): Int =R.layout.activity_add_user

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.let {

            mAppViewModel.showLoadingProgress.observe(this, Observer {
                if (it) mLoadingDialog.showAllowStateLoss(
                    supportFragmentManager,
                    mLoadingDialog::class.simpleName!!
                )
                else mLoadingDialog.dismiss()
            })

            tvTitleCenter.text = getString(R.string.lab_Add_People_You_Know)
            var fragments: ArrayList<Fragment> = ArrayList()
           // fragments.add(UserFollowingFragment())
            fragments.add(ContactsFragment())
          //  fragments.add(FriendsofFriendsFragment())

            viewpagerWhoWant.adapter = object : FragmentStateAdapter(this@AddUserActivity) {
                override fun createFragment(position: Int): Fragment {
                    return fragments[position]
                }
                override fun getItemCount(): Int {
                    return fragments?.size
                }
            }

            viewpagerWhoWant.offscreenPageLimit = 1
            TabLayoutMediator(tablayoutWhoWant, viewpagerWhoWant ,true
            ) { tab, position ->
                tab.text = listTitle[position]
            }.attach()

        }
    }


}