package com.vahan.mitra_playstore.view.documentupload.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import com.vahan.mitra_playstore.view.BaseApplication
import java.io.File

class ImageUploadModel : ViewModel() {
    var imagePath = ""
    var bitmapDataImage: Bitmap? = null

    fun createNewFile(parentDir: File?) =
        File(parentDir, "IMG_${System.nanoTime()}").apply {
            imagePath = absoluteFile.toString()
        }

    fun createBitMap(uri: Uri): Bitmap {
        bitmapDataImage = MediaStore.Images.Media.getBitmap(
            BaseApplication.context?.contentResolver,
            uri
        ) as Bitmap

        return bitmapDataImage as Bitmap
    }
}