package com.earth.libbase.dialog

import android.os.Bundle
import android.view.View
import com.earth.libbase.R
import com.earth.libbase.base.BaseDialogFragment
import com.earth.libbase.databinding.BaseDialogCommonBinding
import com.earth.libbase.databinding.BaseDialogLocationBinding
import com.earth.libbase.databinding.BaseDialogPointBinding
import com.earth.libbase.databinding.BaseDialogserviceLocationBinding
import com.earth.libbase.network.request.GroupNameRequest
import kotlinx.android.synthetic.main.base_dialog_point.*


class LocationServiceDialog(
    var onBlockClick: () -> Unit = {},
    var onDissMissClick: () -> Unit = {}
) :
    BaseDialogFragment<BaseDialogserviceLocationBinding>() {


    override fun layoutId() = R.layout.base_dialogservice_location

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.apply {

            dialogCancel.setOnClickListener {
                onDissMissClick()
                dismiss()
            }
            dialogYes.setOnClickListener {
                onBlockClick()
                dismiss()
            }
        }


    }


}