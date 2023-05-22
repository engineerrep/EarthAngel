package com.earth.angel.user.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityMyFriendsBinding
import com.earth.angel.dialog.DialogCommon
import com.earth.angel.event.MessageEvent
import com.earth.angel.event.ShareShowEvent
import com.earth.angel.gift.ui.UserMainActivity.Companion.openUserMainActivity
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.mine.ui.*
import com.earth.angel.user.UserModel
import com.earth.angel.user.adapter.FriendAdapter
import com.earth.angel.user.adapter.FriendClickListener
import com.earth.angel.user.side.PinyinComparator
import com.earth.angel.user.side.PinyinUtils
import com.earth.angel.user.side.SortModel
import com.earth.angel.user.ui.fragment.UserFollowingFragment
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.FriendRequestUserEntity
import com.earth.libbase.entity.MemberEntity
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.regex.Pattern

class MyFriendsActivity : BaseActivity<ActivityMyFriendsBinding>() {
    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private val userModle by viewModel<UserModel>()

    private var sourceDateList: ArrayList<SortModel>? = ArrayList()
    private var friendList: ArrayList<FriendRequestUserEntity>? = ArrayList()
    private var listMenbertList: ArrayList<MemberEntity>? = null

    private var adapter: FriendAdapter? = null
    private var manager: LinearLayoutManager? = null
    private var type: String? = null

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */

    private var pinyinComparator: PinyinComparator? = null
    override fun getLayoutId(): Int =R.layout.activity_my_friends

    override fun initActivity(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this@MyFriendsActivity)
        mBinding?.run {
            showActionBar()
            type=intent.getStringExtra("type")
            tvTitleCenter.text=getString(R.string.lab_Add_People_You_Know)
            listMenbertList =
                intent.getSerializableExtra("listMenbertList") as ArrayList<MemberEntity>?
            pinyinComparator = PinyinComparator()
            sideBar.setTextView(dialog)
            //设置右侧SideBar触摸监听
            sideBar.setOnTouchingLetterChangedListener { s ->
                if (adapter == null) {
                    return@setOnTouchingLetterChangedListener
                }
                val position: Int = adapter?.getPositionForSection(s[0].toInt())!!
                if (position != -1) {
                    manager?.scrollToPositionWithOffset(position, 0)
                }
            }

            //根据输入框输入值的改变来过滤搜索
            filterEdit.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(s.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable) {}
            })
            ctRequests.setOnClickListener {
                startActivity(Intent(this@MyFriendsActivity, UserFollowingFragment::class.java))
            }
            ctContacts.setOnClickListener {
                startActivity.launch(
                    Intent(
                        this@MyFriendsActivity,
                        ContactsActivity::class.java
                    )
                )
            }
            ctGroups.setOnClickListener {
                startActivity.launch(
                    Intent(
                        this@MyFriendsActivity,
                        GroupListActivity::class.java
                    )
                )

            }
            type?.let {
                ctRequests.visibility=View.GONE
                ctContacts.visibility=View.GONE
                ctGroups.visibility=View.GONE
            }
        }
        friendList()
        if(type==null){
            friendListNum()
        }
    }
    /**
     * 为RecyclerView填充数据
     *
     * @param date
     * @return
     */
    private fun filledData(date: List<FriendRequestUserEntity>): ArrayList<SortModel>? {
        val mSortList: ArrayList<SortModel> = ArrayList()
        for (i in date) {
            val sortModel = SortModel()
            sortModel.name = i.frendUserNickName
            sortModel.imgUrl = i.frendUserHeadImgUrl
            sortModel.userid = i.frendUserId
            //汉字转换成拼音
            val pinyin: String = PinyinUtils.getPingYin(i.frendUserNickName)
            val sortString = pinyin.substring(0, 1).toUpperCase()

            // 正则表达式，判断首字母是否是英文字母
            if (isLetter(sortString)) {
                sortModel.letters = sortString.toUpperCase()
            } else {
                sortModel.letters = "#"
            }
            mSortList.add(sortModel)
        }
        return mSortList
    }

    fun isLetter(str: String?): Boolean {
        val pattern = Pattern.compile("[a-zA-Z]+")
        val m = pattern.matcher(str)
        return m.matches()
    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private fun filterData(filterStr: String) {
        var filterDateList: ArrayList<SortModel?> = ArrayList()
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList.addAll(sourceDateList!!)
        } else {
            filterDateList.clear()
            for (sortModel in sourceDateList!!) {
                val name = sortModel.name
                if (name.indexOf(filterStr) != -1 ||
                    PinyinUtils.getFirstSpell(name).startsWith(filterStr) //不区分大小写
                    || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr)
                    || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr)
                ) {
                    filterDateList.add(sortModel)
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator)
        adapter!!.updateList(filterDateList)
    }

    private fun friendList() {
        launch {
            userModle.myFriendsList().catch {}
                .onStart { }
                .onCompletion { }
                .collectLatest {
                    if (it.isOk(this@MyFriendsActivity)) {
                        it.data?.let {
                            filledData(it)?.let {
                                sourceDateList?.addAll(it)

                            }


                            // sourceDateList= filledData(sourceDateList1) as ArrayList<SortModel>?
                            // sourceDateList = filledData(resources.getStringArray(R.array.date)) as ArrayList<SortModel>?
                            // 根据a-z进行排序源数据
                            sourceDateList?.let {
                                // 根据a-z进行排序源数据
                                Collections.sort(it, pinyinComparator)
                                //RecyclerView社置manager
                                manager = LinearLayoutManager(this@MyFriendsActivity)
                                manager!!.orientation = LinearLayoutManager.VERTICAL
                                adapter =
                                    object : FriendAdapter(this@MyFriendsActivity, it,object : FriendClickListener {
                                        override fun onItemClick(mSortModel: SortModel?) {
                                            if(type==null){
                                                openUserMainActivity(
                                                    this@MyFriendsActivity,
                                                    mSortModel!!.getName(),
                                                    mSortModel!!.getUserid(),
                                                    mSortModel!!.getImgUrl()
                                                )
                                            }else{
                                                var blockDialog = DialogCommon(title = getString(R.string.dialog_sent),onBlockClick = {
                                                    sendGift(type!!.toLong(),mSortModel?.userid!!.toLong())
                                                })
                                                blockDialog.show(
                                                    supportFragmentManager, ""
                                                )

                                            }
                                        }
                                    }) {
                                        override fun update() {
                                            super.update()

                                        }
                                    }
                                mBinding.recyclerView.layoutManager = manager
                                mBinding.recyclerView.adapter = adapter
                            }

                        }
                    }

                }
        }
    }


    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
            }
        }

    companion object {
        const val member = 0
        const val group = 1

        fun openFriendListActivity(context: Context?, type: Int?) {
            val intent = Intent(context, FriendListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }

    private fun sendGift(exchangePersonId: Long,id: Long) {
        launch {
            mEcoGiftGorupsModel.sendGift(id,exchangePersonId)
                .catch {  }
                .onStart {
                   // mAppViewModel.showLoading()
                }
                .onCompletion {
                 //   mAppViewModel.dismissLoading()
                }
                .collectLatest {
                    if(it.isOk(this@MyFriendsActivity)){
                        setResult(100)
                        EventBus.getDefault().post(ShareShowEvent())
                        finish()
                    }else{
                        toast(it.msg)
                    }
                }
        }
    }
    private fun friendListNum() {
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChatUserEvent(event: MessageEvent) {
        sourceDateList?.clear()
        adapter?.notifyDataSetChanged()
        friendList()
        if(type==null){
            friendListNum()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@MyFriendsActivity)
    }
}