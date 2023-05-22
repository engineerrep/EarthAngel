package com.earth.angel.user


import com.earth.libbase.network.ApiService
import com.earth.libbase.network.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val api: ApiService) {

    suspend fun following(pageNum: Int, pageSize: Int) =
        withContext(Dispatchers.IO) {
            api.following(CommonRequest(pageNum, pageSize))
        }

    suspend fun mobilePhoneAddressBookAdd(photoAddRequest: PhotoAddRequest) =
        withContext(Dispatchers.IO) {
            api.mobilePhoneAddressBookAdd(photoAddRequest)
        }

    suspend fun mobilePhoneAddressGet() =
        withContext(Dispatchers.IO) {
            api.mobilePhoneAddressGet()
        }
    suspend fun mobilePhoneAddressInvite(id: String?,link: String?) =
        withContext(Dispatchers.IO) {
            api.mobilePhoneAddressInvite(NameRequest(id,link))
        }
    suspend fun friendsOfFriends() =
        withContext(Dispatchers.IO) {
            api.friendsOfFriends()
        }
    suspend fun requestFriend(userid: Long) =
        withContext(Dispatchers.IO) {
            api.requestFriend(FriendRequest(userid))
        }
    suspend fun deleteFriend(userid: Long) =
        withContext(Dispatchers.IO) {
            api.deleteFriend(FriendRequest(userid))
        }
    suspend fun agreeToBeFriends(userid: Long) =
        withContext(Dispatchers.IO) {
            api.agreeToBeFriends(FriendRequest(userid))
        }
    suspend fun deleteAgreeToBeFriends(userid: Long) =
        withContext(Dispatchers.IO) {
            api.deleteAgreeToBeFriends(FriendRequest(userid))
        }
    suspend fun myFriendsList() =
        withContext(Dispatchers.IO) {
            api.myFriendsList()
        }
    suspend fun sendPhoneVerifyCode(phoneCode: String,phoneNumber: String) =
        withContext(Dispatchers.IO) {
            api.sendPhoneVerifyCode(PhoneVerifyCodeRequest(phoneCode,phoneNumber))
        }
    suspend fun updateMobileNumberRequest(Code: String,phoneCode: String,phoneNumber: String) =
        withContext(Dispatchers.IO) {
            api.updateMobileNumberRequest(UpdateMobileNumberRequest(Code,phoneCode,phoneNumber))
        }
}