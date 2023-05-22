package com.earth.libmine.record

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.BaseApplication
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.RecordEntity
import com.earth.libbase.util.BaseDateUtils
import com.earth.libmine.R
import com.earth.libmine.databinding.ActivityRecordDetailsBinding
import kotlinx.android.synthetic.main.libmine_titlebar.*
import org.jetbrains.anko.toast

@Route(path = Constance.RecordDetailsActivityURL)
class RecordDetailsActivity : BaseActivity<ActivityRecordDetailsBinding>() {
    @Autowired(name = "userId")
    @JvmField
    var userId: String? = null
    private var mAddress = ""

    override fun getLayoutId(): Int = R.layout.activity_record_details

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        ARouter.getInstance().inject(this@RecordDetailsActivity)

        var mRecordEntity: RecordEntity =
            intent.getSerializableExtra("RECORD") as RecordEntity

        mBinding?.run {
            tvLibMineLeftTool.setOnClickListener {
                finish()
            }
            mRecordEntity?.run {
                tvlibHomeGift.text = itemTitle
                pictureResources?.let {
                    if (it.size > 0) {
                        Glide.with(this@RecordDetailsActivity)
                            .load(it[0])
                            .placeholder(R.mipmap.base_comm_img)
                            .into(ivlibHomeGiftImage!!)
                    }
                }
                score?.let {
                    tvLibHomeTree.text = it.toString()
                }

                nickName?.let {
                    tvLibMineOrder.text = "User:$it"
                }
                createTime?.let {
                    tvLibMineOrderDate.text = "Date:" + BaseDateUtils.getPointTime(it)
                }

                mAddress = streetAddress + "\n" +
                        district + "\n" +
                        province + "\n" +
                        "(" + zipCode + ")" + phoneNumber
                tvLibMineAddress?.text = mAddress
            }
            tvLibMineAddressCopy.setOnClickListener {
                val clipboard = BaseApplication.instance
                    .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                if (clipboard == null || mAddress == null) {
                    return@setOnClickListener
                }
                val clip = ClipData.newPlainText("message", mAddress)
                clipboard!!.setPrimaryClip(clip)
                toast("Copy Success")
            }
            ivLibmineContent.setOnClickListener {
                if (BaseDateUtils.isFastClick()) {
                    userId?.let {
                        ARouter.getInstance().build(Constance.HomeGiftDetailsActivityURL)
                            .withString("id", mRecordEntity.releaseId)
                            .withString("userid", userId)
                            .navigation()
                    }
                }
            }
        }
    }
}