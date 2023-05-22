package com.earth.angel.photo.gallery

/**
 * create by zhaimi
 * description:
 */
class GalleryFolder {
    // 文件夹路径
    var dir: String? = null

    var name: String? = null
        get() {
            return if (dir != null) {
                val lastIndexOf = dir!!.lastIndexOf("/")
                dir!!.substring(lastIndexOf.plus(1))
            } else {
                null
            }
        }

    var count: Int = 0
        get() {
            return videos.size + images.size
        }

    val videos: MutableList<GalleryModel> = mutableListOf()

    val images: MutableList<GalleryModel> = mutableListOf()
}