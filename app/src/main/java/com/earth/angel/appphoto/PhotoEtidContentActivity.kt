package com.earth.angel.appphoto

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.ActivityPhotoetidContentBinding
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import kotlinx.android.synthetic.main.include_top_bar.*
import org.jetbrains.anko.toast


@Route(path = Constance.PhotoEtidContentActivityURL)
class PhotoEtidContentActivity : BaseActivity<ActivityPhotoetidContentBinding>() {
    @Autowired(name = "TYPE")
    @JvmField
    var typeString: String? = null
    @Autowired(name = "Content")
    @JvmField
    var mContent: String? = null

    private var maxText = 500//最多显示的字数


    override fun getLayoutId(): Int = R.layout.activity_photoetid_content

    override fun initActivity(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this@PhotoEtidContentActivity)
        showActionBar()
        mBinding?.run {
            tvLeftTool.setOnClickListener {
                finish()
            }
            mContent?.let {
                PhotoEtidContentEt.setText(it)
                if(it=="To fill"){
                    PhotoEtidContentEt.setText("")
                }
            }
            typeString?.run {
                when(typeString){
                     "Title" -> {
                         PhotoEtidContentTitle.text="Name your product so everyone knows what it is."
                         PhotoEtidContentEt.hint="Name your product..."
                         tvAll.setText(R.string.text_100)
                         maxText=100
                         mContent?.let {
                             if(it.length<=100){
                                 tvNum.text = it?.length.toString()
                             }
                         }
                     }
                     "Details" -> {
                         PhotoEtidContentTitle.text="You can add more details about your product..."
                         PhotoEtidContentEt.hint="Add more details..."
                         tvAll.setText(R.string.text_500)
                         maxText=500
                         mContent?.let {
                             if(it.length<=500){
                                 tvNum.text = it?.length.toString()
                             }
                         }
                     }
                    "Group" -> {
                        PhotoEtidContentTitle.text="Add community info"
                        PhotoEtidContentEt.hint="Say something"
                        tvAll.setText(R.string.text_500)
                        maxText=500
                        mContent?.let {
                            if(it.length<=500){
                                tvNum.text = it?.length.toString()
                            }
                        }
                    }
                    else -> {}
                }
            }
            PhotoEtidContentEt?.run {
                var wordNum: CharSequence? = null
                var selectionStart: Int
                var selectionEnd: Int
                doOnTextChanged { text, _, _, _ ->
                    wordNum = text!!
                }
                doAfterTextChanged {
                    maxText - it!!.length
                    tvNum.text = text?.length.toString()
                    selectionStart = getSelectionStart()
                    selectionEnd = getSelectionEnd()
                    if (wordNum!!.length > maxText) {
                        it.delete(selectionStart - 1, selectionEnd)
                        val tempSelection = selectionEnd
                        setSelection(tempSelection)
                    }
                }
            }

            PhotoEtidContentDone.setOnClickListener {
                val  contentStr=PhotoEtidContentEt.text.toString().trim()
                if(contentStr.isNullOrEmpty()){
                    if(typeString=="Title"){
                        toast("Please name your product.")
                        return@setOnClickListener
                    }
                    if(typeString=="Group"){
                        toast("Add community info.")
                        return@setOnClickListener
                    }
                }
                val intent = Intent()
                intent.putExtra("content", contentStr)
                setResult(1, intent)
                finish()

            }

        }

    }


}