package com.earth.angel.user.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.earth.angel.R
import com.earth.angel.databinding.ActivityFriendListBinding
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.search.SearchActivity
import com.earth.angel.user.UserModel
import com.earth.angel.user.side.PinyinComparator
import com.earth.angel.user.side.PinyinUtils
import com.earth.angel.user.side.SortAdapter
import com.earth.angel.user.side.SortModel
import com.earth.angel.util.DataReportUtils
import com.earth.angel.util.DateUtils
import com.earth.libbase.base.BaseActivity
import com.earth.libbase.entity.FriendRequestUserEntity
import com.earth.libbase.entity.MemberEntity
import kotlinx.android.synthetic.main.include_top_bar.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.regex.Pattern

class FriendListActivity : BaseActivity<ActivityFriendListBinding>() {

    private val mEcoGiftGorupsModel by viewModel<EcoGiftGorupsModel>()
    private var sourceDateList: ArrayList<SortModel>? = ArrayList()
    private var friendList: ArrayList<FriendRequestUserEntity>? = ArrayList()
    private var listMenbertList: ArrayList<MemberEntity>? = null

    private var type = 0
    private var houseNumber: Long? = null
    private var adapter: SortAdapter? = null
    private var manager: LinearLayoutManager? = null
    private val userModle by viewModel<UserModel>()

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */

    private var pinyinComparator: PinyinComparator? = null
    override fun getLayoutId(): Int = R.layout.activity_friend_list

    override fun initActivity(savedInstanceState: Bundle?) {
        mBinding?.run {
            showActionBar()
            type = intent.getIntExtra("type", 0)
            houseNumber = intent.getLongExtra("houseNumber", 0)
            tvTitleCenter.text=getString(R.string.lab_Invite)
            listMenbertList =
                intent.getSerializableExtra("listMenbertList") as ArrayList<MemberEntity>?
            llAddNew.setOnClickListener {
                if (DateUtils.isFastClick()){
                    DataReportUtils.getInstance().report("Search")
                    SearchActivity.openSearchActivity(this@FriendListActivity)
                }

            }
            pinyinComparator = PinyinComparator()
            sideBar.setTextView(dialog)
            done.isEnabled = false
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
            when (type) {
                member -> {
                    done.text =  getString(R.string.lab_Invite_title)
                }
                group -> {
                    done.text = getString(R.string.lab_Done)
                }

            }
            done.setOnClickListener {

                when (type) {
                    member -> {
                        invite(houseNumber)
                    }
                    group -> {
                        save()
                    }
                }
            }
        }
        friendList()
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
                    if (it.isOk(this@FriendListActivity)) {
                        it.data?.let {
                            filledData(it)?.let {
                                sourceDateList?.addAll(it)

                            }


                            // sourceDateList= filledData(sourceDateList1) as ArrayList<SortModel>?
                            // sourceDateList = filledData(resources.getStringArray(R.array.date)) as ArrayList<SortModel>?
                            // 根据a-z进行排序源数据
                            sourceDateList?.let {

                                if (type == 0) {
                                    listMenbertList?.let { itlist ->
                                        for (item in itlist) {
                                            for (bean in it) {
                                                if (item.userId.toString() == bean.userid) {
                                                    bean.isSelect = true
                                                    bean.isIsjoin = true
                                                }
                                            }
                                        }
                                    }
                                }


                                // 根据a-z进行排序源数据
                                Collections.sort(it, pinyinComparator)
                                //RecyclerView社置manager
                                manager = LinearLayoutManager(this@FriendListActivity)
                                manager!!.orientation = LinearLayoutManager.VERTICAL
                                adapter =
                                    object : SortAdapter(this@FriendListActivity, it) {
                                        override fun update() {
                                            super.update()

                                            when (type) {
                                                member -> {
                                                    //   done.text = "Invite"
                                                    var ids: ArrayList<Long>? = ArrayList()
                                                    for (item in sourceDateList!!) {
                                                        if (!item.isIsjoin) {
                                                            if (item.isSelect) {
                                                                ids?.add(item.userid.toLong())
                                                            }
                                                        }

                                                    }
                                                    mBinding?.done.isEnabled = ids?.size != 0
                                                    if(ids?.size != 0){
                                                        mBinding?.done.text = getString(R.string.lab_Invite_title)+"("+ ids?.size +")"
                                                    }else{
                                                        mBinding?.done.text = getString(R.string.lab_Invite_title)
                                                    }
                                                }
                                                group -> {
                                                    var ids: ArrayList<Long>? = ArrayList()
                                                    for (item in sourceDateList!!) {
                                                        if (item.isSelect) {
                                                            ids?.add(item.userid.toLong())
                                                        }
                                                    }
                                                    //   done.text = "Done"
                                                    mBinding?.done.isEnabled = ids?.size!! >= 2
                                                    mBinding?.done.text = getString(R.string.lab_Done)
                                                    if(ids?.size != 0){
                                                        mBinding?.done.text = getString(R.string.lab_Done)+"("+ ids?.size +")"
                                                    }else{
                                                        mBinding?.done.text = getString(R.string.lab_Done)
                                                    }
                                                }

                                            }
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

    private fun save() {
        var ids: ArrayList<Long>? = ArrayList()
        for (item in sourceDateList!!) {
            if (item.isSelect) {
                ids?.add(item.userid.toLong())
            }
        }
        launch {
            mEcoGiftGorupsModel.creatGroup(null, null, ids)
                .catch { }.collectLatest {
                    if (it.isOk(this@FriendListActivity)) {
                        setResult(100)
                        finish()
                    }
                }
        }
    }

    private fun invite(houseNum: Long?) {
        var ids: ArrayList<Long>? = ArrayList()
        for (item in sourceDateList!!) {
            if (item.isSelect) {
                ids?.add(item.userid.toLong())
            }
        }
        houseNum?.let {
            launch {
                mEcoGiftGorupsModel.invite(it, ids)
                    .catch { }.collectLatest {
                        if (it.isOk(this@FriendListActivity)) {
                            setResult(102)
                            finish()
                        }
                    }
            }
        }

    }

    companion object {
        const val member = 0
        const val group = 1

        fun openFriendListActivity(context: Context?, type: Int?, houseNumber: Long?) {
            val intent = Intent(context, FriendListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("type", type)
            intent.putExtra("houseNumber", houseNumber)
            context?.startActivity(intent)
        }
    }

}