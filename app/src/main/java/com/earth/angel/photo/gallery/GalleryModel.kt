package com.earth.angel.photo.gallery

import android.net.Uri

/**
 * create by zhaimi
 * description:
 */
class GalleryModel : Comparable<GalleryModel> {

    companion object {
        const val TYPE_IMAGE = "image"
        const val TYPE_VIDEO = "video"
        const val TYPE_PHOTO= "photo" //拍照
    }
    var path: String? = null
    var photo: String? = null
    var name: String? = null
    var fileName: String? = null
    var uri: Uri? = null
    var time: Long = 0

    var width: Int = 0

    var height: Int = 0

    /**
     * 只有视频类型才有duration
     */
    var duration: Long = 0

    /**
     * 默认Image类型
     */
    var type: String = TYPE_IMAGE

    override fun compareTo(other: GalleryModel): Int {
        return when {
            other.time > time -> 1
            other.time < time -> -1
            else -> 0
        }
    }

    /**
     * 路径和名字相同,就认为equal了
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryModel

        if (path != other.path) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        var result = path?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + time.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + type.hashCode()
        return result
    }


}