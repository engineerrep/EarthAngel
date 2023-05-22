package com.earth.angel.photo

import com.earth.angel.photo.gallery.GalleryModel

interface IAddPhotoListener {
    fun selectPhoto(photos: MutableList<GalleryModel>)

}