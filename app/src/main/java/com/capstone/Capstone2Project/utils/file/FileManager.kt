package com.capstone.Capstone2Project.utils.file

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.capstone.Capstone2Project.data.model.camera.InterviewVideo
import com.capstone.Capstone2Project.utils.etc.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileManager(private val context: Context) {

    //디렉토리 만들기
    private fun getPrivateFileDirectory(dir: String): File? {
        val directory = File(context.filesDir, dir)
        return if (directory.exists() || directory.mkdirs()) {
            directory
        } else null
    }

    //파일 만들기
    suspend fun createFile(directory: String, ext: String): String {
        return withContext(Dispatchers.IO) {
            val timestamp = SimpleDateFormat(
                FILE_TIMESTAMP_FORMAT,
                Locale.getDefault()
            ).format(System.currentTimeMillis())
            //child가 파일명이 될것
            return@withContext File(getPrivateFileDirectory(directory),"$timestamp.$ext").canonicalPath
        }
    }

    //외부 저장소에 비디오 저장하기
    suspend fun saveVideoToExternalStorage(
        displayName: String,
        videoUri: Uri
    ): Boolean  {
        return withContext(Dispatchers.IO) {
            val videoCollection = sdk29AndUp {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, "$displayName.mp4")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.DATE_TAKEN, "${System.currentTimeMillis()}" )
            }

            try {
                with(context.contentResolver) {

                    val uri = insert(videoCollection, contentValues)!!

                    /*
                    openFileDescriptor(uri, "w").use { pfd ->
                        FileOutputStream(pfd!!.fileDescriptor).use { outputStream ->
                            val bytes = videoUri.toFile().readBytes()

                            outputStream.write(bytes, 0, bytes.size)
                            outputStream.close()
                        }
                    }
                     */

                    openOutputStream(uri)!!.use { outputStream->
                        val bytes = videoUri.toFile().readBytes()
                        outputStream.write(bytes)
                        outputStream.close()
                    }
                }
                true
            } catch(e: Exception) {
                e.printStackTrace()
                false
            }


        }
    }

    //외부 저장소로부터 비디오 로드하기
    suspend fun loadVideosFromExternalStorage(): List<InterviewVideo> {
        return withContext(Dispatchers.IO) {

            val collection = sdk29AndUp {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN
            )

            val sortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

            val videos = mutableListOf<InterviewVideo>()

            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use {cursor->
                with(cursor) {
                    val idColumn = getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                    val displayNameColumn = getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                    val dateTakenColumn = getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

                    while(moveToNext()) {
                        val id = getLong(idColumn)
                        val displayName = getString(displayNameColumn)
                        val dateTaken = getLong(dateTakenColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        videos.add(InterviewVideo(id, displayName, contentUri, dateTaken))
                    }
                }
                videos.toList()
            } ?: emptyList()
        }

    }


    companion object {
        const val FILE_TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}