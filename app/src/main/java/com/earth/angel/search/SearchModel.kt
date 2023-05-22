package com.earth.angel.search

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.LocationRequest
import kotlinx.coroutines.flow.flow

class SearchModel(private val repository: SearchRepository): ViewModel() {



    fun search(searchKey : String,searchType : Int,pageNum: Int, pageSize: Int) = flow {
        emit(repository.search(searchKey,searchType,pageNum,pageSize))
    }
    fun concerned(userid : Long) = flow {
        emit(repository.concerned(userid))
    }
    fun deleteConcerned(userid : Long) = flow {
        emit(repository.deleteConcerned(userid))
    }
    fun report(description: String,releaseRecordId: Long) = flow {
        emit(repository.report(description,releaseRecordId))
    }
    fun postLocation(mLocationRequest: LocationRequest) = flow {
        emit(repository.postLocation(mLocationRequest))
    }
}