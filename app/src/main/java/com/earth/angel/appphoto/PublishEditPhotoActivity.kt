package com.earth.angel.appphoto

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.earth.angel.R
import com.earth.angel.appphoto.adapter.PhotoPublishEditAdapter
import com.earth.angel.base.PhotoUploadType
import com.earth.angel.databinding.ActivityPublisheditphotoBinding
import com.earth.angel.photo.SelectedMutablePhotoEvent
import com.earth.angel.photo.gallery.GalleryModel
import com.earth.angel.util.EventBusHelper
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.base.Constance
import com.earth.libbase.util.BaseDateUtils
import kotlinx.android.synthetic.main.activity_publisheditphoto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.toast

@Route(path = Constance.PublishEditPhotoActivityURL)
class PublishEditPhotoActivity : BaseActivity<ActivityPublisheditphotoBinding>() {

    @Autowired(name = "position")
    @JvmField
    var position: String? = null

    private var listPhotos = mutableListOf<GalleryModel>()
    private var layoutPager: LinearLayoutManager? = null

    private var imgAdapter: PhotoPublishEditAdapter? = null

    private var snapHelper: PagerSnapHelper? = null
    private var lastPosition = 0 //记录recyclerView最后一次角标位置，用于判断是否转换了item

    override fun getLayoutId(): Int = R.layout.activity_publisheditphoto

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding.run {
            ARouter.getInstance().inject(this@PublishEditPhotoActivity)
            showActionBar()
            EventBusHelper.register(this@PublishEditPhotoActivity)
            imgAdapter =
                PhotoPublishEditAdapter(this@PublishEditPhotoActivity, listPhotos, upDade = {
                    listPhotos.remove(it)
                    imgAdapter?.notifyDataSetChanged()
                })
            layoutPager = LinearLayoutManager(
                this@PublishEditPhotoActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            PublishEditRlv.layoutManager = layoutPager
            PublishEditRlv.adapter = imgAdapter
            imgAdapter?.setOnItemClickListener { adapter, view, position ->
                if (BaseDateUtils.isFastClick()) {
                    var list = ArrayList<String>()
                    for (item in listPhotos) {
                        item.path?.let {
                            list.add(it)
                        }
                    }
                    ARouter.getInstance().build(Constance.GiftPhotoActivity)
                        .withInt("position", position)
                        .withStringArrayList("ImgList", list)
                        .navigation()
                }
            }
            snapHelper = PagerSnapHelper()
            snapHelper?.attachToRecyclerView(PublishEditRlv)
            PublishEditRlv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val view: View = snapHelper?.findSnapView(layoutPager) ?: return
                    val position: Int = layoutPager!!.getPosition(view)
                    if (lastPosition == position) {
                        return
                    }
                    lastPosition = position
                    PhotoEditTitle.text =
                        ((lastPosition + 1).toString() + "/" + listPhotos.size.toString()).toString()
                    initCoverBtn()
                }
            })
            position?.let {
                lastPosition = it.toInt()
                PhotoEditTitle.text =
                    ((lastPosition + 1).toString() + "/" + listPhotos.size.toString()).toString()
            }

            PublishEditCover.setOnClickListener {
                if (listPhotos.size == 0) {
                    return@setOnClickListener
                }
                var bean = listPhotos[lastPosition]
                listPhotos.removeAt(lastPosition)
                listPhotos.add(0, bean)
                lastPosition = 0
                imgAdapter?.notifyDataSetChanged()
                PublishEditRlv.scrollToPosition(lastPosition)
                PhotoEditTitle.text =
                    ((lastPosition + 1).toString() + "/" + listPhotos.size.toString()).toString()
                initCoverBtn()

            }
            PublishEditDelete.setOnClickListener {
                if (listPhotos.size == 1) {
                    toast(R.string.base_tips_Picture)
                    return@setOnClickListener
                }
                listPhotos.removeAt(lastPosition)
                if (lastPosition != 0) {
                    lastPosition -= 1
                }
                imgAdapter?.notifyDataSetChanged()
                PublishEditRlv.scrollToPosition(lastPosition)
                PhotoEditTitle.text =
                    ((lastPosition + 1).toString() + "/" + listPhotos.size.toString()).toString()
                initCoverBtn()

            }
            PublishEditCancel.setOnClickListener {
                EventBusHelper.post(
                    SelectedMutablePhotoEvent(
                        listPhotos,
                        PhotoUploadType.PHOTOEDITBANK
                    )
                )
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EventBusHelper.post(
                SelectedMutablePhotoEvent(
                    listPhotos,
                    PhotoUploadType.PHOTOEDITBANK
                )
            )
            finish()
        }
        return false
    }

    @ExperimentalCoroutinesApi
    @Subscribe
    fun onEvent(event: SelectedMutablePhotoEvent) {
        when (event.photoType) {
            PhotoUploadType.PHOTOEDIT -> {
                if (event.photos.isNotEmpty()) {
                    if (event.photos.size > 0) {
                        listPhotos.addAll(event.photos)
                        imgAdapter?.notifyDataSetChanged()
                        PhotoEditTitle.text =
                            ((lastPosition + 1).toString() + "/" + listPhotos.size.toString()).toString()
                        position?.let {
                            layoutPager?.scrollToPosition(it.toInt())
                        }
                        initCoverBtn()
                    }
                }
            }
        }
    }

    private fun initCoverBtn() {
        if (lastPosition == 0) {
            PublishEditCover?.text = getString(R.string.label_Cover_Image)
            PublishEditCover?.visibility=View.GONE
        } else {
            PublishEditCover?.text = getString(R.string.label_Set_Cover)
            PublishEditCover?.visibility=View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusHelper.unregister(this)
    }

}