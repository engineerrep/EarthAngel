package com.earth.angel.mine

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.GroupRequest
import com.earth.libbase.network.request.UserUpdateRequest
import kotlinx.coroutines.flow.flow

class MineModel(private val repository: MineRepository): ViewModel() {



    fun ownPageList(pageNum: Int, pageSize: Int,userid: Long?) = flow {
        emit(repository.ownPageList(pageNum,pageSize,userid))
    }
    fun ownSharePageList(pageNum: Int, pageSize: Int,userid: Long?) = flow {
        emit(repository.ownSharePageList(pageNum,pageSize,userid))
    }
    fun tradablePageList(pageNum: Int, pageSize: Int,userid: Long?) = flow {
        emit(repository.tradablePageList(pageNum,pageSize,userid))
    }
    fun userPageList(pageNum: Int, pageSize: Int,userid: Long?) = flow {
        emit(repository.userPageList(pageNum,pageSize,userid))
    }
    fun unreadPageList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.unreadPageList(pageNum,pageSize))
    }
    fun unreadSize() = flow {
        emit(repository.unreadSize())
    }
    fun userSelectOne(bean: CommonRequest) = flow {
        emit(repository.userSelectOne(bean))
    }
    fun readPageList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.readPageList(pageNum,pageSize))
    }

    fun unload(id: Long) = flow {
        emit(repository.unload(id))
    }
    fun selfPageList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.selfPageList(pageNum,pageSize))
    }

    fun selfPageListV2(pageNum: Int, pageSize: Int) = flow {
        emit(repository.selfPageListV2(pageNum,pageSize))
    }
    fun selfUnPageListV2(pageNum: Int, pageSize: Int) = flow {
        emit(repository.selUnfPageListV2(pageNum,pageSize))
    }
    fun likeList(pageNum: Int, pageSize: Int) = flow {
        emit(repository.likeList(pageNum,pageSize))
    }
    fun selectOne(userid: Long?) = flow {
        emit(repository.selectOne(userid))
    }
    fun selectMine() = flow {
        emit(repository.selectMine())
    }
    fun updateOne(headImgUrl: String, nickName: String) = flow {
        emit(repository.updateOne(headImgUrl,nickName))
    }
    fun releaseRecordOne(id: Long) = flow {
        emit(repository.releaseRecordOne(id))
    }
    fun updateUser(mUserUpdateRequest: UserUpdateRequest) = flow {
        emit(repository.updateUser(mUserUpdateRequest))
    }
    fun signIn() = flow {
        emit(repository.signIn())
    }
    fun joinCommunity(bean: GroupRequest) = flow {
        emit(repository.joinCommunity(bean))
    }
}