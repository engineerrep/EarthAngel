package com.earth.angel.user

import androidx.lifecycle.ViewModel
import com.earth.libbase.network.request.PhotoAddRequest
import kotlinx.coroutines.flow.flow

class UserModel(private val repository: UserRepository) : ViewModel() {


    fun following(pageNum: Int, pageSize: Int) = flow {
        emit(repository.following(pageNum, pageSize))
    }

    fun mobilePhoneAddressBookAdd(photoAddRequest: PhotoAddRequest) = flow {
        emit(repository.mobilePhoneAddressBookAdd(photoAddRequest))
    }

    fun mobilePhoneAddressGet() = flow {
        emit(repository.mobilePhoneAddressGet())
    }

    fun mobilePhoneAddressInvite(id: String?,link: String?) = flow {
        emit(repository.mobilePhoneAddressInvite(id,link))
    }
    fun friendsOfFriends() = flow {
        emit(repository.friendsOfFriends())
    }
    fun requestFriend(userid: Long) = flow {
        emit(repository.requestFriend(userid))
    }
    fun deleteFriend(userid: Long) = flow {
        emit(repository.deleteFriend(userid))
    }
    fun agreeToBeFriends(userid: Long) = flow {
        emit(repository.agreeToBeFriends(userid))
    }
    fun deleteAgreeToBeFriends(userid: Long) = flow {
        emit(repository.deleteAgreeToBeFriends(userid))
    }
    fun myFriendsList() = flow {
        emit(repository.myFriendsList())
    }
    fun sendPhoneVerifyCode(phoneCode: String,phoneNumber: String) = flow {
        emit(repository.sendPhoneVerifyCode(phoneCode,phoneNumber))
    }
    fun updateMobileNumberRequest(Code: String,phoneCode: String,phoneNumber: String) = flow {
        emit(repository.updateMobileNumberRequest(Code,phoneCode,phoneNumber))
    }
}