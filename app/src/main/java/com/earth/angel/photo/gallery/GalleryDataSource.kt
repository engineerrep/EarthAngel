package com.earth.angel.photo.gallery

import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

/**
 * create by zhaimi
 * description:
 */
class GalleryDataSource(
    private val activity: FragmentActivity,
    val loadedFolder: (galleryFolder: List<GalleryFolder>) -> Unit = {},
    var types: ArrayList<String> = arrayListOf(
        GalleryModel.TYPE_IMAGE, GalleryModel.TYPE_VIDEO
    )
) {

    companion object {
        private val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media._ID
        )

        private val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DURATION
        )

        private const val MIN_IMAGE_WIDTH = 640
        private const val MIN_IMAGE_HEIGHT = 800

    }

    /**
     * 开始加载
     */
    fun start() {
        GlobalScope.launch {
            val videos = async {
                if (types.contains(GalleryModel.TYPE_VIDEO)) {
                    return@async getVideoTask()
                } else {
                    return@async mutableListOf<GalleryModel>()
                }
            }
            val images = async {
                if (types.contains(GalleryModel.TYPE_IMAGE)) {
                    return@async getImageTask()
                } else {
                    return@async mutableListOf<GalleryModel>()
                }
            }

            val list = videos.await() + images.await()
            list.sorted()
            val folderMap: LinkedHashMap<String, GalleryFolder> = LinkedHashMap()
            val defFolder = GalleryFolder()
            val defDir = "/PHOTOS"
            defFolder.dir = defDir
            folderMap[defDir] = defFolder
            for (model in list) {
                val dirPath = File(model.path!!).parentFile!!.absolutePath
                val dirFile = File(dirPath)
                if (dirFile.isDirectory) {
                    model.fileName=dirFile.parent
                    if (folderMap.containsKey(dirPath)) {
                        if (model.type == GalleryModel.TYPE_IMAGE) {
                            folderMap[defDir]?.images?.add(model)
                            folderMap[dirPath]?.images?.add(model)
                        } else {
                            folderMap[defDir]?.videos?.add(model)
                            folderMap[dirPath]?.videos?.add(model)
                        }

                    } else {
                        val folder = GalleryFolder()
                        folder.dir = dirPath
                        if (model.type == GalleryModel.TYPE_IMAGE) {
                            folderMap[defDir]?.images?.add(model)
                            folder.images.add(model)
                        } else {
                            folderMap[defDir]?.videos?.add(model)
                            folder.videos.add(model)
                        }
                        folderMap[dirPath] = folder
                    }
                }

            }

            if (!activity.isDestroyed) {
                activity.runOnUiThread {
                    loadedFolder(ArrayList(folderMap.values))
                }
            }
        }
    }

    private fun getVideoTask(): MutableList<GalleryModel> {


        val result = mutableListOf<GalleryModel>()
        val videoCursor = activity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
            "(" + MediaStore.Video.Media.MIME_TYPE + "= ?)", arrayOf("video/mp4"),
            MediaStore.Video.Media.DATE_MODIFIED + " DESC"
        ) ?: return result

        val nameColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
        val pathColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.DATA)
        val timeColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)
        val widthColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
        val heightColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
        val durationColumn = videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION)

        while (videoCursor.moveToNext()) {
            if (activity.isDestroyed) {
                break
            }

            val path = videoCursor.getString(pathColumn)
            path?.let {
                val file = File(it)
                if (file.exists() && file.isFile) {
                    val name = videoCursor.getString(nameColumn)
                    val width = videoCursor.getInt(widthColumn)
                    val height = videoCursor.getInt(heightColumn)
                    val time = videoCursor.getLong(timeColumn)
                    val duration = videoCursor.getLong(durationColumn)

                    result.add(GalleryModel().apply {
                        this.name = name
                        this.path = path
                        this.width = width
                        this.height = height
                        this.duration = duration
                        this.time = time
                        this.type = GalleryModel.TYPE_VIDEO
                    })

                }
            }
        }
        videoCursor.close()
        return result
    }


    private fun getImageTask(): MutableList<GalleryModel> {

        val result = mutableListOf<GalleryModel>()
        val imageCursor = activity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
            MediaStore.Images.Media.MIME_TYPE + " in(?, ?)", arrayOf("image/jpeg", "image/png"),
            MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        ) ?: return result

        val nameColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
        val pathColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA)
        val timeColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        val widthColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
        val heightColumn = imageCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)
        val idColumn =imageCursor.getColumnIndex(MediaStore.MediaColumns._ID)

        while (imageCursor.moveToNext()) {
            if (activity.isDestroyed) {
                break
            }
            val path = imageCursor.getString(pathColumn)
            path?.let {
                val file = File(it)
                if (file.exists() && file.isFile) {
                    val baseUri =
                        Uri.parse("content://media/external/images/media")
                    val name = imageCursor.getString(nameColumn)
                    val width = imageCursor.getInt(widthColumn)
                    val height = imageCursor.getInt(heightColumn)
                    val time = imageCursor.getLong(timeColumn)
                    val id = imageCursor.getInt(idColumn)

                    if (width >= MIN_IMAGE_WIDTH && height >= MIN_IMAGE_HEIGHT)
                        result.add(GalleryModel().apply {
                            this.name = name
                            this.path = path
                            this.uri =Uri.withAppendedPath(baseUri, "" + id)
                            this.width = width
                            this.height = height
                            this.time = time
                            this.type = GalleryModel.TYPE_IMAGE
                        })
                }
            }
        }
        imageCursor.close()
        return result
    }

}