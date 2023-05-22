package com.earth.angel.gift.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.earth.angel.R
import com.earth.angel.databinding.ActivityGiftAddMessageBinding
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.util.DataReportUtils
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.GiftEntity
import com.earth.libbase.network.ReleasedMessageRequest
import com.earth.libbase.util.PreferencesHelper
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiftAddMessageActivity : BaseActivity<ActivityGiftAddMessageBinding>() {

    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    var giftEntity: GiftEntity? = null

    override fun getLayoutId(): Int = R.layout.activity_gift_add_message

    override fun initActivity(savedInstanceState: Bundle?) {
        showActionBar()
        mBinding?.run {
            giftEntity = intent.getSerializableExtra("giftEntity") as GiftEntity?
            tvTitleCenter.text = "Tell Owner You Want This"
            handler = this@GiftAddMessageActivity
            btSaveShare.setOnClickListener {
                messageRecord()
            }

            PreferencesHelper.getMessageName(this@GiftAddMessageActivity)?.let {
                etName.setText(it)
            }
            PreferencesHelper.getMessageInfo(this@GiftAddMessageActivity)?.let {
                etContactInfo.setText(it)
            }
        }
    }

    private fun messageRecord() {
        mBinding?.run {
            val etNameS = etName.text.toString().trim()
            val contactDetails = etContactInfo.text.toString().trim()
            val msg = etMessage.text.toString().trim()
            if (etNameS.isNullOrEmpty() || contactDetails.isNullOrEmpty() || msg.isNullOrEmpty()) {
                return
            }

            DataReportUtils.getInstance().report(" Enter_message")
            DataReportUtils.getInstance().report(" Enter_username")
            DataReportUtils.getInstance().report(" Enter_contact_info")
            DataReportUtils.getInstance().report(" Submit_message")

            PreferencesHelper.saveMessageName(this@GiftAddMessageActivity, etNameS)
            PreferencesHelper.saveMessageInfo(this@GiftAddMessageActivity, contactDetails)

            giftEntity?.let {
                launch {
                    mEcoGiftGorupsModel.messageRecord(
                        ReleasedMessageRequest(
                            contactDetails,
                            msg,
                            etNameS,
                            it.id.toLong()
                        )
                    )
                        .catch { }.collectLatest {
                            if (it.isOk(this@GiftAddMessageActivity)) {
                                finish()
                            }

                        }
                }
            }

        }

    }

    companion object {
        fun openGiftAddMessageActivity(context: Context?, giftEntity: GiftEntity) {
            val intent = Intent(context, GiftAddMessageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("giftEntity", giftEntity)
            context?.startActivity(intent)
        }
    }
}