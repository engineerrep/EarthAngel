package com.earth.libhome.group

import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.CommentRequest
import com.earth.libbase.network.request.GroupNameRequest
import com.earth.libbase.network.request.GroupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GroupRepository (private val api: ApiService) {
    suspend fun communityCreate( bean: GroupNameRequest) =
        withContext(Dispatchers.IO) {
            api.communityCreate(bean)
        }

    suspend fun communityList( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.communityList(bean)
        }
    suspend fun nearbyCommunityList( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.nearbyCommunityList(bean)
        }
    suspend fun communityDetail( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.communityDetail(bean)
        }
    suspend fun communityUpdate( bean: GroupRequest) =
        withContext(Dispatchers.IO) {
            api.communityUpdate(bean)
        }
    suspend fun joinCommunity( bean: GroupRequest) =
        withContext(Dispatchers.IO) {
            api.joinCommunity(bean)
        }
    suspend fun communityItems( bean: CommentRequest) =
        withContext(Dispatchers.IO) {
            api.communityItems(bean)
        }
    suspend fun exitCommunity( bean: GroupRequest) =
        withContext(Dispatchers.IO) {
            api.exitCommunity(bean)
        }
}