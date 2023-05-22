package com.earth.angel.appphoto

import com.earth.angel.photo.gallery.GalleryModel
import com.google.gson.annotations.SerializedName

data class BasePhotoSaveEntity(
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("itemTitle")
    var itemTitle: String? = null,
    @SerializedName("pictureResources")
    var pictureResources: MutableList<GalleryModel>?
)
