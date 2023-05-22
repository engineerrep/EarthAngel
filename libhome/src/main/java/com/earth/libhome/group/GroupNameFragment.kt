package com.earth.libhome.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.BaseFragment
import com.earth.libbase.util.BaseDateUtils
import com.earth.libbase.util.BaseScreenUtil
import com.earth.libhome.R
import com.earth.libhome.databinding.FragmentGroupnameBinding
import org.jetbrains.anko.dip


class GroupNameFragment : BaseFragment<FragmentGroupnameBinding>() {


    private val mGroupModle: GroupModle by activityViewModels()

    override fun getLayoutId(): Int = R.layout.fragment_groupname

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding?.run {
            handler = this@GroupNameFragment
            LibCreateDone.isEnabled = false
            setViewActionBarHight(tvLibHomeLeftTool)
            tvLibHomeLeftTool?.setOnClickListener {
                activity?.finish()
            }
            LibMineEtNameFirstClosed?.setOnClickListener {
                LibMineEtNameFirst.setText("")
            }
            LibCreateDone.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    mGroupModle.setName(LibMineEtNameFirst.text.toString().trim())
                    findNavController().navigate(R.id.groupMapFragment)
                }
            }
            LibMineEtNameFirst?.run {
                doAfterTextChanged {
                    checkCanUpdate()
                }
            }
        }
    }

    private fun checkCanUpdate() {
        var strName = mBinding?.LibMineEtNameFirst?.text.toString().trim()

        if (strName.isNullOrEmpty()) {
            mBinding?.LibMineEtNameFirstClosed?.visibility = View.GONE
        } else {
            mBinding?.LibMineEtNameFirstClosed?.visibility = View.VISIBLE
        }
        mBinding?.LibCreateDone?.isEnabled = when {
            mBinding?.LibCreateDone?.text.isNullOrEmpty() -> {
                false
            }
            strName.isNullOrEmpty() -> {
                false
            }
            strName.length > 20 -> {
                false
            }
            else -> {
                true
            }
        }
    }

}