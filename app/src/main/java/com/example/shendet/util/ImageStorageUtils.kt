package com.example.shendet.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageUtils {
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        val fileName = "scan_${UUID.randomUUID()}.jpg"
        val directory = File(context.filesDir, "scans")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
