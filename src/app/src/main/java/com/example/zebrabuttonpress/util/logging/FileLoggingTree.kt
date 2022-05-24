package com.example.zebrabuttonpress.util.logging

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree(private val context: Context) : Timber.DebugTree() {
    private val fileName: String = "applog"
    private val folderName: String = "AppLogs"
    private val logDateFormat: String = "MMM dd yyyy 'at' HH:mm:ss:SSS"

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val logTimeStamp = SimpleDateFormat(
                logDateFormat,
                Locale.getDefault()
            ).format(Date())

            // Create file
            val file = generateFile(context, fileName, folderName)

            // If file created or exists save logs
            if (file != null) {
                val writer = FileWriter(file, true)
                writer.appendLine("$logTimeStamp :$tag - $message")
                writer.flush()
                writer.close()
            }
        } catch (e: java.lang.Exception) {
            Log.e(
                LOG_TAG,
                "Error while logging into file : $e"
            )
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        // Add log statements line number to the log
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }

    companion object {
        private val LOG_TAG = FileLoggingTree::class.java.simpleName

        /*  Helper method to create file*/
        private fun generateFile(context: Context, fileName: String, folderName: String): File? {
            var file: File? = null
            if (isExternalStorageAvailable) {
                //File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                //        BuildConfig.APPLICATION_ID + File.separator + path);
                val root = File(context.filesDir, folderName)
                var dirExists = true
                if (!root.exists()) {
                    dirExists = root.mkdirs()
                }
                if (dirExists) {
                    file = File(root, fileName)
                }
            }
            return file
        }

        /* Helper method to determine if external storage is available*/
        private val isExternalStorageAvailable: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}