package com.earth.libbase.network.baserequest

import com.earth.libbase.network.request.PictureUrlRequest
import com.google.gson.annotations.SerializedName

data class BasePhotoRequest(
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("id")
    var id: Long? = null,
    @SerializedName("itemTitle")
    var itemTitle: String? = null,
    @SerializedName("mainPicture")
    var mainPicture: String? = null,
    @SerializedName("pictureResources")
    var pictureResources: List<String>?,
    @SerializedName("itemClassification")
    var itemClassification: Int? = null,
    @SerializedName("itemCondition")
    var itemCondition: String? = null,
    @SerializedName("score")
    var score: Int? = null,
    @SerializedName("communityIds")
    var communityIds: List<String>?
)
