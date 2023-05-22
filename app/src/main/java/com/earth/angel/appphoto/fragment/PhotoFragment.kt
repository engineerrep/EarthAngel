package com.earth.angel.appphoto.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.earth.angel.R
import com.earth.angel.appphoto.PhotoEditActivity
import com.earth.angel.databinding.FragmentPhotoBinding
import com.earth.angel.event.ShareSuccessEvent
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.base.BaseFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PhotoFragment : BaseFragment<FragmentPhotoBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_photo

    private val mAppViewModel: AppViewModel by activityViewModels()

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding?.run {
            handler = this@PhotoFragment
            ivEditPhoto.setOnClickListener {
                if (DateUtils.isFastClick()){
                    DataReportUtils.getInstance().report("Click_post")
                    Log.e("11","11")
                    PhotoEditActivity.openPhotoEditActivity(requireActivity(), true)
                }

            }

            llPost.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    DataReportUtils.getInstance().report("Click_post")
                    PhotoEditActivity.openPhotoEditActivity(requireActivity(), true)
                    //    ShareUtil.share(requireContext())
                }

            }
            llShare.setOnClickListener {
                if (DateUtils.isFastClick()) {
                    DataReportUtils.getInstance().report("Click_post")
             //       mAppViewModel?.showLoading()
              //      ShareUtil.share(requireContext())
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventEvent(event: ShareSuccessEvent) {
        mAppViewModel?.dismissLoading()

    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            EventBus.getDefault().register(this)

        }else{
            EventBus.getDefault().unregister(this)

        }
    }
}