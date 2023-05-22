package com.earth.angel.search


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.GiftHouseSearchRequest
import com.earth.libbase.network.request.LikeUserRequest
import com.earth.libbase.network.request.LocationRequest
import com.earth.libbase.network.request.ReportRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository(private val api: ApiService) {
    suspend fun search(searchKey : String,searchType : Int,pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.search(GiftHouseSearchRequest(searchKey,searchType,pageNum, pageSize))
        }
    suspend fun concerned(userid : Long) =
        withContext(Dispatchers.IO) {
            api.concerned(LikeUserRequest(userid))
        }
    suspend fun deleteConcerned(userid : Long) =
        withContext(Dispatchers.IO) {
            api.deleteConcerned(LikeUserRequest(userid))
        }
    suspend fun report(description: String,releaseRecordId: Long) =
        withContext(Dispatchers.IO) {
            api.report(ReportRequest(description,releaseRecordId))
        }
    suspend fun postLocation(mLocationRequest: LocationRequest) =
        withContext(Dispatchers.IO) {
            api.postLocation(mLocationRequest)
        }
}