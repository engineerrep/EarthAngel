package com.earth.libhome.group

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.CommonRequest
import com.earth.libbase.network.request.GroupNameRequest
import com.earth.libbase.network.request.GroupRequest
import com.earth.libhome.ui.HomeRepository
import kotlinx.coroutines.flow.flow

class GroupApiModle(private val repository: GroupRepository): ViewModel() {

    fun communityCreate(bean: GroupNameRequest) = flow {
        emit(repository.communityCreate(bean))
    }

    fun communityList(bean: CommentRequest) = flow {
        emit(repository.communityList(bean))
    }

    fun nearbyCommunityList(bean: CommentRequest) = flow {
        emit(repository.nearbyCommunityList(bean))
    }

    fun communityDetail(bean: CommentRequest) = flow {
        emit(repository.communityDetail(bean))
    }
    fun communityUpdate(bean: GroupRequest) = flow {
        emit(repository.communityUpdate(bean))
    }
    fun joinCommunity(bean: GroupRequest) = flow {
        emit(repository.joinCommunity(bean))
    }
    fun communityItems(bean: CommentRequest) = flow {
        emit(repository.communityItems(bean))
    }
    fun exitCommunity(bean: GroupRequest) = flow {
        emit(repository.exitCommunity(bean))
    }
}