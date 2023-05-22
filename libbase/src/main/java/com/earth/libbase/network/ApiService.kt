package com.earth.libbase.network


import com.earth.libbase.base.BaseResultData
import com.earth.libbase.base.ResultCreateData
import com.earth.libbase.baseentity.*
import com.earth.libbase.entity.CommentEntity
import com.earth.libbase.entity.*
import com.earth.libbase.network.baserequest.BasePhotoRequest
import com.earth.libbase.network.request.*
import com.tencent.imsdk.group.GroupInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.http.*


interface ApiService {
    @POST("v1/api/user/mobilePhoneLogin")
    suspend fun login(@Body body: LoginRequest): BaseResultData<LoginEntity>

    @POST("v1/api/giftHouse/recommend/list")
    suspend fun recommend(@Body body: CommonRequest): BaseResultData<List<GiftHouseEntity>>

    @POST("v1/api/giftHouse/ownPageList")
    suspend fun getJoinGroup(@Body body: CommonRequest): BaseResultData<List<GiftHouseEntity>>

    @POST("v1/api/giftHouseUser/join")
    suspend fun joinGroup(@Body body: JoinRequest): BaseResultData<String>

    @POST("v1/api/giftHouse/creat/V2")
    suspend fun creatGroup(@Body body: CreatGroupRequest): BaseResultData<String>

    @POST("v1/api/giftHouseUser/invite")
    suspend fun invite(@Body body: InviteGroupRequest): BaseResultData<String>

    @POST("v1/api/giftHouseUser/pageList")
    suspend fun giftHouseUser(@Body body: GroupMemberRequest): BaseResultData<List<MemberEntity>>

    @POST("v1/api/giftHouse/ace-gift/pageList/V2")
    suspend fun pageList(@Body body: PageGiftListRequest): BaseResultData<List<GiftEntity>>

    @POST("v1/api/goodsRelation/add")
    suspend fun goodsRelation(@Body body: GoodRelationRequest): BaseResultData<String>

    @POST("v1/api/goodsRelation/delete")
    suspend fun deleteGoodsRelation(@Body body: GoodRelationRequest): BaseResultData<String>

    @POST("v1/api/giftHouseUser/quit")
    suspend fun quitGroup(@Body body: QuitGroupRequest): BaseResultData<String>

    @POST("v1/api/giftHouse/update")
    suspend fun updateGroup(@Body body: QuitGroupRequest): BaseResultData<String>

    @POST("v1/api/releasedMessageRecord/add")
    suspend fun releasedMessageRecord(@Body body: ReleasedMessageRequest): BaseResultData<String>

    @POST("v1/api/search/pageList/V2")
    suspend fun search(@Body body: GiftHouseSearchRequest): BaseResultData<List<SearchDetailEntity>>

    @POST("v1/api/sortLabel/list")
    suspend fun sortLabel(): BaseResultData<List<TypeEntity>>

    @POST("v1/api/releaseRecord/add")
    suspend fun releaseRecord(@Body body: ReleaseRecordRequest): BaseResultData<ProductAddEntity>

    @POST("v1/api/releaseRecord/update")
    suspend fun releaseRecordUpdate(@Body body: BasePhotoRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/releaseRecord/ownPageList/V2")
    suspend fun ownPageList(@Body body: CommonRequest): BaseResultData<MutableList<GiftEntity>>

    @POST("v1/api/releaseRecord/tradablePageList")
    suspend fun tradablePageList(@Body body: CommonRequest): BaseResultData<MutableList<GiftEntity>>

    @POST("v1/api/releaseRecord/pageList/V2")
    suspend fun userPageList(@Body body: CommonRequest): BaseResultData<MutableList<GiftEntity>>

    @POST("v1/api/releasedMessageRecord/unread/pageList")
    suspend fun unreadPageList(@Body body: CommonRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/releasedMessageRecord/unread/size")
    suspend fun unreadSize(): BaseResultData<String>

    @POST("v1/api/releasedMessageRecord/read/pageList")
    suspend fun readPageList(@Body body: CommonRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/releaseRecord/done")
    suspend fun unload(@Body body: UnloadRequest): BaseResultData<String>

    @POST("v1/api/releasedMessageRecord/self/pageList")
    suspend fun selfPageList(@Body body: CommonRequest): BaseResultData<GiftWantEntity>

    @POST("v1/api/releasedMessageRecord/selfRead/pageListV2")
    suspend fun selfPageListV2(@Body body: CommonRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/releasedMessageRecord/selfUnread/pageListV2")
    suspend fun selfUnPageListV2(@Body body: CommonRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/releaseRecord/likeList")
    suspend fun likeList(@Body body: CommonRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/user/selectOne")
    suspend fun selectOne(@Body body: GiftHouseSearchRequest): BaseResultData<UserEntity>

    @POST("v1/api/user/selectOne")
    suspend fun selectMine(@Body body: GiftHouseSearchRequest): BaseResultData<MineEntity>

    @POST("v1/api/user/update")
    suspend fun updateOne(@Body body: UpdateUserRequest): BaseResultData<String>

    @POST("v1/api/releaseRecord/selectOne")
    suspend fun releaseRecordOne(@Body body: RecordOneRequest): BaseResultData<GiftEntity>

    @POST("v1/api/concerned/add")
    suspend fun concerned(@Body body: LikeUserRequest): BaseResultData<String>

    @POST("v1/api/frends/requestFriend")
    suspend fun requestFriend(@Body body: FriendRequest): BaseResultData<String>

    @POST("v1/api/concerned/delete")
    suspend fun deleteConcerned(@Body body: LikeUserRequest): BaseResultData<String>

    @POST("v1/api/frends/delete/V2")
    suspend fun deleteFriend(@Body body: FriendRequest): BaseResultData<String>

    @POST("v1/api/frends/agreeToBeFriends")
    suspend fun agreeToBeFriends(@Body body: FriendRequest): BaseResultData<String>

    @POST("v1/api/frends/deleteRequest")
    suspend fun deleteAgreeToBeFriends(@Body body: FriendRequest): BaseResultData<String>


    @POST("v1/api/releaseRecord/pageList")
    suspend fun selectUserGift(@Body body: SelectUserRequest): BaseResultData<OwnGiftEntity>

    @POST("v1/api/releaseRecord/delete")
    suspend fun deleteReleaseRecord(@Body body: RecordOneRequest): BaseResultData<String>

    @POST("v1/api/releaseRecord/reshelf")
    suspend fun reshelfReleaseRecord(@Body body: RecordOneRequest): BaseResultData<GiftEntity>

    @POST("v1/api/releaseRecord/likeFrends")
    suspend fun likeFrends(@Body body: CommentRequest): BaseResultData<List<UserLocationEntity>>

    @POST("v1/api/releaseRecord/unload")
    suspend fun sendGift(@Body body: SendGiftRequest): BaseResultData<GiftEntity>
    @POST("v1/api/releaseRecord/selectOne/V2")
    suspend fun checkSendGift(@Body body: SendGiftRequest): BaseResultData<GiftEntity>
    @POST("v1/api/user/report")
    suspend fun report(@Body body: ReportRequest): BaseResultData<String>

    @POST("v1/api/greenSlogan/selectList")
    suspend fun greenSlogan(): BaseResultData<List<TitleEntity>>

    @POST("v1/api/ecoGift/indexPageList/V2")
    suspend fun indexPageList(@Body body: CommonRequest): BaseResultData<List<HouseListEntity>>

    @POST("v1/api/releaseComment/pageList")
    suspend fun commentPageList(@Body body: CommentRequest): BaseResultData<List<CommentEntity>>

    @POST("v1/api/releaseComment/add")
    suspend fun addComment(@Body body: CommentAddRequest): BaseResultData<String>

    @POST("v1/api/user/postLocation")
    suspend fun postLocation(@Body body: LocationRequest): BaseResultData<List<UserLocationEntity>>

    @POST("v1/api/frends/friendRequestListByMe")
    suspend fun following(@Body body: CommonRequest): BaseResultData<FriendPageEntity>

    @POST("v1/api/mobilePhoneAddressBook/add")
    suspend fun mobilePhoneAddressBookAdd(@Body body: PhotoAddRequest): BaseResultData<String>

    @POST("v1/api/mobilePhoneAddressBook/search")
    suspend fun mobilePhoneAddressGet(): BaseResultData<List<PhotoContactsEntity>>

    @POST("v1/api/mobilePhoneAddressBook/invite/v2")
    suspend fun mobilePhoneAddressInvite(@Body body: NameRequest): BaseResultData<String>

    @POST("v1/api/mobilePhoneAddressBook/friendsOfFriends")
    suspend fun friendsOfFriends(): BaseResultData<List<PhotoContactsEntity>>

    @POST("v1/api/frends/myFriendsList")
    suspend fun myFriendsList(): BaseResultData<List<FriendRequestUserEntity>>

    @POST("v1/api/user/sendPhoneVerifyCode")
    suspend fun sendPhoneVerifyCode(@Body body: PhoneVerifyCodeRequest): BaseResultData<String>

    @POST("v1/api/user/updateMobileNumber")
    suspend fun updateMobileNumberRequest(@Body body: UpdateMobileNumberRequest): BaseResultData<String>

    @POST
    suspend fun getGiftImg(
        @Url url: String,
        @Query("Action") action: String,
        @Query("Version") version: String,
        @Body userId: String

    ): BaseResultData<String>

    @POST("v1/api/shareVersion/add")
    suspend fun shareVersionAdd(@Body body: ShareRequest): BaseResultData<ShareEntity>
    @POST("v1/api/shareVersion/update")
    suspend fun shareVersionUpdate(@Body body: ShareRequest): BaseResultData<ShareEntity>
    @POST("v1/api/releaseRecord/recommend")
    suspend fun recommendMain(@Body body: CommonRequest): BaseResultData<MutableList<GiftEntity>>

    @POST("v1/api/user/emailRegister")
    suspend fun emailRegister(@Body body: RegistRequest): BaseResultData<LoginEntity>

    @POST("v1/api/user/update")
    suspend fun updateUser(@Body body: UserUpdateRequest): BaseResultData<BaseUser>

    @POST("v1/api/user/verify/changePassword")
    suspend fun changePassword(@Body body: ChangePasswordRequest): BaseResultData<LoginEntity>

    @POST("v1/api/readRecord/read")
    suspend fun readRecord(): BaseResultData<SingEntity>

    @POST("v1/api/signInRecord/signIn")
    suspend fun signIn(): BaseResultData<SingEntity>

    @POST("v1/api/shareRecord/share")
    suspend fun shareIn(): BaseResultData<SingEntity>

    @POST("v1/api/user/send/emailCode")
    suspend fun emailCode(@Body body: RegistRequest): BaseResultData<SingEntity>

    @POST("v1/api/user/verify/emailCode")
    suspend fun verifyEmailCode(@Body body: EmailCodeRequest): BaseResultData<SingEntity>

    @POST("v1/api/user/emailLogin")
    suspend fun emailLogin(@Body body: RegistRequest): BaseResultData<LoginEntity?>

    @POST("v1/api/releaseRecord/add")
    suspend fun releaseRecordAdd(@Body body: BasePhotoRequest): BaseResultData<ProductAddEntity>

    @POST("v1/api/user/index")
    suspend fun indexMain(): BaseResultData<BasePageMainGiftEntity>

    @POST("v1/api/releaseRecord/newGifts")
    suspend fun newGifts(@Body body: CommonRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/releaseRecord/nearbyItemGifts")
    suspend fun nearbyItemGifts(@Body body: CommonRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/releaseRecord/userPageList")
    suspend fun userPageGiftList(@Body body: CommonRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/releaseRecord/statusPageList")
    suspend fun statusPageList(@Body body: CommonRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/concerned/add")
    suspend fun concernedAdd(@Body body: BaseUserRequest): BaseResultData<BaseUser>

    @POST("v1/api/concerned/delete")
    suspend fun concernedDelete(@Body body: BaseUserRequest): BaseResultData<BaseUser>

    @POST("v1/api/user/userIndex")
    suspend fun userIndex(@Body body: CommonRequest): BaseResultData<BaseMinePagetEntity>

    @POST("v1/api/user/deleteAccount")
    suspend fun deleteAccount(): BaseResultData<BaseUser>

    @POST("v1/api/poket/add")
    suspend fun poketAdd(@Body body: AddGiftRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/releaseRecord/unload")
    suspend fun giftUnload(@Body body: CommentRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/releaseRecord/relist")
    suspend fun giftRelist(@Body body: CommentRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/releaseRecord/delete")
    suspend fun giftDelete(@Body body: CommentRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/poket/selfPageList")
    suspend fun selfPokedPageList(@Body body: CommonRequest): BaseResultData<BaseChatPageGiftEntity>

    @POST("v1/api/poket/otherPageList")
    suspend fun otherPageList(@Body body: CommonRequest): BaseResultData<BaseChatPageGiftEntity>

    @POST("v1/api/releaseRecord/done")
    suspend fun releaseRecordDone(@Body body: RecordDoneRequest): BaseResultData<MutableList<BaseGiftEntity>>

    @POST("v1/api/releaseRecord/selectOne")
    suspend fun releaseGistOne(@Body body: CommentRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/user/selectOne")
    suspend fun userSelectOne(@Body body: CommonRequest): BaseResultData<BaseUser>

    @POST("v1/api/goodsRelation/add")
    suspend fun goodsRelationAdd(@Body body: GoodsRelationRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/goodsRelation/delete")
    suspend fun goodsRelationDelete(@Body body: CommentRequest): BaseResultData<BaseGiftEntity>

    @POST("v1/api/poket/bagPageList")
    suspend fun poketBagPageList(@Body body: CommentRequest): BaseResultData<BasePagePocketEntity>

    @POST("v1/api/poket/delete")
    suspend fun poketBagdelete(@Body body: PocketDeleteRequest): BaseResultData<BasePagePocketEntity>


    @POST("v1/api/goodsRelation/pageList")
    suspend fun goodsRelationpageList(@Body body: CommentRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/concerned/pageList")
    suspend fun concernedpageList(@Body body: CommentRequest): BaseResultData<BasePageUserEntity>

    @POST("v1/api/shoppingAddress/index")
    suspend fun shoppingAddressIndex(): BaseResultData<ShipAddressEntity>

    @POST("v1/api/shoppingAddress/add")
    suspend fun shoppingAddressAdd(@Body body: ShipAddressRequest): BaseResultData<ShipAddressEntity>

    @POST("v1/api/shoppingAddress/update")
    suspend fun shoppingAddressUpdate(@Body body: ShipAddressRequest): BaseResultData<ShipAddressEntity>

    @POST("v1/api/poket/deleteCounterparty")
    suspend fun deleteCounterparty(@Body body: PocketDeleteRequest): BaseResultData<ShipAddressEntity>

    @POST("v1/api/environmentalArticles/pageList")
    suspend fun pageArticlesList(@Body body: CommentRequest): BaseResultData<BasePageArticleEntity>

    @POST("v1/api/environmentalArticles/selectOne")
    suspend fun pageSelectOne(@Body body: CommentRequest): BaseResultData<ArticleEntity>

    @POST("v1/api/imageRecognition/picByIo")
    suspend fun picByIo(@Body body: BaseFileRequest): BaseResultData<MutableList<CategoryEntity>>

    @POST("v1/api/itemClassification/list")
    suspend fun categoryList(): BaseResultData<MutableList<CategoryEntity>>

    @POST("v1/api/releasedRecordMessage/add")
    suspend fun recordMessageAdd(@Body body: RecordMessageAddRequest): BaseResultData<MessageListEntity>

    @POST("v1/api/releasedRecordMessage/pageList")
    suspend fun recordMessagePageList(@Body body: CommentRequest): BaseResultData<BasePageListMessageEntity>

    @POST("v1/api/releasedRecordMessage/delete")
    suspend fun recordMessageDelete(@Body body: MessageRequest): BaseResultData<MessageListEntity>

    @POST("v1/api/releaseRecord/itemClassificationPageList")
    suspend fun itemClassificationPageList(@Body body: CommentRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/releasedRecordMessage/report")
    suspend fun reportMessage(@Body body: MessageRequest): BaseResultData<MessageListEntity>

    @POST("v1/api/ecoPoint/myScore")
    suspend fun myScore(): BaseResultData<BaseUser>

    @POST("v1/api/ecoPointRecord/pageList")
    suspend fun myScorePageList(@Body body: CommentRequest): BaseResultData<BasePageListPointEntity>

    @POST("v1/api/tradingRecord/page")
    suspend fun myTradingRecord(@Body body: CommentRequest): BaseResultData<BasePageRecordEntity>

    @POST("v1/api/tradingRecord/delete")
    suspend fun myTradingRecordDelete(@Body body: FriendUserRequest): BaseResultData<RecordEntity>

    @POST("v1/api/community/create")
    suspend fun communityCreate(@Body body: GroupNameRequest): BaseResultData<String>

    @POST("v1/api/community/create")
     fun communityCreate1(@Body body: GroupNameRequest): Call<String>

    @POST("v1/api/community/list")
    suspend fun communityList(@Body body: CommentRequest): BaseResultData<BasePageGroupEntity>

    @POST("v1/api/community/nearby/list")
    suspend fun nearbyCommunityList(@Body body: CommentRequest): BaseResultData<BasePageGroupEntity>

    @POST("v1/api/community/detail")
    suspend fun communityDetail(@Body body: CommentRequest): BaseResultData<GroupEntity>

    @POST("v1/api/community/update")
    suspend fun communityUpdate(@Body body: GroupRequest): BaseResultData<GroupEntity>

    @POST("v1/api/community/user/join")
    suspend fun joinCommunity(@Body body: GroupRequest): BaseResultData<GroupEntity>

    @POST("v1/api/community/items")
    suspend fun communityItems(@Body body: CommentRequest): BaseResultData<BasePageGiftEntity>

    @POST("v1/api/community/user/exit")
    suspend fun exitCommunity(@Body body: GroupRequest): BaseResultData<GroupEntity>

    @Multipart
    @POST("v1/batch-upload")
    suspend fun upload(@Part list: List<MultipartBody.Part>): BaseResultData<List<String>>


}