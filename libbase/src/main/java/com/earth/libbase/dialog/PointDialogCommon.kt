package com.earth.libbase.dialog

import android.os.Bundle
import android.view.View
import com.earth.libbase.R
import com.earth.libbase.base.BaseDialogFragment
import com.earth.libbase.databinding.BaseDialogCommonBinding
import com.earth.libbase.databinding.BaseDialogPointBinding
import kotlinx.android.synthetic.main.base_dialog_point.*


class PointDialogCommon(
    var content: String?=null,
    var onBlockClick: () -> Unit = {},
    var onDissMissClick: () -> Unit = {}
) :
    BaseDialogFragment<BaseDialogPointBinding>() {


    override fun layoutId() = R.layout.base_dialog_point

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.apply {


            nextBtn.setOnClickListener {
                onDissMissClick()
                dismiss()
            }
            if(content.isNullOrEmpty()){
                dialogTv.visibility=View.GONE
            }else{
                dialogTv.visibility=View.VISIBLE
                dialogTv.text=content

            }

        }


    }


}