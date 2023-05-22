package com.earth.angel.photo

import com.earth.angel.photo.gallery.GalleryModel

data class SelectedMutablePhotoEvent (
    var photos : MutableList<GalleryModel>,
    var photoType : Int
)
