package com.earth.angel.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.earth.angel.R
import com.earth.angel.base.EarthAngelApp
import com.earth.angel.databinding.FragmentMineBinding
import com.earth.angel.event.MessageEvent
import com.earth.angel.mine.ui.*
import com.earth.angel.user.UserModel
import com.earth.angel.user.ui.MyFriendsActivity
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.angel.util.ScreenUtil
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MineFragment : BaseActivity<FragmentMineBinding>() {

    private val mMineModel by viewModel<MineModel>()
    private val userModle by viewModel<UserModel>()
    private var headUrl: String?=null

    override fun getLayoutId(): Int = R.layout.fragment_mine
    override fun initActivity(savedInstanceState: Bundle?){
        mBinding?.run {
            handler = this@MineFragment
            hideActionBar()
            val lp: ConstraintLayout.LayoutParams = tvLeftTool.layoutParams as ConstraintLayout.LayoutParams
            lp.setMargins(0, ScreenUtil.getActivityMessageHeight(this@MineFragment), 0, 0)
            tvLeftTool.layoutParams = lp
            EventBus.getDefault().register(this@MineFragment)
            llUser.setOnClickListener {
                //EditProfileActivity.openEditProfileActivity(this@MineFragment)
                if (DateUtils.isFastClick()){
                    jumpV2()
                }
            }
            tvLeftTool.setOnClickListener {
                finish()
            }
            ivSet.setOnClickListener {
                SetActivity.openSetActivity(this@MineFragment)
            }
            ivHead.setOnClickListener {
                jumpV2()
             /*   headUrl?.let {
                    val intent = Intent(this@MineFragment, GiftPhotoActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(GiftPhotoActivity.IMG, it)
                    this@MineFragment.startActivity(intent)
                }*/
            }
            ctMyGiftMine.setOnClickListener {
                MyEcoGiftActivity.openMyEcoGiftActivity(this@MineFragment)
            }
            ctPhone.setOnClickListener {
                startActivity.launch(Intent(this@MineFragment, VerifyPhoneActivity::class.java))

            }
            ctProfileAddPeople.setOnClickListener {
                startActivity.launch(Intent(this@MineFragment, MyFriendsActivity::class.java))

            }
            EarthAngelApp.myProfileEntity?.let {
                tvName.text = it.nickName
                tvNameID.text = "ID : " +it.id
                it.headImgUrl?.let {
                    headUrl=it
                    Glide.with(this@MineFragment)
                        .load(it)
                        .into(ivHead)
                }
                tvTitlePhone.text=it.phoneNumber
            }
        }

        friendList()
    }


    private fun select() {
        launch {
            mMineModel.selectMine().catch { }.collectLatest {
                if (it.isOk(this@MineFragment)) {
                    it.data?.let { mine ->
                            mBinding?.run {
                                tvName.text = mine.nickName
                                tvNameID.text = "ID : " +mine.id
                                mine.headImgUrl?.let {
                                    headUrl=it
                                    Glide.with(this@MineFragment)
                                        .load(it)
                                        .into(ivHead)
                                }
                                tvTitlePhone.text=mine.phoneNumber
                            }
                        PreferencesHelper.saveMyProfileCache(this@MineFragment, Gson().toJson(mine))
                    }
                }
            }
        }
    }

 /*   private fun getNum() {
        launch {
            mMineModel.unreadSize()
                .catch {
                }.collectLatest {
                    if (it.isOk(this@MineFragment)) {

                        if (it.data.isNullOrEmpty()) {
                            mBinding?.tvNum?.visibility = View.GONE
                        } else {
                            if (it.data == "0") {
                                mBinding?.tvNum?.visibility = View.GONE
                            } else {
                                mBinding?.tvNum?.visibility = View.VISIBLE
                                mBinding?.tvNum?.text = it.data.toString()
                            }
                        }
                    }
                }
        }
    }*/





    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            when (it.resultCode) {
                100 -> {
                    select()
                }
                101 -> {
                 //   getNum()
                }
            }
        }

    fun jumpV2() {
        DataReportUtils.getInstance().report("Me_photo")
        startActivity.launch(Intent(this@MineFragment, EditProfileActivity::class.java))
    }



    override fun onRestart() {
        super.onRestart()
        friendList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: MessageEvent) {
        friendList()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MineFragment)
    }

    private fun friendList() {
        launch {
            userModle.following(1, 100).catch { }
                .collect {
                    if (it.data?.list.isNullOrEmpty()) {
                        mBinding?.tvNumFriend?.visibility = View.GONE
                    } else {
                        mBinding?.tvNumFriend?.visibility = View.VISIBLE
                        mBinding?.tvNumFriend?.text = it.data?.list?.size.toString()
                    }
                }
        }
    }
}