package com.earth.angel.module


import com.earth.angel.MainActivity
import com.earth.angel.MainPhotoActivity
import com.earth.angel.appphoto.*
import com.earth.angel.chat.*
import com.earth.angel.gift.adapter.MainListPagingAdapter
import com.earth.angel.gift.ui.*
import com.earth.angel.gift.ui.fragment.EcoGiftGorupsModel
import com.earth.angel.gift.ui.fragment.EcoGiftGroupsFragment
import com.earth.angel.gift.ui.fragment.EcoGiftGroupsRepository
import com.earth.angel.login.LoginActivity
import com.earth.angel.login.LoginModel
import com.earth.angel.login.LoginRepository
import com.earth.angel.login.UserHeadActivity
import com.earth.angel.mine.MineModel
import com.earth.angel.mine.MineRepository
import com.earth.angel.mine.ui.*
import com.earth.angel.photo.AddPhotoActivity
import com.earth.angel.photo.CameraActivity
import com.earth.angel.photo.CameraV2Activity
import com.earth.angel.regist.ForgotPasswordActivity
import com.earth.angel.regist.RegistActivity
import com.earth.angel.regist.RegistModel
import com.earth.angel.regist.RegistRepository
import com.earth.angel.regist.forgotpassword.PasswordViewModle
import com.earth.angel.regist.ui.RegistViewModle
import com.earth.angel.search.SearchActivity
import com.earth.angel.search.SearchModel
import com.earth.angel.search.SearchRepository
import com.earth.angel.share.ShareActivity
import com.earth.angel.share.modle.ShareModel
import com.earth.angel.share.modle.ShareRepository
import com.earth.angel.user.UserModel
import com.earth.angel.user.UserRepository
import com.earth.angel.user.ui.AddUserActivity
import com.earth.angel.user.ui.ContactsActivity
import com.earth.angel.user.ui.FriendListActivity
import com.earth.angel.user.ui.fragment.FriendsofFriendsFragment
import com.earth.angel.user.ui.fragment.UserFollowingFragment
import com.earth.libarticl.ArticleModle
import com.earth.libarticl.ArticleRepository
import com.earth.libbase.base.AppViewModel
import com.earth.libbase.dialog.LoadingDialog
import com.earth.libbase.network.RetrofitManager
import com.earth.libhome.gift.HomeGiftDetailsActivity
import com.earth.libhome.group.GroupApiModle
import com.earth.libhome.group.GroupMainActivity
import com.earth.libhome.group.GroupModle
import com.earth.libhome.group.GroupRepository
import com.earth.libhome.packet.HomePackingBagActivity
import com.earth.libhome.packet.PocketModle
import com.earth.libhome.packet.PocketRepository
import com.earth.libhome.ui.HomeModle
import com.earth.libhome.ui.HomeRepository
import com.earth.libmine.set.LibMineEtNameActivity
import com.earth.libmine.set.LibMineSetActivity
import com.earth.libmine.set.LibMineShippingAddressActivity
import com.earth.libmine.ui.LibMineModle
import com.earth.libmine.ui.LibMineRepository
import com.earth.libmine.ui.LibMineUserActivity
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*


val dataSourceModule = module {
    single { RetrofitManager.apiService }
    single { Calendar.getInstance() }
    //single { AppDatabase.buildDatabase(androidContext()) }
}


val repositoryModule = module {
    single { EcoGiftGroupsRepository(get()) }
    single { SearchRepository(get()) }
    single { PhotoRepository(get()) }
    single { MineRepository(get()) }
    single { LoginRepository(get()) }
    single { UserRepository(get()) }
    single { ContactListRepository(get()) }
    single { ShareRepository(get()) }
    single { HomeRepository(get()) }
    single { RegistRepository(get()) }
    single { LibMineRepository(get()) }
    single { PocketRepository(get()) }
    single { ArticleRepository(get()) }
    single { GroupRepository(get()) }

}

val viewModelModule = module {
    viewModel { EcoGiftGorupsModel(get()) }
    viewModel { SearchModel(get()) }
    viewModel { PhotoModel(get()) }
    viewModel { MineModel(get()) }
    viewModel { AppViewModel() }
    viewModel { LoginModel(get()) }
    viewModel { UserModel(get()) }
    viewModel { ContactListViewModel(get()) }
    viewModel { ShareModel(get()) }
    viewModel { RegistViewModle() }
    viewModel { HomeModle(get()) }
    viewModel { RegistModel(get()) }
    viewModel { LibMineModle(get()) }
    viewModel { PocketModle(get()) }
    viewModel { ArticleModle(get()) }
    viewModel { PasswordViewModle() }
    viewModel { GroupModle() }
    viewModel { GroupApiModle(get()) }

}

val fragmentModule = module {

}


val adapterModule = module {
    scope<ChatListActivity> {
        scoped { ChatPopularityPagingAdapter() }
    }
    scope<MainPhotoActivity> {
        scoped { MainListPagingAdapter() }
    }
}


val dialogModule = module {

    scope<MainActivity> {
        scoped { LoadingDialog() }
    }
    scope<SearchActivity> {
        scoped { LoadingDialog() }
    }
    scope<PhotoEditActivity> {
        scoped { LoadingDialog() }
    }
    scope<AddGroupActivity> {
        scoped { LoadingDialog() }
    }
    scope<EditProfileActivity> {
        scoped { LoadingDialog() }
    }
    scope<EcoGiftsWantActivity> {
        scoped { LoadingDialog() }
    }
    scope<ConsideringGiftActivity> {
        scoped { LoadingDialog() }
    }
    scope<LoginActivity> {
        scoped { LoadingDialog() }
    }
    scope<UserMainActivity> {
        scoped { LoadingDialog() }
    }
    scope<GroupDetailsActivity> {
        scoped { LoadingDialog() }
    }
    scope<MyEcoGiftActivity> {
        scoped { LoadingDialog() }
    }
    scope<AddUserActivity> {
        scoped { LoadingDialog() }
    }
    scope<FriendListActivity> {
        scoped { LoadingDialog() }
    }
    scope<ContactsActivity> {
        scoped { LoadingDialog() }
    }
    scope<GiftDetailsActivity> {
        scoped { LoadingDialog() }
    }
    scope<VerifyPhoneActivity> {
        scoped { LoadingDialog() }
    }
    scope<FriendsofFriendsFragment> {
        scoped { LoadingDialog() }
    }
    scope<ChatListActivity> {
        scoped { LoadingDialog() }
    }
    scope<CameraActivity> {
        scoped { LoadingDialog() }
    }
    scope<CameraV2Activity> {
        scoped { LoadingDialog() }
    }
    scope<RepotActivity> {
        scoped { LoadingDialog() }
    }
    scope<EcoGiftGroupsFragment> {
        scoped { LoadingDialog() }
    }
    scope<ShareActivity> {
        scoped { LoadingDialog() }
    }
    scope<AddPhotoActivity> {
        scoped { LoadingDialog() }
    }
    scope<CutPhotoActivity> {
        scoped { LoadingDialog() }
    }
    scope<UserFollowingFragment> {
        scoped { LoadingDialog() }
    }
    scope<UserHeadActivity> {
        scoped { LoadingDialog() }
    }
    scope<RegistActivity> {
        scoped { LoadingDialog() }
    }
    scope<LibMineEtNameActivity> {
        scoped { LoadingDialog() }
    }
    scope<LibMineShippingAddressActivity> {
        scoped { LoadingDialog() }
    }
    scope<HomePackingBagActivity> {
        scoped { LoadingDialog() }
    }
    scope<PhotoPostActivity> {
        scoped { LoadingDialog() }
    }
    scope<HomeGiftDetailsActivity> {
        scoped { LoadingDialog() }
    }
    scope<LibMineSetActivity> {
        scoped { LoadingDialog() }
    }
    scope<LibMineUserActivity> {
        scoped { LoadingDialog() }
    }
    scope<ForgotPasswordActivity> {
        scoped { LoadingDialog() }
    }
    scope<GroupMainActivity> {
        scoped { LoadingDialog() }
    }
    scope<ChatActivity> {
        scoped { LoadingDialog() }
    }
}