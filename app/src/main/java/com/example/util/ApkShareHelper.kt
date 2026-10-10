package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ApkShareHelper {

    /**
     * Extracts and shares the installed app's base APK file via Android's native share sheet.
     * Works with WhatsApp, Telegram, Drive, Bluetooth, Email, Nearby Share, etc.
     */
    fun shareInstalledApk(context: Context) {
        try {
            val appInfo = context.applicationInfo
            val sourceApk = File(appInfo.sourceDir)

            if (!sourceApk.exists()) {
                Toast.makeText(context, "APK file not found", Toast.LENGTH_SHORT).show()
                return
            }

            // Copy to app's cache directory with a user-friendly APK filename
            val apkCacheDir = File(context.cacheDir, "shared_apk")
            if (!apkCacheDir.exists()) {
                apkCacheDir.mkdirs()
            }

            val appName = context.getString(context.applicationInfo.labelRes).ifBlank { "Salesplus" }
            val cleanName = appName.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val targetFile = File(apkCacheDir, "${cleanName}.apk")

            // Copy file safely with streams
            FileInputStream(sourceApk).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            // Obtain secure content URI via FileProvider
            val authority = "${context.packageName}.fileprovider"
            val apkUri: Uri = FileProvider.getUriForFile(context, authority, targetFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, apkUri)
                putExtra(Intent.EXTRA_SUBJECT, "$appName APK")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Install $appName - Customer Retention & Sales App for Fashion Retailers."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share APK via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not share APK: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_LONG).show()
        }
    }
}
