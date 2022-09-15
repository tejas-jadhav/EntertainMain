package com.example.entertainmain.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

suspend fun getBitmapFromUrl(
    url: String,
    context: Context
): Bitmap? {
    return try {
        Glide.with(context)
            .asBitmap()
            .override(250, 300)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .load(url)
            .submit()
            .get()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToExternalDirectory(
    bitmap: Bitmap,
    fileName: String
): String? {
    try {


        var savedImagePath: String? = null
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + "/favorites"
        )
        var success = true

        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }

        if (success) {
            val imageFile = File(storageDir, fileName)
            savedImagePath = imageFile.absolutePath
            return try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                savedImagePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    } catch (exception: Exception) {
        Log.e("MovieDetailsViewModel", "Exception occurred: ${exception.message}")
        return null
    }

    return null


}

