package com.earth.angel.view

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.databinding.ActivityGiftPhotoBinding
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.entity.GiftEntity


@Route(path = Constance.GiftPhotoActivity)
class GiftPhotoActivity : BaseActivity<ActivityGiftPhotoBinding>() {
    private var imgUrl: String? = null
    private var snapHelper: PagerSnapHelper? = null
    private var lastPosition = 0 //记录recyclerView最后一次角标位置，用于判断是否转换了item
    companion object {
        const val IMG = "IMAGE"
        const val IMAGE_LIST = "IMAGE_LIST"
    }

    private var position: Int? = 0
    private var mPhotoEditAdapter: PhotoEditAdapter? = null
    private var listPicTureEntity: ArrayList<String> = ArrayList()
    private var giftEntity: GiftEntity? = null
    private var layoutPager: LinearLayoutManager? = null
    private var uriStr: ArrayList<String>?  = null

    override fun getLayoutId(): Int = R.layout.activity_gift_photo
    override fun setContentViewBefore() {
        super.setContentViewBefore()
        //去除标题栏
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //去除状态栏
        //去除状态栏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

    }

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            ARouter.getInstance().inject(this)
            layoutPager = LinearLayoutManager(this@GiftPhotoActivity)
            layoutPager?.orientation = LinearLayoutManager.HORIZONTAL
            rlv.layoutManager = layoutPager
            mPhotoEditAdapter = PhotoEditAdapter(listPicTureEntity)
            rlv.adapter = mPhotoEditAdapter
            photoViewBack.setOnClickListener {
                finish()
            }
            position = intent.getIntExtra("position", 0)
            uriStr = intent.getStringArrayListExtra("ImgList") as ArrayList<String>
            uriStr?.run {
                listPicTureEntity.addAll(this)
                mPhotoEditAdapter?.notifyDataSetChanged()
                layoutPager?.scrollToPosition(position!!)
                tvNum.text =
                    ((position!! + 1).toString() + "/" + listPicTureEntity.size.toString()).toString()
            }
            imgUrl = intent.getStringExtra(GiftPhotoActivity.IMG)
            imgUrl?.let {
                listPicTureEntity.add(it)
                mPhotoEditAdapter?.notifyDataSetChanged()
                tvNum.text = "/" + listPicTureEntity.size.toString()
            }
            giftEntity = intent.getSerializableExtra(GiftPhotoActivity.IMAGE_LIST) as GiftEntity?
            giftEntity?.pictureResources?.let {
                for (item in it) {
                    listPicTureEntity.add(item.pictureUrl)
                }
                mPhotoEditAdapter?.notifyDataSetChanged()


                tvNum.text = "/" + listPicTureEntity.size.toString()
            }
            snapHelper = PagerSnapHelper()
            snapHelper?.attachToRecyclerView(rlv)
            rlv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val view: View = snapHelper?.findSnapView(layoutPager) ?: return
                    val position: Int = layoutPager!!.getPosition(view)
                    if (lastPosition == position) {
                        return
                    }
                    lastPosition = position
                    tvNum.text =
                        ((lastPosition + 1).toString() + "/" + listPicTureEntity.size.toString()).toString()
                }
            })


        }
    }
}