package com.earth.libbase.dialog

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.earth.libbase.R
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.databinding.ActivityPointDialogBinding
import com.earth.libbase.entity.SingEntity


@Route(path = Constance.PointDialogActivityURL)
class PointDialogActivity : BaseActivity<ActivityPointDialogBinding>() {


    override fun getLayoutId(): Int = R.layout.activity_point_dialog

    override fun initActivity(savedInstanceState: Bundle?) {
        window.setLayout(
            ConstraintLayout.LayoutParams.FILL_PARENT,
            ConstraintLayout.LayoutParams.FILL_PARENT
        );
        mBinding.run {
            viewBank.setOnClickListener {
                finish()
                overridePendingTransition(0,0)
            }
            val intent = intent
            val dateBean: SingEntity =
                intent.getSerializableExtra("SIGN") as SingEntity
            dateBean?.run {
                tvAccumulative.text= " Accumulative login $signInCount day "
                initPoint(signInCount)
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0,0)
    }


    private fun initPoint(position: Int) {
        mBinding.run {
            when (position) {
                1 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                2 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                3 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setYesView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                4 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setYesView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setYesView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                5 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setYesView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setYesView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setYesView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                6 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setYesView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setYesView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setYesView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setYesView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                7 -> {
                    setYesView(ivDay1Yes,tvDay1,tvDay1Iv,rlDay1,1)
                    setYesView(ivDay2Yes,tvDay2,tvDay2Iv,rlDay2,2)
                    setYesView(ivDay3Yes,tvDay3,tvDay3Iv,rlDay3,3)
                    setYesView(ivDay4Yes,tvDay4,tvDay4Iv,rlDay4,4)
                    setYesView(ivDay5Yes,tvDay5,tvDay5Iv,rlDay5,5)
                    setYesView(ivDay6Yes,tvDay6,tvDay6Iv,rlDay6,6)
                    setYesView(ivDay7Yes,tvDay7,tvDay7Iv,rlDay7,7)
                }
                else -> {
                }
            }
        }
    }

    private fun setYesView(vie1: ImageView, view2: TextView, view3: ImageView, view4: RelativeLayout,position: Int){
        mBinding.run {
            vie1.visibility= View.VISIBLE
            view2.setTextColor(ContextCompat.getColor(this@PointDialogActivity, R.color.text_black))
            when (position) {
                1 -> {
                    view3.setImageResource(R.mipmap.point_10_yes)
                }
                2 -> {
                    view3.setImageResource(R.mipmap.point_10_yes)
                }
                3 -> {
                    view3.setImageResource(R.mipmap.point_20_yes)
                }
                4 -> {
                    view3.setImageResource(R.mipmap.point_30_yes)
                }
                5 -> {
                    view3.setImageResource(R.mipmap.point_40_yes)
                }
                6 -> {
                    view3.setImageResource(R.mipmap.point_40_yes)
                }
                7 -> {
                    view3.setImageResource(R.mipmap.point_50_yes)
                }
                else ->{

                }
            }
            view4.setBackgroundResource(R.drawable.base_biankuang_yes_8)
        }
    }
    private fun setView(vie1: ImageView, view2: TextView, view3: ImageView, view4: RelativeLayout,position: Int){
        mBinding.run {
            vie1.visibility= View.GONE
            view2.setTextColor(ContextCompat.getColor(this@PointDialogActivity, R.color.text_Unblack))
            when (position) {
                1 -> {
                    view3.setImageResource(R.mipmap.point_10)
                }
                2 -> {
                    view3.setImageResource(R.mipmap.point_10)
                }
                3 -> {
                    view3.setImageResource(R.mipmap.point_20)
                }
                4 -> {
                    view3.setImageResource(R.mipmap.point_30)
                }
                5 -> {
                    view3.setImageResource(R.mipmap.point_40)
                }
                6 -> {
                    view3.setImageResource(R.mipmap.point_40)
                }
                7 -> {
                    view3.setImageResource(R.mipmap.point_50)
                }
                else ->{

                }
            }
            view4.setBackgroundResource(R.drawable.base_biankuang_chatme_8)
        }
    }

}