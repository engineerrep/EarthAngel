package com.curvydatingplus.ui.photo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.earth.angel.photo.gallery.GalleryModel

class UploadPhotoViewModel : ViewModel() {
    var galleryListItem = MutableLiveData<MutableList<GalleryModel>>()

    fun select(item: MutableList<GalleryModel>) {
        galleryListItem.value = item
    }
}
