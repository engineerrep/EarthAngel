package com.earth.angel.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.text.TextUtils
import android.util.Base64
import android.view.View
import com.earth.angel.base.EarthAngelApp
import java.io.*


object BitmapUtil {
    fun rotateBitmap(source: Bitmap, angle: Float, x: Int, y: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle, x.toFloat(), y.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun imageToBase64(path: String?): String? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        var `is`: InputStream? = null
        var data: ByteArray? = null
        var result: String? = null
        try {
            `is` = FileInputStream(path)
            //创建一个字符流大小的数组。
            data = ByteArray(`is`.available())
            //写入数组
            `is`.read(data)
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != `is`) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    @Throws(java.lang.Exception::class)
    fun decoderBase64File(base64Code: String?): String {
        val savePath = getSDPath() + "/" + System.currentTimeMillis().toString() + ".jpg"
        val buffer = Base64.decode(base64Code, Base64.DEFAULT)
        val out = FileOutputStream(savePath)
        out.write(buffer)
        out.close()
        return savePath
    }

    private fun getSDPath(): String {

        var sdDir: File? = null
        sdDir = EarthAngelApp.instance.getExternalFilesDir(null)
        return sdDir.toString()
    }

    //确认，生成图片
    fun confirm(view: View): String {
        val bm = loadBitmapFromView(view)
        val filePath: String =
            getSDPath() + File.separator.toString() + "/" + System.currentTimeMillis()
                .toString() + ".jpg"
        try {
            bm!!.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(filePath))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return  filePath
    }

    private fun loadBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}