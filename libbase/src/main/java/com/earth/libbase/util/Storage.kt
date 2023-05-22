package com.earth.libbase.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log

import com.earth.libbase.R
import com.earth.libbase.base.BaseApplication.Companion.instance
import java.io.*


/**
 * 文件存储工具类
 */
object Storage {


    private const val TAG = "Storage"
    private val SDCARD_ROOT = getSDPath()

    private val DIR_HOME_HIDE = SDCARD_ROOT + File.separator +
            instance.resources.getString(R.string.base_app_name) + File.separator

    private val DIR_IMAGE = DIR_HOME_HIDE + "image" + File.separator              // 图片临时文件目录
    private val DIR_CRASH =
        DIR_HOME_HIDE + "crash" + File.separator                        // Crash日志目录
    private val DIR_LOG =
        DIR_HOME_HIDE + "log" + File.separator                            // 日志收集文件目录

    private const val TYPE_APP_HOME = 0

    const val TYPE_IMAGE = 1
    const val TYPE_CRASH = 2
    const val TYPE_LOG = 3
    const val TYPE_CAPTURE = 4

    private fun getSDPath(): String {

        var sdDir: File? = null
        sdDir =instance.getExternalFilesDir(null)
        return sdDir.toString()
    }


    private fun getPath(type: Int): String {

        var dir = ""

        when (type) {
            TYPE_APP_HOME -> {
                dir = createDir(DIR_HOME_HIDE)
                addNoMediaFile(DIR_HOME_HIDE)
            }

            TYPE_IMAGE -> {
                dir = createDir(DIR_IMAGE)
                addNoMediaFile(DIR_IMAGE)
            }

            TYPE_CRASH -> dir =
                    createDir(DIR_CRASH)
            TYPE_LOG -> dir =
                    createDir(DIR_LOG)

            else -> {
            }
        }

        return if (dir.endsWith(File.separator)) {
            dir
        } else {
            dir + File.separator
        }
    }

    private fun createDir(filePath: String): String {
        val file = File(filePath)
        if (!file.exists() || !file.isDirectory) {
            file.mkdirs()
        }
        return filePath
    }

    fun saveImage(bitmap: Bitmap): String {
        val path = getPath(TYPE_IMAGE) + createJpgName()
        return if(saveImage(bitmap, path)){
            path
        }else{
            ""
        }
    }

    private fun saveImage(bitmap: Bitmap, path: String, quality: Int = 100) :Boolean{
        var result = false
        val saveFile = File(path)
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(saveFile))
            val format =
                if (path.endsWith(".png")) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            bitmap.compress(format, quality, bos)
            bos.flush()
            Log.e(TAG, "Success==")
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "IOException==")
        } finally {
            try {
                bos?.close()
                var str=saveFile.length()
                LogUtils.info(str)
            } catch (e: IOException) {

            }
            return result
        }
    }

    /**
     * 添加.nomedia文件，避免指定目录下的图片和视频被系统扫描到图库中（也屏蔽了其他软件的扫描）
     *
     * @param path
     */
    private fun addNoMediaFile(path: String) {
        val file = File(path)
        try {
            if (!file.exists() || !file.isFile) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
        } catch (e: IOException) {

        }
    }

    private fun createJpgName(): String {
        return "IMG_" + System.currentTimeMillis() + ".jpg"
    }

    fun createFile(folder: File?, fileName: String):File{
        if(folder == null){
            return File(getSDPath())
        }
        if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    fun saveFile(fileName: String, content: String) {
        val file = File(fileName)
        val parent = file.parentFile
        var fos: FileOutputStream? = null
        try {
            if (!parent.exists()) parent.mkdirs()
            if (!file.exists()) file.createNewFile()
            fos = FileOutputStream(fileName)
            fos.write(content.toByteArray())
            fos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveFileImage(stringFile: String?): String {
        val bitmap= orientation(stringFile)
        val path = getPath(TYPE_IMAGE) + createJpgName()
        bitmap?.let {
            return if(saveImage(bitmap, path)){
                path
            }else{
                ""
            }
        }
        return ""
    }

    fun orientation(absolutePath: String?): Bitmap? {
        var bitmap_or = BitmapFactory.decodeFile(absolutePath)
        try {
            val exif = absolutePath?.let { ExifInterface(it) }
            val orientation: Int = exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90F)
            } else if (orientation == 3) {
                matrix.postRotate(180F)
            } else if (orientation == 8) {
                matrix.postRotate(270F)
            }
            bitmap_or = Bitmap.createBitmap(
                bitmap_or,
                0,
                0,
                bitmap_or.width,
                bitmap_or.height,
                matrix,
                true
            ) // rotating bitmap
            return bitmap_or
        } catch (e: java.lang.Exception) {
        }
        return null
    }
    // 通过uri加载图片
    fun getBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor =
                context.contentResolver.openFileDescriptor(uri!!, "r")!!
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    fun getImageGalleryFile(context: Context, bmp: Bitmap): File? {
        val storePath =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            val isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()
            //不通知相册更新
            //MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    bmp, fileName, null);
            return if (isSuccess) {
                file
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getFileSufix(name: String): String? {
        if(name.isNullOrBlank()){
            return null
        }
        return name.substring(name.lastIndexOf("."))
    }

}
